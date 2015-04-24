package com.ATSoft.GameFramework.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by Aleksandar on 31.3.2015..
 */
public class BroadcastReceiver_Service extends BroadcastReceiver {
    private final String LOG = "BroadReceiverService";
    private boolean _headSetConnected;
    @Override
    public void onReceive(Context context, Intent intent){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Entering");
        }
        if (intent.hasExtra("state")){
            if(LoggerConfig.ON) {
                Log.i(LOG, "Passed intent have extra data...");
            }
            if (_headSetConnected && intent.getIntExtra("state", 0) == 0){
                _headSetConnected = false;
                GameResourceManager.getApplicationContext().startService(GameResourceManager.Music.Pause());
            } else if (!_headSetConnected && intent.getIntExtra("state", 0) == 1){
                _headSetConnected = true;
                GameResourceManager.getApplicationContext().startService(GameResourceManager.Music.Play());
            }
        } else {
            if(LoggerConfig.ON) {
                Log.i(LOG, "Intent have NO extra AT ALL...");
            }
        }

    }

    //
    // region test helpers
    //

    public <T> T getField(String fieldName) throws Exception {
        return getPrivateField(fieldName, this);
    }

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