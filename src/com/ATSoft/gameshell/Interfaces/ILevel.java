package com.ATSoft.gameshell.Interfaces;

import com.ATSoft.gameshell.Player.Player;
import com.ATSoft.gameshell.util.Background;

import java.util.List;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public interface ILevel {
    void setLevelBackground(Background background);
    void setEnemies(List<IEnemy> enemies);
    void setPlayer(Player player);
    void handleTouchEvent(String touchAction, float normalized_X, float normalized_Y);
    List<IEnemy> getEnemies();
    Player getPlayer();
    String getNextLevel();
    String getNextLayoutId();
}
