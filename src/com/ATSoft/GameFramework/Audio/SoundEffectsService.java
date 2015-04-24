package com.ATSoft.GameFramework.Audio;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.ATSoft.GameFramework.Util.GameResourceManager;
import com.ATSoft.GameFramework.Util.LoggerConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aleksandar on 3.4.2015..
 */
public class SoundEffectsService extends Service {
    /**
     * First, as always, LOG constant
     */
    private static final String LOG = "SoundEffectsService";

    /**
     * TEST VARIABLES
     */
    public static int _testOnCreate     = 0;
    public static int _testOnBind       = 0;
    public static int _testOnCommand    = 0;
    public static int _testOnStop       = 0;
    public static int _testOnDestroy    = 0;



    /**
     * Boolean _destroyed.
     * in case that some thread try to start service again
     * after it is destroyed.
     */
    private static boolean _destroyed = false;
    public static void setDestroyed(boolean value) { _destroyed = value; }
    public static boolean getDestroyed() { return _destroyed; }

    /**
     * Class PlayOptions, helper for play from SoundPool
     * Contains all options except songID.
     */
    class PlayOptions {
        public float get_leftVolume() {
            return _leftVolume;
        }

        public void set_leftVolume(float _leftVolume) {
            this._leftVolume = _leftVolume;
        }

        public float get_rightVolume() {
            return _rightVolume;
        }

        public void set_rightVolume(float _rightVolume) {
            this._rightVolume = _rightVolume;
        }

        public int get_priority() {
            return _priority;
        }

        public void set_priority(int _priority) {
            this._priority = _priority;
        }

        public int get_loop() {
            return _loop;
        }

        public void set_loop(int _loop) {
            this._loop = _loop;
        }

        public float get_rate() {
            return _rate;
        }

        public void set_rate(float _rate) {
            this._rate = _rate;
        }

        float _leftVolume;
        float _rightVolume;
        int _priority;
        int _loop;
        float _rate;

        public PlayOptions(){
            _leftVolume = 1.0f;
            _rightVolume = 1.0f;
            _priority = 0;
            _loop = 0;
            _rate = 1.0f;
        }
        public PlayOptions(float leftVolume, float rightVolume, int priority, int loop, float bitRate){
            _leftVolume = leftVolume;
            _rightVolume = rightVolume;
            _priority = priority;
            _loop = loop;
            _rate = bitRate;
        }

    }

    /**
     * Default PlayOptions object
     */
    PlayOptions _defaultPlayOption = new PlayOptions();

    /**
     * Instance of class. Just in case, don't know at the moment
     * if it will be needed, necessary, or 5th wheel :)
     */
    private static SoundEffectsService _soundEffectService = null;

    /**
     * Context. I'm pretty sure that I'll have heavy use of it.
     */
    private static Context _context = null;

    /**
     * Binder. I want Service, so I must have binder.
     * Pretty much the same as "you want wife, you'll get mother in law extra".
     * And free of charge, too :)
     */
    private static IBinder _iBinder = null;

    /**
     * SoundPool. Hart and soul of class.
     * Everything in it is here just to ensure
     * that this component works as it should.
     * And it's most useful helper, _loaded :)
     */
    private static SoundPool _soundPool;
    private static boolean _loaded = true;
    private static boolean is_loaded(){ return _loaded; }

    /**
     * Hash map in which sounds will go.
     * Maybe this is redundant, or outright stupid,
     * but right now it seems like good idea :)
     * @key - Resource.ID
     * @value - int returned by load(sound) function
     */
    private static HashMap<Integer, Integer> _soundsMap = new HashMap<Integer, Integer>();

    /**
     * Since I still don't know if I'll have class at all,
     * singleton pattern seems appropriate.
     * @param ctx - Application context
     * @return - Instance of class
     */
    public static SoundEffectsService getInstance(Context ctx) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "GetInstance, with Context passed... entering.");
        }
        if(_soundEffectService == null){
            _soundEffectService = new SoundEffectsService(ctx);
        }
        return _soundEffectService;
    }

    /**
     * Same as nothing, ot return null, but Service demands it.
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent){
        _testOnBind++;
        return _iBinder;
    }

    /**
     * No parameter getter of instance.
     * @return - Instance of a class.
     */
    public static SoundEffectsService getInstance(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "GetInstance, without arguments... entering.");
        }
        return _soundEffectService;
    }

    /**
     * As every decent singleton, this also have private constructor.
     * @param ctx - Application context
     */
    private SoundEffectsService(Context ctx) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "Constructor... entering.");
        }
        // Set context...
        _context = ctx;
        // ... and create SoundPool.
        CreateSoundPool();
    }

    /**
     * Asked for by AndroidManifest.xml
     * No point and no use, but machine got her way.
     */
    public SoundEffectsService(){
    }

    /**
     * Copied piece of code for creating audio pool for every build configuration.
     * Thanks, Krypton Games :)
     */
    protected void CreateSoundPool(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "SoundPool create... entering");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        }else{
            createOldSoundPool();
        }

        _soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                if(LoggerConfig.ON) {
                    Log.i(LOG, "SoundPool LOAD_COMPLETE event... entering");
                }
                _loaded = true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Create NEW SoundPool... entering.");
        }
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }
    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Create OLD SoundPool... entering.");
        }
        _soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);
    }


    // SOUND EFFECT CLASS
    @Override
    public void  onCreate(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnCreate... entering.");
        }
        _testOnCreate++;
        if(_destroyed) return;
        super.onCreate();
    }

    /**
     * Hart and soul of this service. Exposes all service functionality,
     * resolves all user requests.
     * @param intent - Intent object that carries command to service through it's Action property
     * @param flags - Intent flags that can help resolve desired behavior. Unused at the moment.
     * @param startId - Identification. Have no idea at all for the moment of how to make it useful.
     * @return int - Service type.
     */
    // SOUND EFFECT CLASS
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnStartCommand... entering.");
        }
        _testOnCommand++;
        try {
            if (_destroyed) {
                return -1;
            }

            // Before all, check if context and sound pool exists at all,
            // and create them if they don't.
            if (_context == null) {
                _context = GameResourceManager.getApplicationContext();
            }
            if (_soundPool == null) {
                CreateSoundPool();
            }

            // For now, service implementation expects that raw resource ID
            // should be passed in as Extra of intent that requested service.
            int _soundId = intent.getIntExtra("SOUND_ID", -1);
            String _action = intent.getAction();
            if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionLoadFile")) {
                // Load file into pool
                loadFile(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionUnloadFile")) {
                // Unload file from pool
                unload(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionPlay")) {
                // Play sound from pool
                play(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionPause")) {
                // Pause specific sound
                pause(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionResume")) {
                // Resume specific sound
                resume(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionStop")) {
                // Stops specific sound
                stop(_soundId);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionSetVolume")) {
                // SetVolume to specific sound
                float _left = intent.getFloatExtra("VOLUME_LEFT", 1.0f);
                float _right = intent.getFloatExtra("VOLUME_RIGHT", 1.0f);
                setVolume(_soundId, _left, _right);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionSetPriority")) {
                // Set Priority of specific sound
                int _priority = intent.getIntExtra("PRIORITY", 1);
                setPriority(_soundId, _priority);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionSetRate")) {
                // Set Rate of specific sound. Effectively changes speed of sound reproduction.
                float _rate = intent.getFloatExtra("RATE", 1.0f);
                setRate(_soundId, _rate);
            } else if (_action.equals("com.ATSoft.GameResourceManager.SoundEffects.ActionRelease")) {
                // Destroy service.
                destroy();
            }
            return Service.START_NOT_STICKY;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Load resource file into sound pool. No check of type what so ever.
     * If user try to play picture as audio, let him be. He must have reason for that.
     * In mean time, load it into pool and store returned int in _soundsMap HashMap.
     * @param resourceId - integer, R.raw.sound_id.
     */
    private void loadFile(int resourceId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "LoadFile... entering.");
        }
        AssetFileDescriptor afd = _context.getResources().openRawResourceFd(resourceId);
        try
        {

            if(!_soundsMap.containsKey(resourceId)) {
                _loaded = false;
                int _soundId = _soundPool.load(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength(), 1);
                _soundsMap.put(resourceId, _soundId);
            }
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
    }

    /**
     * Utility method that converts R.raw.resourceID to SoundPool soundID
     */
    private int getSound(int key){
        return _soundsMap.get(key);
    }

    /**
     * Unload specific sound from SoundPool.
     *
     * This and all subsequent methods require translation between
     * R.raw.ResourceID, which is entry parameter,
     * and actual SoundID from HashMap.
     *
     * Also removes it from _soundMapHashMap
     * @param _soundId - integer, R.raw.soundID.
     */
    private void unload(int _soundId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "UnLoadFile... entering.");
        }
        _soundsMap.remove(_soundId);
        _soundPool.unload(getSound(_soundId));
    }

    /**
     * Play specified sound. Subsequent method.
     * @param _soundId - integer, R.raw.soundID.
     */
    private void play(int _soundId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Play with default options... entering.");
        }
        // Playing with default options
        if(is_loaded()) {
            _soundPool.play(getSound(_soundId), _defaultPlayOption.get_leftVolume(), _defaultPlayOption.get_rightVolume(), _defaultPlayOption.get_loop(), _defaultPlayOption.get_priority(), _defaultPlayOption.get_rate());
        }
    }

    private void play(int _soundId, PlayOptions playOptions){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Play with passed options... entering.");
        }
        // Playing with passed options.
        if(is_loaded()) {
            _soundPool.play(getSound(_soundId), playOptions.get_leftVolume(), playOptions.get_rightVolume(), playOptions.get_loop(), playOptions.get_priority(), playOptions.get_rate());
        }
    }

    /**
     * Pauses specified sound. Subsequent method.
     * @param _soundId  - integer, R.raw.soundID.
     */
    private void pause(int _soundId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Pause... entering.");
        }
        if(is_loaded()) {
            _soundPool.pause(getSound(_soundId));
        }
    }

    /**
     * Resumes play of specified sound. Subsequent method.
     * @param _soundId - integer, R.raw.soundID.
     */
    private void resume(int _soundId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Resume... entering.");
        }
        if(is_loaded()) {
            _soundPool.resume(getSound(_soundId));
        }
    }

    /**
     * Stops specified sound. Subsequent method.
     * @param _soundId - integer, R.raw.soundID.
     */
    private void stop(int _soundId){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Stop... entering.");
        }
        if(is_loaded()) {
            _soundPool.stop(getSound(_soundId));
        }
    }

    /**
     * Sets volume of specified sound. Subsequent method.
     * Since playing with volume have great importance in
     * creating desired effect, should be designed with great concern.
     * @param _soundId - integer, R.raw.soundID.
     * @param _left - float, volume of left chanel
     * @param _right - float, volume of right chanel
     */
    private void setVolume(int _soundId, float _left, float _right){
        if(LoggerConfig.ON) {
            Log.i(LOG, "SetVolume... entering.");
        }
        if(is_loaded()) {
            _soundPool.setVolume(getSound(_soundId), _left, _right);
        }
    }

    /**
     * Sets priority of specified sound. Subsequent method.
     * Since priority can have great importance in
     * creating desired effect, should be designed with great concern.
     * @param _soundId - integer, R.raw.soundID.
     * @param _priority - integer, new priority of sound
     */
    private void setPriority(int _soundId, int _priority){
        if(LoggerConfig.ON) {
            Log.i(LOG, "SetPriority... entering.");
        }
        if(is_loaded()) {
            _soundPool.setPriority(getSound(_soundId), _priority);
        }
    }

    /**
     * Sets Rate of specified sound. Subsequent method.
     * Since Rate can have great importance in
     * creating desired effect, should be designed with great concern.
     * @param _soundId - integer, R.raw.soundID.
     * @param _rate - float, new rate of sound
     */
    private void setRate(int _soundId, float _rate){
        if(LoggerConfig.ON) {
            Log.i(LOG, "SetRate... entering.");
        }
        if(is_loaded()) {
            _soundPool.setRate(getSound(_soundId), _rate);
        }
    }

    /**
     * Destroy service. What else we should tell?
     */
    private void destroy(){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Destroy... entering.");
        }
        _testOnStop++;
        try{
            unloadFiles();
            _soundsMap.clear();
            _soundPool.release();
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
     * Helper function.
     * Unloads files from pool.
     */
    private void unloadFiles(){
        for(Map.Entry<Integer,Integer> i : _soundsMap.entrySet()){
            unload(i.getValue());        }
    }

    /**
     * OnDestroy override.
     * Calls Destroy method before pass event to base class.
     */
    // SOUND EFFECT CLASS
    @Override
    public void onDestroy(){
        _testOnDestroy++;

        destroy();
    }

}
