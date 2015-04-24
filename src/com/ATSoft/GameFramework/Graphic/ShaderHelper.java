package com.ATSoft.GameFramework.Graphic;

import android.util.Log;
import com.ATSoft.GameFramework.Util.LoggerConfig;

import static android.opengl.GLES20.*;


/**
 * Created by Aleksandar on 18.4.2015..
 */
public class ShaderHelper {
    // Log TAG
    private final static String LOG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        if (LoggerConfig.ON) {
            Log.i(LOG, "Compile Vertex Shader");
        }
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }
    public static int compileFragmentShader(String shaderCode) {
        if (LoggerConfig.ON) {
            Log.i(LOG, "Compile Fragment Shader");
        }
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }
    private static int compileShader(int type, String shaderCode) {
        if (LoggerConfig.ON) {
            Log.i(LOG, "Actual Compile Shader");
        }
        // Creating shader object for a type...
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            // (if we are here, something is very wrong)
            if (LoggerConfig.ON) {
                Log.w(LOG, "Could not create new shader.");
            }
            return 0;
        }

        // ... and inject our source code into it.
        glShaderSource(shaderObjectId, shaderCode);

        glCompileShader(shaderObjectId);

        // Prepare info object...
        final int[] compileStatus = new int[1];
        // ... and compile shader.

        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (LoggerConfig.ON) {
        // Print the shader info log to the Android log output.
            Log.v(LOG, "Results of compiling source:" + "\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
        }

        // Check compile result...
        if (compileStatus[0] == 0) {
        // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);
            if (LoggerConfig.ON) {
                Log.w(LOG, "Compilation of shader failed.");
            }
            return 0;
        }

        // ... and, if we are here, UFFFFF, everything passed well :)
        return shaderObjectId;
    }

    public static int linkProgram(int programObjectId, int vertexShaderId, int fragmentShaderId) {
        final int _thisProgram = programObjectId;
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.e(LOG, "ERROR!!! Null reference passed");
            }
            return 0;
        }

        glAttachShader(_thisProgram, vertexShaderId);
        glAttachShader(_thisProgram, fragmentShaderId);

        glLinkProgram(_thisProgram);

        final int[] linkStatus = new int[1];
        glGetProgramiv(_thisProgram, GL_LINK_STATUS, linkStatus, 0);

        if (LoggerConfig.ON) {
        // Print the program info log to the Android log output.
            Log.v(LOG, "Results of linking program:\n"
                    + glGetProgramInfoLog(_thisProgram));
        }

        if (linkStatus[0] == 0) {
        // If it failed, delete the program object.
            glDeleteProgram(_thisProgram);
            if (LoggerConfig.ON) {
                Log.e(LOG, "ERROR!!! Linking of program failed.");
            }
            return 0;
        }

        return _thisProgram;
    }

    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(LOG, "Results of validating program: " + validateStatus[0]
                + "\nLog:" + glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

}
