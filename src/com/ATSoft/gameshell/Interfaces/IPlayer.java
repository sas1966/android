package com.ATSoft.gameshell.Interfaces;

import android.graphics.Point;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public interface IPlayer {
    void setPosition(Point point);
    Point getPosition();
    boolean handleTouchEvent(String touchAction, float normalized_X, float normalized_Y);
}
