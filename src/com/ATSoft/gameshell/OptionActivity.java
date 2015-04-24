package com.ATSoft.gameshell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import com.ATSoft.GameFramework.Audio.BackgroundAudioService;
import com.ATSoft.GameFramework.Audio.SoundEffectsService;
import com.ATSoft.GameFramework.Util.GameResourceManager;
import com.ATSoft.GameFramework.Util.LoggerConfig;


public class OptionActivity extends ActionBarActivity {
    /**
     * Logging tag. Beginning of every class in this project,
     * or as close as I can manage it.
     */
    private final static String LOG="OptionActivity";

    /**
     * Local activity scope variable that stores
     * current music volume level. Pretty much obsolete
     * with new way of storing and passing that value
     * through system preferences, but still here for a time being.
     */
    private float _volume;

    /**
     * In sharp contrast with _volume, _context is a very important
     * activity scope local variable. Stores current application context.
     */
    private static Context _context;

    /**
     * Activity scope variables, used as a parameters in
     * calling activity for a result.
     * Result we want with RESULT_CODE_PLAYLIST : PlaylistID.
     * Result we want with RESULT_CODE_URL      : URL
     */
    private final int RESULT_CODE_PLAYLIST = 27;
    private final int RESULT_CODE_URL = 28;

    /**
     * If set, will indicate that user hit Home button.
     * Set to true in OnResume, set to false before stat of another activity.
     */
    private static boolean HOME_BUTTON_PRESSED = true;


    /**
     * OnCreate method of Activity class. Overridden.
     * This method have lot's to do to prepare and connect
     * user interface it exposes with core game settings,
     * and to track and respond to user actions.
     * @param savedInstanceState - Bundle created, stored and restored
     *                           by the system at this point.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Logging constant
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnCreate... entering.");
        }

        // First we pay tribute to android gods
        // and do what must be done...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // before we turn out attention to more important stuff, as:
        // - setting context as application context
        _context = this.getApplicationContext();

        // - setting shared preferences for an application
        final SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        // - and editor for those, both final so they can be seen from internal classes
        final SharedPreferences.Editor _edit = _preferences.edit();
        // - setting volume. We'll use system preferences to pass that value in and out.
        _volume = _preferences.getFloat("Volume", 1.0f);
        // - setting player play state. That we pass through preferences.
        boolean _isPlaying = ! _preferences.getBoolean("Play", false);
        // - and, for the end, store Playlist radio button into final local variable.
        // Why? So we can access it in following code.
        // Why that button, and not 2 other? Because at the moment only ot have complex implementation.
        // We'll easily create 2 more local variables here if we need them.
        final RadioButton _rb = (RadioButton) findViewById(R.id.radioButton_PLAYLIST);

        // After that, we are ready to do some real work.
        // First we need to select appropriate radio button...
        GameResourceManager.Music.PlaySource _musicSource = GameResourceManager.Music.getMusicSource();
        int _buttonID = 0;
        String _buttonToSelect = "";
        switch(_musicSource){
            case GAME:
                _buttonID = R.id.radioButton_GAME;
                _buttonToSelect ="Game";
                break;
            case PLAYLIST:
                _buttonID = R.id.radioButton_PLAYLIST;
                _buttonToSelect = "Playlist";
                break;
            case URL:
                _buttonID = R.id.radioButton_URL;
                _buttonToSelect = "Url";
                break;
        }
        // Now we have button id and text name of correct button
        // in _buttonID and _buttonToSelect variables.
        // Let's use them and set appropriate check state.
        if(LoggerConfig.ON) {
            Log.i(LOG, "Setting radio button checked state. Button to select: " + _buttonToSelect);
        }

        // Is the right button Playlist button?
        if(_buttonToSelect.equals("Playlist")) {

            // It is, so check it, and set onClick listener on it,
            // so we'll not pass click on selected option
            _rb.setChecked(true);
            _rb.setOnClickListener(null);
            _rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_rb.isChecked()){
                        // Set home button to false, otherwise
                        // it will close app
                        HOME_BUTTON_PRESSED = false;
                        if(LoggerConfig.ON) {
                            Log.i(LOG, "OnCheckedChanged... entering. Option selected: DEVICE PLAYLIST");
                        }
                        // ... and start activity for result.
                        Intent inty = new Intent(getApplicationContext(), playlistListActivity.class);
                        startActivityForResult(inty, RESULT_CODE_PLAYLIST);
                    }
                }
            });
        } else {
            // or just select correct button in other case.
            ((RadioButton) findViewById(_buttonID)).setChecked(true);
        }
        // Let's make sure that we can show all options,
        // by checking if there are play lists stored into external memory.
        // _playListCursor - iterator through play lists.
        Cursor _playListCursor = null;

        // No one can be sure what will occurs in database operations
        // so try / catch block is in place.
        try {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Retrieve play lists... entering.");
            }
            String[] _criteria = {"*"};
            Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            _playListCursor = getContentResolver().query(tempPlaylistURI, _criteria, null, null, null);

            if (_playListCursor == null) {
                // There is no play lists, so
                if(LoggerConfig.ON) {
                    Log.i(LOG, "No play lists on device. Disabling PLAY LISTS option.");
                }
                // option Play lists can't be enabled,
                findViewById(R.id.radioButton_PLAYLIST).setEnabled(false);
            } else {
                // but since we found at least one play list,
                if(LoggerConfig.ON) {
                    Log.i(LOG, "Play lists found on device. Enabling PLAY LISTS option.");
                }
                // option Play List is enabled.
                findViewById(R.id.radioButton_PLAYLIST).setEnabled(true);
            }
        } catch (Exception e){
            if(LoggerConfig.ON) {
                Log.e(LOG, "Error in retrieve play lists, " + e.getMessage());
            }
        } finally {
            // Here we are not concerned with what happened before hand.
            // All we care about at this point is that we need to close database cursor object.
            if(_playListCursor != null)
                    _playListCursor.close();
        }

        // It is good time to store references to
        // volume Seek Bar and Mute button to local variables,
        final SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        final ImageButton _mute = (ImageButton) findViewById(R.id.muteButton);

        // and to set seek bar progress accordingly to volume level...
        sb.setProgress((int)(_volume * 100.0f));

        // as well as selected state of Mute button,...
        _mute.setSelected(_isPlaying);

        // ... set listener on Volume SeekBar,...
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Implementation of OnSeekBarChangeListener needs implementation of
            // 3 methods: OnStartTrackingTouch, OnStopTrackingTouch and OnProgressChange.
            // For a time being we are not interested into first 2, so we'll
            // implement them with empty block.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(LoggerConfig.ON) {
                    Log.i(LOG, "OnStopTrackingTouch... entering.");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(LoggerConfig.ON) {
                    Log.i(LOG, "OnStartTrackingTouch... entering.");
                }
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(LoggerConfig.ON) {
                    Log.i(LOG, String.format("OnVolumeSeekBarProgressChanged... entering. Volume: %d", progress));
                }

                // However,OnProgressChange is a event that interests us deeply.
                // We'll store current progress into _volume variable,
                _volume = progress / 100.0f;

                // as well as into System preferences database.
                _edit.putFloat("Volume", _volume);
                _edit.apply();

                // and let's check if there is possibility for sound to be heard.
                // Mute button have reversed logic, it turns music off
                // rather then turning it on, so we'll check for not selected state
                if(!_mute.isSelected()){
                    // Music can play, that's ok, but we make this for humans,
                    // not dogs or superheroes, so let's check actual volume
                    if(_volume<0.05){
                        // It is definitely too low to be heard, so we'll turn it off completely,...
                        _volume = 0.0f;
                        // ... store change to System preference database,...
                        _edit.putFloat("Volume", _volume);
                        _edit.putBoolean("Play", false);
                        _edit.apply();
                        // ... set selected state of Mute button to true (remember, reversed logic),...
                        _mute.setSelected(true);
                        // ... and call Pause function of GRM.Music class.
                        startService(GameResourceManager.Music.Pause());
                    } else {
                        // At this point we found that we can:
                        // 1. Play music and
                        // 2. We can play it as lout or quiet as we want,
                        // so let's do that by calling SetVolume function of GRM.Music.
                        startService(GameResourceManager.Music.SetVolume());
                    }
                } else {
                    // At this point we can't play at all, but user moved
                    // volume control, so it is sound to presume that he wants
                    // to hear something. To allow him to do so, we'll:
                    // 1. Store that music is playing into database
                    _edit.putBoolean("Play", true);
                    _edit.apply();
                    // 2. Play actual music by calling Play function of GRM.Music
                    startService(GameResourceManager.Music.Play());
                    // and 3. Select appropriate state of Mute button.
                    _mute.setSelected(false);
                }

            }
        });

        // ... on _mute button, ...
        _mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoggerConfig.ON) {
                    Log.i(LOG, "OnImageButtonClick... entering.");
                }
                // View passed by listener to this class is Image button,
                // so let's recognize that right away
                ImageButton _mb = (ImageButton) view;

                // and change it's selected state to opposite of it's current state.
                _mb.setSelected(!_mb.isSelected());

                // Now we have time to look for what user wanted, and do either
                if(_mb.isSelected()){
                    // a) turn music off, by storing appropriate value into database ...
                    _edit.putBoolean("Play", false);
                    _edit.apply();
                    // ... and calling right function of GRM.Music, or
                    startService(GameResourceManager.Music.Pause());
                } else {
                    // b) Turn music on, doing exactly the same as in case a,
                    // but with different, more appropriate value to store
                    // and function to call.
                    _edit.putBoolean("Play", true);
                    _edit.apply();
                    startService(GameResourceManager.Music.Play());
                }
            }
        });

        // ... and on MusicSource radio group.
        // At this point we'll follow selection change,
        // and play with RadioButtonPlayList OnclickListener.
        // Just for remember, if at this point that button is selected,
        // it also have OnClickListener attached.
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupMusicSource);

       // We'll set actual OnCheckedChangeListener ...
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int _id) {

                // ... to begin listen.
                if(_id == R.id.radioButton_GAME){

                    // We heard: GAME option is selected one, so...
                    if(LoggerConfig.ON) {
                        Log.i(LOG, "OnCheckedChanged... entering. Option selected: GAME MUSIC");
                    }
                    // ... we'll set appropriate fields of GMR.Music class, as music source...
                    GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.GAME);
                    // ... and resource to play, ...
                    GameResourceManager.Music.setResourceId(R.raw.game_music);

                    // ... and store current choice into database.
                    int _intSource = GameResourceManager.Music.PlaySource.GAME.ordinal();
                    _edit.putInt("Source", _intSource);
                    _edit.putInt("ResourceID", R.raw.game_music);
                    _edit.apply();

                    // We'll also remove OnClickListener from Playlist radio button,
                    // because next click on it will trigger this handler,
                    // so we'll handle it from here,
                    _rb.setOnClickListener(null);

                    // and start play selected source by calling helper function playSelected.
                    playSelected();

                } else if(_id == R.id.radioButton_PLAYLIST){

                        // We heard: PLAYLIST option is selected one, so...
                        if(LoggerConfig.ON) {
                            Log.i(LOG, "OnCheckedChanged... entering. Option selected: DEVICE PLAYLIST");
                        }
                        // ... for start we'll set any existing OnClickListener to null,
                        // because at this point we don't know if it have none, one or many of them,
                        // and we want only one, ...
                        _rb.setOnClickListener(null);

                        // ... so we'll set it:
                        _rb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            // First check if option is selected:
                            if(_rb.isChecked()){

                                // and be sure that it is 99,999% of cases this will be hit.
                                // So we'll set flag that it is not user that caused leave
                                // from this activity, at least not directly, ...
                                HOME_BUTTON_PRESSED = false;

                                if(LoggerConfig.ON) {
                                    Log.i(LOG, "OnCheckedChanged... entering. Option selected: DEVICE PLAYLIST");
                                }
                                // ... and create intent that will start activity which will return
                                // play list identification to us, so we'll call startActivityForResult.
                                // Here we'll use that RESULT_CODE_PLAYLIST constant, still 27. Why? Why not?
                                Intent inty = new Intent(getApplicationContext(), playlistListActivity.class);
                                startActivityForResult(inty, RESULT_CODE_PLAYLIST);
                            }
                        }
                    });
                    } else if(_id == R.id.radioButton_URL){

                        // We heard: URL option is selected one, so...
                        if(LoggerConfig.ON) {
                            Log.i(LOG, "OnCheckedChanged... entering. Option selected: URL");
                        }
                        // we'll do exactly the same as with Game option,
                        // but with an appropriate values for options
                        GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.URL);

                        // this is a place that we'll need activity that will list URL's,
                        // but for now we'll use just URL stored into GMR.Music.
                        //
                        // TODO: implement start activity for result URL
                        //
                        int _reqCode = RESULT_CODE_URL;
                        String _uri = GameResourceManager.Music.getUriToPlay();

                        // Rest is already seen: remove listener from playlist button,
                        _rb.setOnClickListener(null);

                        // store changes into database
                        int _intSource = GameResourceManager.Music.PlaySource.URL.ordinal();
                        _edit.putInt("Source", _intSource);
                        _edit.putString("URI", _uri);
                        _edit.apply();

                        // and call helper that will do what is needed to play music.
                        playSelected();
                    }
                 }
        });

        // And with that we hit the end of this quite a long method.
        // Which is good.
        // Not so good is certainty that it will become longer for option that
        // will deal with sound effects and graphic, but for now, this is a
        // THE END of this method.
    }

    /**
     * Helper function that will start playing selected source
     */
    private void playSelected(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "PlaySelected... entering.");
        }
        // To do so, firs stop current playing, ...
        startService(GameResourceManager.Music.Stop());

        // ... get if user wants music...
        SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        boolean _isPlaying = _preferences.getBoolean("Play", false);
        if(_isPlaying)
            // ... and if YES, play.
            startService(GameResourceManager.Music.Play());
     }

    /**
     * Function that will be called on activity leave
     */
    @Override
    protected void onUserLeaveHint(){
        // If HOME_BUTTON_PRESSED is true, user initiate leave activity action, so...
        if(HOME_BUTTON_PRESSED) {
            if(LoggerConfig.ON) {
                Log.i(LOG, "OnUserLeaveHint... entering. User hit HOME button.");
            }
            // ... we'll call clear service helper function ...

            startService(GameResourceManager.Music.Stop());
            clearServices();

            // ... and create new intent toward home activity ...
            Intent intent = new Intent(getApplicationContext(), startActivity.class);
            // with added flags that will indicate user wish to exit, and call start activity on that.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            intent.putExtra("SENDER", "OptionActivity");
            startActivity(intent);
            // Point on i, call finish to end this activity.
            /*int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);*/
        }
    }

    /**
     * Helper function that stops background services
     */
    private void clearServices(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "ClearServices... entering.");
        }
        // At the moment, we have 2 services running, so
        // we'll send DESTROY and STOP command to both of them.
        //startService(GameResourceManager.Music.SetDestroy());
        Intent inty = new Intent(this, BackgroundAudioService.class);
        stopService(inty);

        //startService(GameResourceManager.SoundEffects.Destroy());
        Intent inty1 = new Intent(this, SoundEffectsService.class);
        stopService(inty1);
    }

    /**
     * OnActivityResult Callback function.
     * This is a point where data from child activities are
     * collected and processed.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "Activity returned result and called OnActivityResult... entering.");
        }
        // For now we have just PlaylistID as entry, but it will
        // change soon to include URL. Regardless, logic and implementation are the same.
        if(requestCode == RESULT_CODE_PLAYLIST && resultCode == Activity.RESULT_OK) {
            // It is PlaylistID we got, checked,...
            String _id = data.getStringExtra("PlaylistID");
            if(_id != null){
                if(LoggerConfig.ON) {
                    Log.i(LOG, "PlaylistID is passed in.");
                }
                // ... and rechecked, so we can set it as valid source,...
                long _playlistId = Long.parseLong(_id);
                GameResourceManager.Music.setPlayListId(_playlistId);
                GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.PLAYLIST);

                // ... update database to correct values,...
                SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
                SharedPreferences.Editor _edit = _preferences.edit();
                int _intSource = GameResourceManager.Music.PlaySource.PLAYLIST.ordinal();
                _edit.putInt("Source", _intSource);
                _edit.putLong("PlayListID", _playlistId);
                _edit.apply();

                // ... and call helper to play chosen playlist.
                playSelected();
            }
        }
        // Here we'll handle URL in same manner.
        if(requestCode == RESULT_CODE_URL && resultCode == Activity.RESULT_OK) {
            if(LoggerConfig.ON) {
                Log.i(LOG, "URL string is passed in.");
            }
            //Implementation will follow.
            //
            // TODO: Implement URL select callback
            //
        }
    }

    /**
     * Here we'll play a little game which will
     * eventually allow us to pass Volume level back to main screen
     * @param keyCode - int, representing code of a pressed key
     * @param event - KeyEvent, gives more data for press key event
     * @return - boolean, in this case true if back is pressed
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "onKeyDown... entering.");
        }
        // Check if it is right code...
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // ... and if it is, set that we leave activity from reason
            // different then UserHitHomeButton,...
            HOME_BUTTON_PRESSED = false;
            // create new intent and pass it on,...
            Intent inty = new Intent(this, startActivity.class);
            inty.putExtra("SENDER", "OptionActivity");
            inty.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(inty);
            // ... and finish this activity.
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
     * Handler for menu button pressed event(s)
     * @param item - pressed menu item
     * @return - true, we'll handle it here. Or we wont, but it is something different :)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnOptionsItemSelected... entering.");
        }
        // This piece of code broke app yesterday. Let's see it with
        // additional setting for HOME_BUTTON_PRESSED
        HOME_BUTTON_PRESSED = false;
        Intent inty = new Intent(this, startActivity.class);
        inty.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(inty);
        finish();

        return true;
    }

    /**
     * OnResume method
     */
    @Override
    public void onResume(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnResume... entering.");
        }
        // Here we'll reset HOME_BUTTON_PRESSED to true...
        HOME_BUTTON_PRESSED = true;

        // ... and pass it to base class.
        super.onResume();
    }

    /**
     * OnPause method
     */
    @Override
    public void onPause(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnPause... entering.");
        }
        // We'll just pass it to base class.
        super.onPause();
    }

}
