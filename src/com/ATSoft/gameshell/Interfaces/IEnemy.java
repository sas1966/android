package com.ATSoft.gameshell.Interfaces;

import android.graphics.Point;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public interface IEnemy {
    void setPosition(Point point);
    boolean handleTouch(String touchAction, float normalized_X, float normalized_Y);
    void updatePhisics(long deltaTime);
    void getPosition(Point point);
}
