package com.ATSoft.GameFramework.Graphic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.ATSoft.GameFramework.Util.LoggerConfig;

/**
 * Created by Aleksandar on 10.4.2015..
 */
public class GraphicService extends Service {

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

    // GRAPHIC RESOURCES CLASS
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
     * Binder. I want Service, so I must have binder.
     * Pretty much the same as "you want wife, you'll get mother in law extra".
     * And free of charge, too :)
     */
    private static IBinder _iBinder = null;

    /**
     * Same as nothing, it return null, but Service demands it.
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent){
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnBind... entering.");
        }
        _testOnBind++;
        //
        // TODO: Implement binding code here
        //
        return _iBinder;
    }

    /**
     * Hart and soul of this service. Exposes all service functionality,
     * resolves all user requests.
     * @param intent - Intent object that carries command to service through it's Action property
     * @param flags - Intent flags that can help resolve desired behavior. Unused at the moment.
     * @param startId - Identification. Have no idea at all for the moment of how to make it useful.
     * @return int - Service type.
     */
    // GRAPHIC RESOURCES CLASS
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnStartCommand... entering");
        }
        _testOnCommand++;
        try {
            if (_destroyed)
                return -1;
            //
            // TODO: Implement service commands switchboard
            //

        } catch (Exception e) {
            if(LoggerConfig.ON) {
                Log.i(LOG, "ERROR! " + e.getMessage());
            }
        }

        return START_NOT_STICKY;
    }

    /**
     * Method for cleaning resources and stopping service.
     */
    private void destroy(){
        _testOnStop++;
        try {
            // cleaning code here
            //
            // TODO: Implement cleaning code here
            //
        } catch (Exception e){
            if(LoggerConfig.ON) {
                Log.i(LOG, "ERROR! in destroy() method: " + e.getMessage());
            }
        }
        finally {
            stopSelf();
            super.onDestroy();
        }
    }

    /**
     * Override method for cleaning up.
     * System point for cleaning service.
     */
    @Override
    public void onDestroy(){
        _testOnDestroy++;
        destroy();
    }

    //
    // TODO: implement service commands
    //

}
