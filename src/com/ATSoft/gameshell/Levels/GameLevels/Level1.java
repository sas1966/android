package com.ATSoft.gameshell.Levels.GameLevels;

import com.ATSoft.gameshell.BaseClasses.BaseLevel;

/**
 * Created by Aleksandar on 13.4.2015..
 */
public class Level1 extends BaseLevel{
    private final String _nextLevel = "com.ATSoft.androidgame.app.Levels.GameLevels.Level2";
    private final String _nextLayout = "game_level2_layout";

    @Override
    public String getNextLevel() {
        return _nextLevel;
    }

    @Override
    public String getNextLayoutId() {return _nextLayout ;}
}
