package com.ATSoft.GameFramework.Util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * TextResourceReader.
 *
 * Implementation: static function.
 *
 * Usage:   Read text file from R.raw and put output into String.
 *          Used by SurfaceViewRenderer class to store shaders in
 *          memory before compiling them.
 *
 * Source: OpenGl ES for Android
 *
 * Created by Aleksandar on 18.4.2015..
 */
public class TextResourceReader {
    /**
     * Static method that reads context of a file and stores it into String variable.
     *
     * @param context       - Current context
     * @param resourceId    - unique resource Id for a text file we want to load
     * @return              - String variable that contains contents of a referenced file,
     *                        or a null, if something goes wrong.
     */
    public static String readTextFileFromResource(Context context, int resourceId){
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream =
                    context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString();
    }
}
