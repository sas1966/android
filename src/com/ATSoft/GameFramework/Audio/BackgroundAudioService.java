package com.ATSoft.GameFramework.Audio;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import com.ATSoft.GameFramework.Util.GameResourceManager;
import com.ATSoft.GameFramework.Util.LoggerConfig;

import java.io.IOException;
import java.util.ArrayList;

// import android.net.wifi.WifiManager;


/**
 * AT Soft
 *
 * class BackgroundAudioService
 * all related to game background music is gathered here.
 *
 * Created by Aleksandar on 27.3.2015..
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class BackgroundAudioService extends Service implements OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    /**
     * Define LOG constant string for class
     */
    private final static String LOG = "BackgroundAudioService";

    /**
     *  SharedPreferences
     */
    private static SharedPreferences _preferences = PreferenceManager.getDefaultSharedPreferences(GameResourceManager.getApplicationContext());

    /**
     * Boolean _destroyed.
     * in case that some thread try to start service again
     * after it is destroyed.
     */
    private static boolean _destroyed = false;
    public static void setDestroyed(boolean value) { _destroyed = value; }
    public static boolean getDestroyed() { return _destroyed; }

    /**
     * MediaPlayer _mPlayer
     * main, and only, player in class
     */
    private static MediaPlayer _mPlayer;

    /**
     * ArrayList(String) _songsToPlay
     * keeps list of songs for playing
     *
     * int _positionInList
     * keeps current track number
     */
    private ArrayList<String> _songsToPlay;
    private int _positionInList;

    /**
     * IBinder _mBinder
     * unused. Here just to allow implementation of Service super class.
     *
     * AudioManager _audioManager
     * unused. Here to allow further expansions and functionality.
     *
     * WiFiManager _wiFiManager
     * unused. Here to allow further expansions and functionality.
     */
    private IBinder _mBinder = null;

    /**
     * Property that exposes if _mediaPlayer is playing.
     */
    public boolean isPlaying() {
        return _mPlayer!=null && _mPlayer.isPlaying();
    }

    /**
     * Property that exposes if _mediaPlayer is in looping mode.
     */
    public boolean isLooping(){
        return _mPlayer!=null && _mPlayer.isLooping();
    }



    /**
     * TEST VARIABLES
     */
    public static int _testOnCreate = 0;
    public static int _testOnBind = 0;
    public static int _testOnCommand = 0;
    public static int _testOnStop = 0;
    public static int _testOnDestroy = 0;

    /**
     * Demanded by Service superclass
     * @param newIntent - Intent
     * @return - IBinder
     * At the moment, equivalent to @return null.
     */
    @Override
    public IBinder onBind(Intent newIntent){
        if(LoggerConfig.ON) {
            Log.i(LOG, "onBind entering...");
        }
        _testOnBind++;
        return _mBinder;
    }


    /**
     * Should be first method of the class to be called,
     * but so far I didn't noticed that.
     * Used to instantiate local instances of AudioManager and WiFiManager.
     * Since they aren't used at the moment, it is OK for now,
     * but it can become nuisance in the future.
     */
    //  BACKGROUND AUDIO SERVICE
    @Override
    public void onCreate(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "onCreate entering...");
        }
        _testOnCreate++;
        if(_destroyed) return;
        super.onCreate();
        /*AudioManager _audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        WifiManager _wiFiManager = (WifiManager)getSystemService(WIFI_SERVICE);*/
    }

    /**
     * Main crossroad for services class gives
     * @param intent - Intent object that caries Action for class to execute
     * @param flags - not used in this implementation
     * @param startId - not used in this implementation
     * @return - int member of Service Enum. Return value not used.
     */
    //  BACKGROUND AUDIO SERVICE
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "onStartCommand entering...");
        }
        _testOnCommand++;
        try {
            if (_destroyed) {
                return -1;
            }
            String _action = intent.getAction();
            if (_action.equals("com.ATSoft.GameResourceManager.Music.ActionSetVolume")) {
                setVolume();
            } else if (_action.equals("com.ATSoft.GameResourceManager.Music.ActionPlay")) {
                play();
            } else if (_action.equals("com.ATSoft.GameResourceManager.Music.ActionPause")) {
                pause();
            } else if (_action.equals("com.ATSoft.GameResourceManager.Music.ActionStop")) {
                stop();
            } else if (_action.equals("com.ATSoft.GameResourceManager.Music.ActionDestroy")) {
                destroy();
            }
            return Service.START_NOT_STICKY;
        } catch(Exception e){
            return -1;
        }
    }

    /**
     * SetVolume Action implementation
     */
    private void setVolume(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "setVolume...entering.");
        }
        try {
            float vol = _preferences.getFloat("Volume", 1.0f);
            if (_mPlayer != null) {
                _mPlayer.setVolume(vol, vol);
            }
        } catch (Exception e) {}
    }

    /**
     * Play Action implementation
     */
    private void play(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "play... entering.");
        }
        try{
            if(_mPlayer == null)  initializePlayer();

            if(GameResourceManager.Music.getMusicSource() != GameResourceManager.Music.PlaySource.PLAYLIST){
                // For options that have just 1 track simple call start on MediaPlayer
                if(!isPlaying()) {
                    setVolume();
                    _mPlayer.start();
                }
            } else {
                // For potential multi track case, call method to set all correct
                if(!isPlaying()) {
                    _positionInList = 0;
                    _mPlayer.setOnCompletionListener(this);
                    playList(_positionInList);
                }
            }
        } catch (IllegalStateException e) {
            _mPlayer.release();
            _mPlayer = null;
            play();
        } catch (Exception e){
            if(LoggerConfig.ON) {
                Log.e(LOG, "Play ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Play song at i-th place in play list
     * @param i - position of song in a play list
     */
    private void playList(int i){
        if(LoggerConfig.ON) {
            Log.i(LOG, "PlayList playing... entering. No of song: " + i);
        }
        try{
            if(isPlaying()){
                _mPlayer.stop();
                _mPlayer.reset();
            }
            _mPlayer.setDataSource(_songsToPlay.get(i));
            _mPlayer.prepare();
            setVolume();
            _mPlayer.start();
        } catch (IOException e) {
            if(LoggerConfig.ON) {
                Log.e(LOG, "No such file: " + e.getMessage());
            }
        }
        catch (Exception e) {
            if(LoggerConfig.ON) {
                Log.e(LOG, "File found, but something else got wrong: " + e.getMessage());
            }
        }
    }

    /**
     * Implementation of OnCompletionListener interface
     * @param mp - MediaPlayer that just finished playing track
     * Decide if there is a next track to play, destroys current instance of MediaPlayer
     * and eventually plays next track on NEW instance of MediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mp){
        if(LoggerConfig.ON) {
            Log.i(LOG, "onCompletion... entering.");
        }
        // Increase position in list
        _positionInList++;
        // Check if it is points to position outside list
        if(_positionInList >= _songsToPlay.size()){
            // if it is looping, check if continuous play is switched on
            if(isLooping()){
                // it is, so reset pointer to tracks
                _positionInList = 0;
                // reset current MediaPlayer instance
                mp.reset();
                // create new instance of MediaPlayer class,
                // initialize it properly, and play next song
                _mPlayer = new MediaPlayer();
                _mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                _mPlayer.setOnCompletionListener(this);
                playList(_positionInList);
            } else {
                // it is not looping, so release on CompletionListener
                // and destroy current instance of MediaPlayer
                _positionInList = 0;
                mp.setOnCompletionListener(null);
                stop();
            }
        } else {
            // there are still tracks to play in list, so destroy current
            // instance of MediaPlayer, create new one and play next song.
            mp.reset();
            _mPlayer = new MediaPlayer();
            _mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            _mPlayer.setOnCompletionListener(this);
            playList(_positionInList);
        }
    }

    /**
     * Pause Action implementation.
     * It preserves MediaPlayer state.
     */
    private void pause(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Pause... entering.");
        }
        try {
            if (_mPlayer != null)
                _mPlayer.pause();
        } catch (Exception e) {
            if(LoggerConfig.ON){
                Log.i(LOG,"ERROR!!! in pause: " + e.getMessage());
            }
        }
    }

    /**
     * Stop Action implementation.
     * It destroys MediaPlayer, but just it.
     */
    private void stop(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Stop... entering.");
        }
        try {
            if (_mPlayer != null) {
                _mPlayer.stop();
                _mPlayer.reset();
                _mPlayer = null;
            }
        } catch (Exception e) {
            if(LoggerConfig.ON){
                Log.i(LOG,"ERROR!!! in stop: "+ e.getMessage());
            }
        }
    }

    /**
     * Destroy Action implementation.
     * It destroys instance of a class.
     */
    private void destroy(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Destroy... entering.");
        }
        _testOnStop++;
        try{
            _mPlayer.stop();
            _mPlayer.reset();
            _mPlayer.release();
        } catch (Exception e){
            if(LoggerConfig.ON) {
                Log.e(LOG, "OnDestroy ERROR:" + e.getMessage());
            }
        } finally {
            _destroyed = true;
            this.stopSelf();

            super.onDestroy();
        }
    }

    /**
     * OnDestroy override.
     * Calls Destroy method before pass event to base class.
     */
    //  BACKGROUND AUDIO SERVICE
    @Override
    public void onDestroy(){
        _testOnDestroy++;
        destroy();

    }

    /**
     * Initialize player, sets everything for play method.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void initializePlayer(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Initialize Player... entering.");
        }
        // Take music source
        GameResourceManager.Music.PlaySource _switcher = GameResourceManager.Music.getMusicSource();
        // and application context from GameResourceManager class
        Context context = GameResourceManager.getApplicationContext();

        switch (_switcher){
            case GAME:
                // Option 1: Music that is delivered with game.
                // Get file data from GameResourceManager class.
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(GameResourceManager.Music.getResourceId());
                try
                {
                    // create instance capable of playing
                    if(_mPlayer == null){
                        _mPlayer = new MediaPlayer();
                    } else {
                        _mPlayer.reset();
                        _mPlayer = new MediaPlayer();
                    }
                    // add onErrorListener...
                    _mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int i, int j) {
                            if(LoggerConfig.ON) {
                                Log.e(LOG, "OnError into MediaPlayer class... entering.");
                            }
                            // Destroy current instance of MediaPlayer
                            // and create and initialize new instance.
                            mp.stop();
                            mp.reset();
                            initializePlayer();
                            play();
                            return false;
                        }
                    });
                    // ... and other configuration data...
                    _mPlayer.setLooping(true);
                    // ... as DataSource is...
                    _mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                    // ... prepare instance of MediaPlayer...
                    _mPlayer.prepare();
                    // ... close file class and return.
                    // Actual start of MediaPlayer instance occurs in a caller of a method.
                    afd.close();
                }
                catch (IllegalArgumentException e)
                {
                    // Passed reference is not of an required type
                    if(LoggerConfig.ON) {
                        Log.e(LOG, "Unable to play audio queue do to exception: " + e.getMessage());
                    }
                }
                catch (IllegalStateException e)
                {
                    // Player is not capable of playing  due to...
                    if(LoggerConfig.ON) {
                        Log.e(LOG, "Unable to play audio queue do to exception: " + e.getMessage());
                    }
                }
                catch (IOException e)
                {
                    // Track does not exists or it is temporarily inaccessible...
                    if(LoggerConfig.ON) {
                        Log.e(LOG, "Unable to play audio queue do to exception: " + e.getMessage());
                    }
                }
                break;
            case PLAYLIST:
                // Option 2: User choose his music
                // so we'll need instance of ContextResolver...
                ContentResolver resolver = context.getContentResolver();
                // ... and long PlayListID which user choose...
                final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", GameResourceManager.Music.getPlayListID());

                if(uri == Uri.EMPTY || uri == null){
                    GameResourceManager.Music.setMusicSource(GameResourceManager.Music.PlaySource.GAME);
                    initializePlayer();
                    break;
                }

                // ... as well as this constant, just for readability sake...
                final String dataKey = MediaStore.Audio.Media.DATA;
                // ... to create Cursor object that will iterate through tracks on list.
                Cursor tracks = resolver.query(uri, new String[] { dataKey }, null, null, null);
                if (tracks != null) {
                    // PlayList have songs, so we'll get sure that our list
                    // will contain just those
                    if(_songsToPlay == null) {
                        // Our list is not initialized yet, so we'll do this now...
                        _songsToPlay = new ArrayList<String>();
                    } else if(!_songsToPlay.isEmpty()) {
                        // ... or clear context that it may have now...
                        _songsToPlay.clear();
                    }
                    // ... and enter circle in which we'll populate
                    // our PlayList with data about tracks that are in users playlist, ...
                    while(tracks.moveToNext()){
                        final int dataIndex = tracks.getColumnIndex(dataKey);
                        final String dataPath = tracks.getString(dataIndex);
                        _songsToPlay.add(dataPath);
                    }
                    // ... release Cursor object, ...
                    tracks.close();
                }
                // create and set new instance of MediaPlayer
                // and let caller to choose what to do with it.
                _mPlayer = new MediaPlayer();
                _mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                break;
            case URL:
                // Option 3: user choose continuous net audio stream
                // so let us...
                try {
                    // ... initialize new instance of MediaPlayer class, ...
                    _mPlayer = new MediaPlayer();
                    _mPlayer.setLooping(true);
                    // ... set error handler, ...
                    _mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int i, int j) {
                            if(LoggerConfig.ON) {
                                Log.e(LOG, "OnError Into URL fired...");
                            }
                            mp.stop();
                            mp.reset();
                            initializePlayer();
                            play();
                            return false;
                        }
                    });
                    // ... and set it's source to Uri that user choose
                    // in order to allow that...
                    _mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    Uri _uriToPlay = Uri.parse(GameResourceManager.Music.getUriToPlay());
                    _mPlayer.setDataSource(this, _uriToPlay);
                    _mPlayer.prepare();
                } catch (IOException e) {
                    // ... unless something unexpected occur with IO stream...
                    if(LoggerConfig.ON) {
                        Log.e(LOG, e.getMessage());
                    }
                }
                catch (Exception e) {
                    // or something else.
                    if(LoggerConfig.ON) {
                        Log.e(LOG, e.getMessage());
                    }
                }

                break;

        }

    }

    /**
     * Implementation of AudioManager.OnAudioFocusChange
     * @param focusChange
     * Method receives info about what happened to AudioFocus object
     * and reacts appropriate to that. Very simple, in fact almost a
     * total copy of example from Java tutorial
     */
    public void onAudioFocusChange(int focusChange) {
        if(LoggerConfig.ON) {
            Log.e(LOG, String.format("Audio focus change to %d", focusChange));
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                stop();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                SharedPreferences.Editor _edit = _preferences.edit();
                _edit.putFloat("Volume", 0.1f);
                _edit.apply();
                setVolume();
                break;
        }
    }
}

