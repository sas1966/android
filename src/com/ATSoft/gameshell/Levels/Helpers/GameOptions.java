package com.ATSoft.gameshell.Levels.Helpers;

import com.ATSoft.GameFramework.Command.GameManager;
import com.ATSoft.gameshell.BaseClasses.BaseLevel;

/**
 * Class GameOption
 *
 * Purpose:
 *  - setting game environment
 *
 *  Implementation: instance of class,
 *                  if everything is OK, ONLY instance of the class
 *
 *  Enters into: trough GameStateClass.
 *  Caller: Button that it is in fact Enemy class instance that have OPTION look and feel
 *  Exit from:  to calling level, trough GameManagerClass; track is preserved into PreviousLevel
 *              variable, set when GameManager when options are called
 *
 * Created by Aleksandar on 13.4.2015..
 */

public class GameOptions extends BaseLevel {
    /**
     * Keeps info about level from which Options are called
     */
    public GameManager.GameState PreviousLevel;
}
