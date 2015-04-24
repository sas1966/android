package com.ATSoft.GameFramework.Util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ATSoft.GameFramework.Audio.BackgroundAudioService;
import com.ATSoft.GameFramework.Audio.SoundEffectsService;

/**
 * Created by Aleksandar on 27.3.2015..
 */
public class GameResourceManager {
    /**
     * Class that collects all classes and methods for accessing resources
     **/
    private static final String LOG = "GameResourceManager";
    //private static Application _app;
    private static Context _context;
    private static GameResourceManager _gameResourceManager=null;
    /*private GameResourceManager(Application app){
        _app = app;
        _context = _app.getApplicationContext();
    }*/
    private GameResourceManager(Context ctx){
        _context = ctx;
    }

    public static GameResourceManager getGameResourceManager(Context ctx){
        if(LoggerConfig.ON) {
            Log.i(LOG, "getGameResourceManager... entering.");
        }
        if(_gameResourceManager == null) {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Create GameStateManager instance---");
            }
            _gameResourceManager = new GameResourceManager(ctx);
        }
        return _gameResourceManager;
    }

    /*public static Application getApplication(){
        return _app;
    }*/

    public static Context getApplicationContext() { return _context; }

    public static class Music
    {
        private final static String LOG = "G_R_Manager.MUSIC";
        /**
         * Class that plays background music
         * Uses helper class BackgroundAudioService
         *
         * ENUM PlaySource
         * Define source for background music
         * 1. GAME - Music provided with game
         * 2. PLAYLIST - Music from an chosen playlist on a device
         * 3. URL - Net audio stream
         *
         **/
        public enum PlaySource{
            GAME,
            PLAYLIST,
            URL
        }
        /**
         *
         * private variable _playListID: long
         * accessible with setter and getter on Option screen
         * stores playlist identification number PID to pass to BackgroundAudioMusic helper class
         *
         **/
        private static long _playListID = -1;
        public static long getPlayListID() {return _playListID;}
        public static void setPlayListId(long value) {_playListID = value;}
        /**
         *
         * private variable _resourceId: int
         * set in game initialization code
         * user have no direct access to it
         * stores resource ID of default game background music
         *
         **/
        private static int _resourceId;
        public static void setResourceId(int rID){
            _resourceId = rID;
        }
        public static int getResourceId(){
            return _resourceId;
        }
        /**
         * private variable _audioSource: PlaySource
         * accessible with setter on Option screen
         * stores user choice of background music
         **/
        private static PlaySource _audioSource = PlaySource.GAME;
        public static void setMusicSource(PlaySource source){
            _audioSource=source;
        }
        public static PlaySource getMusicSource(){
            return _audioSource;
        }
        /**
         * private variable _uriLink: String
         * accessible with setter on Option screen
         * Sets audio stream Uri to play
         * Debug default = Radio Beograd 202
         * Production version = null
         **/
        private static String _uriLink = "http://rts.ipradio.rs:8006";
        public static void setUriString(String value){
            _uriLink = value;
        }
        public static String getUriToPlay(){
            return _uriLink;
        }

        /**
         * Just for debug...
         */
        public static BroadcastReceiver_Service _BRS = null;
        public static void setBroadcastReceiver(BroadcastReceiver_Service brs){
            _BRS = brs;
        }
        public static BroadcastReceiver_Service getBroadcastReceiver(){
            return _BRS;
        }
        /**
         * Creates and returns PLAY Intent
         * Caller is responsible for actual calling of an intent
         **/
        public static Intent Play()
        {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Intent PLAY create... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, BackgroundAudioService.class);
            inty.setAction("com.ATSoft.GameResourceManager.Music.ActionPlay");
            return inty;
        }
        /**
         * Creates and returns PAUSE Intent
         * Caller is responsible for actual calling of an intent
         **/
        public static Intent Pause()
        {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Intent PAUSE create... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, BackgroundAudioService.class);
            inty.setAction("com.ATSoft.GameResourceManager.Music.ActionPause");
            return inty;
        }
        /**
         * Creates and returns STOP Intent
         * Caller is responsible for actual calling of an intent
         **/
        public static Intent Stop()
        {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Intent STOP create... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, BackgroundAudioService.class);
            inty.setAction("com.ATSoft.GameResourceManager.Music.ActionStop");
            return inty;
        }
        /**
         * Creates and returns SET VOLUME Intent
         * Caller is responsible for actual calling of an intent
         **/
        public static Intent SetVolume()
        {
            if(LoggerConfig.ON) {
                Log.i(LOG, String.format("Intent SET_VOLUME create... entering."));
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, BackgroundAudioService.class);
            inty.setAction("com.ATSoft.GameResourceManager.Music.ActionSetVolume");
            return inty;
        }
        /**
         * Creates and returns DESTROY Intent
         * Caller is responsible for actual calling of an intent
         **/
        public static Intent SetDestroy()
        {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Intent SET_DESTROY create... entering.");
            }
           //if(_app==null) return null;
            Intent inty = new Intent(_context, BackgroundAudioService.class);
            inty.setAction("com.ATSoft.GameResourceManager.Music.ActionDestroy");
            return inty;
        }

    }

    public static class SoundEffects
    {

        private static final String LOG = "GRM_SoundEffects";

        SoundEffectsService _soundEffectsService = SoundEffectsService.getInstance(GameResourceManager.getApplicationContext());

        public static Intent LoadFile(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "LoadFile... entering. File ID: " + _fileID);
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionLoadFile");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent UnloadFile(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "UnloadFile... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionUnloadFile");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent Play(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "PLayFile... entering. File ID: " + _fileID);
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionPlay");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent Pause(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "Pause... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionPause");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent Resume(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "ResumeFile... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionResume");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent Stop(int _fileID){
            if(LoggerConfig.ON) {
                Log.i(LOG, "Stop... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionStop");
            inty.putExtra("SOUND_ID", _fileID);
            return inty;
        }

        public static Intent SetVolume(int _fileID, float _lVol, float _rVol){
            if(LoggerConfig.ON) {
                Log.i(LOG, "SetVolume... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionSetVolume");
            inty.putExtra("SOUND_ID", _fileID);
            inty.putExtra("VOLUME_LEFT", _lVol);
            inty.putExtra("VOLUME_RIGHT", _rVol);
            return inty;
        }

        public static Intent SetPriority(int _fileID, int _priority){
            if(LoggerConfig.ON) {
                Log.i(LOG, "SetPriority... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionSetPriority");
            inty.putExtra("SOUND_ID", _fileID);
            inty.putExtra("PRIORITY", _priority);
            return inty;
        }

        public static Intent SetRate(int _fileID, float _rate){
            if(LoggerConfig.ON) {
                Log.i(LOG, "SetRate... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionSetRate");
            inty.putExtra("SOUND_ID", _fileID);
            inty.putExtra("RATE", _rate);
            return inty;
        }

        public static Intent Destroy(){
            if(LoggerConfig.ON) {
                Log.i(LOG, "Destroy... entering.");
            }
            //if(_app==null) return null;
            Intent inty = new Intent(_context, SoundEffectsService.class);
            inty.setAction("com.ATSoft.GameResourceManager.SoundEffects.ActionRelease");
            return inty;
        }

    }

    public static class Graphic {

    }
}

