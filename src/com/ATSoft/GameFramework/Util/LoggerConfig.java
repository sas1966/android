package com.ATSoft.GameFramework.Util;

/**
 * Helper class. For turning logging process On or Off from one place.
 *
 * To work properly, every single Log statement in code must be
 * wrapped in block
 *          if(LoggerConfig.ON){
 *              Log.i/e/v ...
 *          }
 * You get idea behind this :)
 *
 * Created by Aleksandar on 18.4.2015..
 */
public class LoggerConfig {
    public static final boolean ON = true;
}
