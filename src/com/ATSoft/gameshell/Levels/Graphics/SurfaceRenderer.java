package com.ATSoft.gameshell.Levels.Graphics;

import android.content.Context;
import android.util.Log;
import com.ATSoft.GameFramework.Graphic.BaseGLSurfaceViewRenderer;
import com.ATSoft.GameFramework.Graphic.ShaderHelper;
import com.ATSoft.GameFramework.Util.LoggerConfig;
import com.ATSoft.GameFramework.Util.TextResourceReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Aleksandar on 17.4.2015..
 */
public class SurfaceRenderer extends BaseGLSurfaceViewRenderer {

    private final static String LOG = "SurfaceRenderer";

    private static final int POSITION_COMPONENT_COUNT = 2;

    // For logging purposes... Probably won't overstay it's welcome. Reverse logic.
    boolean _drawed = true;

    private int _programObjectID;
    float[] _vertices;
    private final int BYTES_PER_FLOAT = 4;
    private FloatBuffer _vertexData;
    private final Context _context;

    private static boolean _verticesSet = false;
    private static boolean _vertexShadersSet = false;
    private static boolean _fragmentShadersSet = false;

    private String[] _vertexShaders;
    private String[] _fragmentShaders;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public SurfaceRenderer(Context context){
        if(LoggerConfig.ON){
            Log.i(LOG, "Constructor... entering");
        }
        _context = context;
    }

    public boolean isReady(){
        return _verticesSet && _vertexShadersSet && _fragmentShadersSet;
    }

    @Override
    public void setVertices(float[] vertices){
        if(LoggerConfig.ON){
            Log.i(LOG, "Set Vertices... entering");
        }
        _vertices = vertices;
        _vertexData = ByteBuffer.allocateDirect(_vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        _vertexData.put(_vertices);

        _verticesSet = true;
    }

    @Override
    public void setVertexShaders(int... shaders){
        if(LoggerConfig.ON){
            Log.i(LOG, "Set Vertex Shaders... entering");
        }
        _vertexShaders = new String[shaders.length];
        int i = 0;
        for(int shader : shaders){
            _vertexShaders[i] = TextResourceReader.readTextFileFromResource(_context, shader);
            i++;
        }
        _vertexShadersSet = true;

    }

    @Override
    public void setFragmentShaders(int... shaders){
        if(LoggerConfig.ON){
            Log.i(LOG, "Set Fragment Shaders... entering");
        }
        _fragmentShaders = new String[shaders.length];
        int i = 0;
        for(int shader : shaders){
            _fragmentShaders[i] = TextResourceReader.readTextFileFromResource(_context, shader);
            i++;
        }
        _fragmentShadersSet = true;
    }

    @Override
    public void onSurfaceCreated(GL10 surface, EGLConfig config){
        super.onSurfaceCreated(surface, config);
        if(LoggerConfig.ON){
            Log.i(LOG, "On Surface Created entering");
        }

        _programObjectID = glCreateProgram();
        if(_createProgram()) {

            if (LoggerConfig.ON) {
                ShaderHelper.validateProgram(_programObjectID);
            }

            glUseProgram(_programObjectID);

            uColorLocation = glGetUniformLocation(_programObjectID, U_COLOR);
            aPositionLocation = glGetAttribLocation(_programObjectID, A_POSITION);

            _vertexData.position(0);
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                    false, 0, _vertexData);
            glEnableVertexAttribArray(aPositionLocation);
            if (LoggerConfig.ON) {
                Log.i(LOG, "Program set for use successfully");
            }
        }
    }

    private boolean _createProgram(){
        if(_vertexShadersSet){
            int i = 0;
            int _vertexShader = 0;
            int _fragmentShader = 0;
            for(String s : _vertexShaders){
                _vertexShader = ShaderHelper.compileVertexShader(s);
                if(_vertexShader == 0){
                    return false;
                }
                _fragmentShader = ShaderHelper.compileFragmentShader(_fragmentShaders[i]);
                if(_fragmentShader == 0){
                    return false;
                }
                i++;
            }
            _programObjectID = ShaderHelper.linkProgram(_programObjectID, _vertexShader, _fragmentShader);
            if(_programObjectID == 0){
                return false;
            }
        }


        return true;
    }

    @Override
    public void onSurfaceChanged(GL10 surface, int width, int height){
        super.onSurfaceChanged(surface, width, height);
        if(LoggerConfig.ON){
            Log.i(LOG, "On Surface Changed entering");
        }
    }

    @Override
    public void onDrawFrame(GL10 frame){
        super.onDrawFrame(frame);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);


        if(_drawed && LoggerConfig.ON){
            Log.i(LOG, "On Draw Frame entering");
            _drawed = false;
        }
    }
}
