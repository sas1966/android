package com.ATSoft.gameshell.BaseClasses;

import android.opengl.GLSurfaceView;
import com.ATSoft.gameshell.Interfaces.IEnemy;
import com.ATSoft.gameshell.Interfaces.ILevel;
import com.ATSoft.gameshell.Player.Player;
import com.ATSoft.gameshell.util.Background;

import java.util.List;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public abstract class BaseLevel implements ILevel {
    GLSurfaceView _thisGLView;

    List<IEnemy> _enemies;
    Player _player;
    Background _background;

    public void setLevelBackground(Background background){
        _background = background;
    }
    public void setEnemies(List<IEnemy> enemies){
        _enemies = enemies;
    }
    public void setPlayer(Player player){
        _player = player;
    }
    public void handleTouchEvent(String touchAction, float normalized_X, float normalized_Y){
        if(_player.handleTouchEvent(touchAction, normalized_X, normalized_Y)) return;

        for (IEnemy i : _enemies){
            if(i.handleTouch(touchAction, normalized_X, normalized_Y)) break;
        }
    }
    public List<IEnemy> getEnemies(){
        return _enemies;
    }

    public Player getPlayer(){
        return _player;
    }

    public String getNextLevel(){
        return null;
    }

    public String getNextLayoutId() { return null; }
}
