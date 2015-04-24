package com.ATSoft.gameshell.Player;

import android.graphics.Point;
import com.ATSoft.gameshell.BaseClasses.BasePlayer;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public class Player extends BasePlayer {
    private Player _player;
    private Point _position;

    private Player(){
        _player = null;
    }
    public Player getPlayer(){
        if(_player == null){
            _player = new Player();
        }
        return _player;
    }

    public boolean handleTouchEvent(String event, float normalizedX, float normalizedY){
        boolean _result = false;

        return _result;
    }

    public void setPosition(Point position){
        _position = position;
    }

    public Point getPosition(){
        return _position;
    }

}
