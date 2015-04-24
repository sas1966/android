package com.ATSoft.GameFramework.Graphic;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created by Aleksandar on 17.4.2015..
 */
public abstract class BaseGLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    @Override
    public void onSurfaceCreated(GL10 surface, EGLConfig config){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 surface, int width, int height){
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 frame){
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public abstract void setVertices(float[] vertexes);

    public abstract void setVertexShaders(int... shaders);

    public abstract void setFragmentShaders(int... shaders);
}
