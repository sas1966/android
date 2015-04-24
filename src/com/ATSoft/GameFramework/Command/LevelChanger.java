package com.ATSoft.GameFramework.Command;

import android.content.Context;

import com.ATSoft.gameshell.Interfaces.ILevel;

/**
 * Class LevelChanger
 *
 * Purpose: Instantiate new level and fleshes it out
 *          with help of LevelFactory class
 *
 * Implementation: singleton
 *
 * How it works:
 *  - It is instantiated by GameManagerClass,
 *    and gets name of a class that implements
 *    requested level.
 *  - Creates that level through Reflection
 *    and keeps reference to it in _currentLevel field.
 *  - Calls LevelFactoryClass to fleshes level with game objects.
 *
 * Created by Aleksandar on 13.4.2015..
 */
public class LevelChanger {
    /**
     * Private fields, setters, getters and constructor.
     *  _levelChanger   - singleton instance of a class
     *  _currentLevel   - reference to active level
     *  getCurrentLevel - getter for _currentLevel
     *  _context        - reference to applicationContext. Allows access to resources.
     *  _lf             - instance of LevelFactory class. If everything is OK, ONLY instance.
     *  _layoutId       - unique integer, store ID of a xml resource file that keeps level layout.
     *                    Setting it will call LevelFactory class.
     *  getCurrentClassName and
     *  getExceptionMessage     - for testing only. Those will be deleted in production code.
     */
    private static LevelChanger _levelChanger = null;
    private ILevel _currentLevel = null;
    public ILevel getCurrentLevel(){
        return _currentLevel;
    }
    private LevelFactory _lf;
    public void setLevelLayoutId(int id){
        _lf.setResourceId(id);
    }


    /**
     * Private constructor.
     * @param context - reference to application context.
     */
    private LevelChanger(Context context){
        _currentLevel = null;
        _lf = LevelFactory.getLevelFactory(context);

    }

    /**
     * Implementation of singleton pattern.
     * @param context - reference to application context.
     * @return        - hopefully ONLY instance of a LevelChangerClass
     */
    public synchronized static LevelChanger getLevelChanger(Context context){
        if(_levelChanger == null) {
            _levelChanger = new LevelChanger(context);
        }
        return _levelChanger;
    }

    /**
     * Hart and soul of a class.
     * Instantiate requested class through reflection,
     * flashes it with objects (Not implemented yet) // TODO: populate instance with objects
     * and deals with all bunch of unexpected and, mostly,
     * unwanted situation.
     *
     * @param className - Name of a class to be instantiated.
     */
    @SuppressWarnings("rawtypes")
	public void ChangeLevel(String className){
        try {
            // Get wanted class and instantiate it.
            Class c = Class.forName(className);
            _currentLevel = (ILevel) c.newInstance();

            // This can throw lot's of exceptions, so that
            // is a main reason why this code have it's own class.

            } catch (ClassNotFoundException e){
                // Just for fulfilling contract. If this
                // happens in production code, phone that runs
                // app is broken, so nothing to be done about it.
                e.getStackTrace();
            } catch (InstantiationException e){
                // Lots of stuff can lead here, but most common
                // would be memory shortage, so nothing that
                // we can do about it.
                e.getStackTrace();
            } catch (IllegalAccessException e) {
                // And again, same as with first and most probable
                // exception, if we find ourselves here in production code,
                // app is running on a broken phone.
                e.getStackTrace();
            }
    }

    /**
     * Returns next level ClassName and Layout
     * @return - ClassName / Layout of next level respectively
     */
    public String getNextLevelName(){
        if(_currentLevel != null) {
            return _currentLevel.getNextLevel();
        } else return "ERROR! Class is not instantiated";
    }

    public String getNextLayoutId(){
        if(_currentLevel != null) {
            return _currentLevel.getNextLayoutId();
        } else return "ERROR! Class is not instantiated";
    }

}
