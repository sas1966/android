package com.ATSoft.gameshell;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ATSoft.GameFramework.Audio.BackgroundAudioService;
import com.ATSoft.GameFramework.Audio.SoundEffectsService;
import com.ATSoft.GameFramework.Command.GameManager;
import com.ATSoft.GameFramework.Util.BroadcastReceiver_Service;
import com.ATSoft.GameFramework.Util.GameResourceManager;
import com.ATSoft.GameFramework.Util.LoggerConfig;
import com.ATSoft.gameshell.Levels.Graphics.SurfaceRenderer;
import com.ATSoft.gameshell.Levels.Graphics.SurfaceView;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity extends Activity {
	/**
     * LOG tag for activity
     */
    private static final String LOG = "StartActivity";

    /**
     * If set, will indicate that user hit Home button
     * Set to true in OnResume, set to false before stat of another activity.
     */
    private static boolean HOME_BUTTON_PRESSED = true;

    /**
     * Default uri
     */
    private static final String _defaultUriToPlay = "http://rts.ipradio.rs:8006";


    /**
     * Activity level private fields.
     * BroadCastReceiver and Intent filter may not be here for long.
     */
    private BroadcastReceiver_Service _broadcastReceiverService;
    private IntentFilter _iFilter;

    /**
     * SurfaceView. From Levels/Graphic.
     * Extends GLSurfaceView via GameFramework/BaseGLSurfaceView.
     */
    SurfaceView _thisSurfaceView;
    SurfaceRenderer _surfaceRenderer;
    boolean _rendererSet;

    /**
     * OnCreateActivity.
     * @param savedInstanceState - android framework stuff.
     *                           I'm afraid that i'll need to intervene with it
     *                           to get equal behaviour of OnResume when activity was
     *                           exited with home and back button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnCreate {{ *** }}  ... entering.");
        }
        Intent _startingIntent = this.getIntent();
        String _sender = _startingIntent.getStringExtra("SENDER");
        Boolean _isExit = _startingIntent.getBooleanExtra("EXIT", false);

        // Classic start of OnCreate
        super.onCreate(savedInstanceState);
        _thisSurfaceView = new SurfaceView(this);

        // For now we'll comment next line. Goal is to set OpenGL surface view.
        //setContentView(R.layout.activity_start);

        // And now we get astray. If intent that started activity
        // have Boolean.Extra "EXIT" in it, it is time to go out, but before we do that,...
        if (getIntent().getBooleanExtra("EXIT", false)) {
            // ... we'll need to stop running services by calling
            // helper clearServices(),...
            clearServices();

            // and after that, we can kill main thread.
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
        }

        //
        // Here we'll set OpenGL View
        //
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            _thisSurfaceView.setEGLContextClientVersion(2);
            // Create our renderer.
            _surfaceRenderer = new SurfaceRenderer(this);
            //
            // TODO: inject dependencies into _surfaceRenderer
            //
            // For now, we'll just mock them.



            _thisSurfaceView.setRenderer(_surfaceRenderer);
            _rendererSet = true;
            mockRendererData();
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        // Set environmental variables...
        @SuppressWarnings({"unused"})
        GameResourceManager _gameResourceManager = GameResourceManager.getGameResourceManager(this.getApplicationContext());
        _broadcastReceiverService = new BroadcastReceiver_Service();
        _iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        ProgressDialog _pd = new ProgressDialog(MainActivity.this);
        _pd.setTitle("Loading");
        _pd.setMessage("Please wait...");
        _pd.setMax(100);
        _pd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;
            }

        });


        GameManager _mainGameManager = GameManager.getGameManager(this);
        _mainGameManager.setProgressDialog(_pd);
        _mainGameManager.setGameState(GameManager.GameState.LOADING);

        // ... and service IsDestroyed variables,...
        BackgroundAudioService.setDestroyed(false);
        SoundEffectsService.setDestroyed(false);

        // ... as well the animation sequences... (to be implemented)
        //
        // TODO: store animation sequences to memory
        //

        //
        // Here We'll set view to our Surface view.
        //
        setContentView(_thisSurfaceView);
    }

    /**
     * OnPause method.
     */
    @Override
    protected void onPause(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "StartActivity OnPause... entering.");
        }
        // Unregister broadcast receiver HEADS_PLUG...
        try{
            unregisterReceiver(_broadcastReceiverService);
        } catch (Exception e) {}

        // ... and pass event to base class.
        super.onPause();

        if (_rendererSet) {
            _thisSurfaceView.onPause();
        }
    }

    @Override
    protected void onStop(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "StartActivity OnStop... entering.");
        }
        // Unregister broadcast receiver HEADS_PLUG...
        try{
        unregisterReceiver(_broadcastReceiverService);
        } catch (Exception e) {}

        // ... and pass event to base class.
        super.onStop();
    }

    /**
     * OnResume method.
     */
    @Override
    protected void onResume(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "StartActivity OnResume... entering.");
        }
        Intent _startingIntent = this.getIntent();
        String _sender = _startingIntent.getStringExtra("SENDER");
        Boolean _isExit = _startingIntent.getBooleanExtra("EXIT", false);

        // Reset value of HOME_BUTTON_PRESSED to true; if event occurs,
        // it will be most probable initiated by user, so this is right value.
        HOME_BUTTON_PRESSED = true;

        // We'll call helper method to check state of
        // backgrounds services,...
        SynchronizePreferences();

        // ... and register HEAD_PLUG event receiver...
        registerReceiver(_broadcastReceiverService, _iFilter);


        // ... before we pass event to base class.
        super.onResume();

        if (_rendererSet) {
            _thisSurfaceView.onResume();
        }
    }


    /**
     * Helper method for maintain correct state
     * of SystemPreferences.
     */
    public void SynchronizePreferences(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "SynchronizePreferences... entering.");
        }
        // Get reference to SharedPreferences...
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        // ... and check if this is first run.
        boolean _valueIsSet = _preferences.getBoolean("Value_Set", false);
        if(_valueIsSet){
            // If it is not, get info about Services state
            // and set Services accordingly.
            boolean _isPlaying = _preferences.getBoolean("Play", false);
            int _sourceOrdinal = _preferences.getInt("Source", 0);
            GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.values()[_sourceOrdinal]);
            int _resourceID = _preferences.getInt("ResourceID", R.raw.game_music);
            GameResourceManager.Music.setResourceId(_resourceID);
            long _playListID = _preferences.getLong("PlayListID", -1);
            GameResourceManager.Music.setPlayListId(_playListID);
            String _uriToPlay = _preferences.getString("URI", _defaultUriToPlay);
            GameResourceManager.Music.setUriString(_uriToPlay);

            // At the moment, it is all about Music class, but
            //
            // TODO: get settings for SoundEffects and Animation Services
            //

            // After setting, start Music, unless it was muted.
            if(_isPlaying)
                startService(GameResourceManager.Music.Play());
        } else {
            // And here it is, so we'll do some initial settings
            // to GRM.Music...
            GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.GAME);

            // ... and store them to database.
            SharedPreferences.Editor editor = _preferences.edit();
            editor.putBoolean("Value_Set", true);
            editor.putBoolean("Play", true);
            editor.putFloat("Volume", 1.0f);
            int _intSource = GameResourceManager.Music.getMusicSource().ordinal();
            editor.putInt("Source", _intSource);
            editor.putInt("ResourceID", R.raw.game_music);
            editor.putLong("PlayListID", GameResourceManager.Music.getPlayListID());
            editor.putString("URI", GameResourceManager.Music.getUriToPlay());
            editor.apply();
            //
            // Same as for get option, it is all about GRM.Music so far.
            //
            // TODO: Store initial settings for SoundEffects and Animation classes here.
            //

            // At the end, it is time to play some default music.
            startService(GameResourceManager.Music.Play());
        }
    }

    /**
     * OnPostCreateMethod.
     * @param savedInstanceState - Bundle created in OnSavedInstanceStateCreate.
     *                             At the moment, completely machine creation, but as
     *                             I said, I'm afraid that I'll need to intervene in that.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "StartActivity OnPPostCreate... entering.");
        }
        // Pass event to base class...
        super.onPostCreate(savedInstanceState);

        // call helper to store sound effects to pool,...
        storeEffectsToPool();

    }

    /**
     * OnDestroy method.
     * Since we're playing with exit from various methods,
     * and app as well, this is in order to increase safety.
     */
    @Override
    protected void onDestroy(){
        // Just pass event to base class.
        super.onDestroy();
    }

    /*
        Now is a right time to discuss ways of possible exits
        out of application. We are at the start screen, into
        start activity at the moment.
        User can choose to go to some other activity. That case will
        be indicated with HOME_BUTTON_PRESSED = false state.
        Then he may decide to exit application by hitting
        HOME button. That case will be indicated with
        HOME_BUTTON_PRESSED = true state.
        At the end, he may decide to hit BACK button,
        and in that case we'll intercept and handle key_down event.
        Different paths require different approaches, which will be
        discussed at right time.
     */

    /**
     * Overrides OnBackPressed.
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // cleanup app, save preferences, etc.
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(HOME_BUTTON_PRESSED) {
                // ... to choose that HOME button was hit
                // and that we need to shut down services before exit.
                try {
                    unregisterReceiver(_broadcastReceiverService);
                } catch (Exception e) {
                }
                startService(GameResourceManager.Music.Stop());
                clearServices();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * But we need this also for our little trick to work
     **/
    @Override
    public void onBackPressed(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "onBackPressed... entering.");
        }
        // Here we'll do nothing. Only purpose for this method
        // is to prevent system to do something about OnBackPressed event.
    }


    /**
     * Overrides OnUserLeaveHint.
     * Event occurs every time that user leaves activity.
     */
    @Override
    protected void onUserLeaveHint(){
        // So we'll make use of state variable ...
        if(HOME_BUTTON_PRESSED) {
            // ... to choose that HOME button was hit
            // and that we need to shut down services before exit.
            try{
                unregisterReceiver(_broadcastReceiverService);
            } catch (Exception e) {}
            startService(GameResourceManager.Music.Stop());
            clearServices();

            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            // There is nothing else left beyond this point in app life cycle.
            // Control is returned to device operating system.
        }
        // ... or leave user go to another activity
        // inside application.
    }

    /**
     * Helper method ClearServices.
     * Stops all running services.
     */
    private void clearServices() {
        // Stops services one by one.
        // One.
        Intent inty = new Intent(this, BackgroundAudioService.class);
        stopService(inty);
        // Two.
        Intent inty1 = new Intent(this, SoundEffectsService.class);
        stopService(inty1);
        // Three. When there are three services running.
        //
        // If there would be more, they stop commands will be above.
    }

    /**
     * Helper method AskForExit.
     * Create intent on main activity, add it Intent.FLAG_ACTIVITY_CLEAR_TOP flag
     * and EXIT = True boolean, and let main activity OnCreate to exit.
     */
    private void askForExit(){
        Intent intent = new Intent(this.getApplicationContext(), startActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        intent.putExtra("SENDER", "StartActivity");
        this.startActivity(intent);
    }


    /**
     * Option button handler.
     * @param view - (Button)View, passed as argument to handler
     *               when OnClick event occurs.
     */
    public void onDummyButtonTouch(View view){
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnDummyButtonTouch... entering.");
        }
        // Set flag that we want to leave activity...
        HOME_BUTTON_PRESSED = false;

        // ... and go to another one.
        Intent inty = new Intent(this, OptionActivity.class);
        inty.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        inty.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(inty);
    }

    /**
     * Helper method, for test purposes at the moment.
     * @return - ArrayList<Integer>, populated with resource
     *           numbers of desired effects.
     */
    private ArrayList<Integer> prepareSoundEffects(){
        ArrayList<Integer> _result = new ArrayList<Integer>();
        _result.add(R.raw.blop_new);
        _result.add(R.raw.bomb_exploding_new);
        _result.add(R.raw.woosh_new);
        return _result;
    }

    /**
     * Helper method. Can go to production classes. Here as test method.
     * Loads effects stored into ArrayList<ResourceId>
     * into SoundPool files.
     */
    public void storeEffectsToPool(){
        ArrayList<Integer> _effects = prepareSoundEffects();
        for(int i : _effects){

            startService(GameResourceManager.SoundEffects.LoadFile(i));
        }
    }

    /**
     * Helper method.  Can go to production classes. Here as test method.
     * @param ints - Resource number(s) of desired effects.
     *               Call play of desired effects. Assumes that before
     *               it starts, effects are already loaded into pool.
     */
    private void playEffect(int... ints){
        for(int i : ints){
            startService(GameResourceManager.SoundEffects.Play(i));
        }
    }

    //
    // region Test Buttons Handlers
    //
    /**
     * Button click handlers. For test purposes only.
     * @param view - (Button)View, passed as argument to handler
     *               when OnClick event occurs.
     */
    public void button1ClickListener(View view){
        playEffect(R.raw.blop_new);
    }

    public void button2ClickListener(View view){
        playEffect(R.raw.bomb_exploding_new);
    }

    public void button3ClickListener(View view){
        playEffect(R.raw.woosh_new);
    }

    public void button4ClickListener(View view){
        playEffect(R.raw.blop_new, R.raw.bomb_exploding_new);
    }

    public void button5ClickListener(View view){
        playEffect(R.raw.bomb_exploding_new, R.raw.woosh_new);
    }

    public void button6ClickListener(View view){
        playEffect(R.raw.blop_new, R.raw.bomb_exploding_new, R.raw.woosh_new);
    }
    //
    // end region Test Buttons Handlers
    //

    //
    // region mockRendererContextBEGIN
    //

    void mockRendererData(){
        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.8f, -0.8f,
                0.8f, 0.8f,
                -0.8f, 0.8f,
                // Triangle 2
                -0.8f, -0.8f,
                0.8f, -0.8f,
                0.8f, 0.8f,
                // Line 1
                -0.8f, 0f,
                0.8f, 0f,
                // Mallets
                0f, -0.5f,
                0f, 0.5f
        };
        _surfaceRenderer.setVertices(tableVerticesWithTriangles);

        _surfaceRenderer.setVertexShaders(R.raw.simpe_vertex_shader);
        _surfaceRenderer.setFragmentShaders(R.raw.simple_fragment_shader);

    }

    //
    // region mockRendererContext END
    //


    //
    // region test helpers
    //

    protected <F> F getPrivateField( String fieldName, Object obj)
            throws NoSuchFieldException, IllegalAccessException {
        Field field =
                obj.getClass().getDeclaredField( fieldName );

        field.setAccessible( true );
        return (F)field.get(obj);
    }

    //
    // end region Test Helpers
    //

}
