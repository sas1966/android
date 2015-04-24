package com.ATSoft.GameFramework.Command;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import com.ATSoft.GameFramework.Util.LoggerConfig;
import com.ATSoft.gameshell.Levels.Helpers.GameOptions;
import com.ATSoft.gameshell.R;

import java.lang.reflect.Field;

/**
 * Class GameManager
 *
 * Purpose: change levels through LevelChanger class.
 *
 * Implementation: singleton.
 *
 * How it works:
 *  - At the beginning, sets level to GameLoaderClass,
 *    after that changes are initialized by calls from current level.
 *  - If GameOptionClass level is requested, stores previous level
 *    and passes it to GameOptionInstance
 *  - Injects current level into activity - NOT IMPLEMENTED YET.
 *
 * Created by Aleksandar on 13.4.2015..
 */
public class GameManager{

    private static final String LOG = "GameManager";

    /**
     * Private fields and constructor
     * _gameManager     - singleton instance of class
     * _context         - application context
     * _state           - current state
     * _levelChanger    - instance of level changer class.
     */
    private static GameManager _gameManager;
    private Context _context;
    private GameState _state;
    private LevelChanger _levelChanger;
    private LevelFactory _levelFactory;
    private GameManager(Context context){
        _context = context;
        _levelChanger = LevelChanger.getLevelChanger(_context);
        _levelFactory = LevelFactory.getLevelFactory(_context);
    }

    private ProgressDialog _progress;
    public void setProgressDialog(ProgressDialog progress){
        _progress = progress;
    }

    /**
     * Public enum GameState.
     * Level will be changed by passing one of values
     * to instance of GameManagerClass
     */
    public enum GameState{
        LOADING,
        START_SCREEN,
        OPTIONS,
        PLAY,
        INSTRUCTIONS,
        RESULTS,
        CREDITS
    }

    /**
     * Default empty constructor,
     * for testing purpose.
     */
    private GameManager(){
        _state = GameState.LOADING;
    }

    /**
     * Implementation of singleton pattern
     * @param context - application context, needed for access to resources
     * @return - GameManager singleton instance.
     */
    public static synchronized GameManager getGameManager(Context context){
        if(_gameManager == null){
            _gameManager = new GameManager(context);
        }
        return _gameManager;
    }

    /**
     * Will keep reference to next layout
     */
    String _layoutId;

    /**
     * Actual working part
     * @param option - GameState we want to change to.
     *                 This method don't have any logic
     *                 whatsoever about deciding if it is
     *                 possible. It is a duty of a caller
     *                 to send correct data.
     */
    public void setGameState(GameState option){
        // We'll save current game state if Option screen is requested,...
        GameState _previous = _state;

        // ... change state variable to new state
        _state = option;

        // ... and introduce local variable for passing resourceId for layout.


        // and call _levelChanger instance, passing appropriate value.
        // Appropriate values are stored at Resources.String dictionary.
        switch (_state){
            case LOADING:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.GAME_LOADER));
                _layoutId = _context.getResources().getString(R.string.GameLoaderLayout);
                break;
            case START_SCREEN:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.START_SCREEN));
                _layoutId = _context.getResources().getString(R.string.StartScreenLayout);
                break;
            case OPTIONS:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.GAME_OPTIONS));
                ((GameOptions) _levelChanger.getCurrentLevel()).PreviousLevel = _previous;
                _layoutId = _context.getResources().getString(R.string.GameOptionLayout);
                break;
            case PLAY:
                String _nextLevel = _levelChanger.getNextLevelName();
                _layoutId = _levelChanger.getNextLayoutId();
                if(_nextLevel == null){
                    _nextLevel = _context.getResources().getString(R.string.PLAY);
                    _layoutId = _context.getResources().getString(R.string.Level1Layout);
                }
                _levelChanger.ChangeLevel(_nextLevel);
                break;
            case INSTRUCTIONS:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.INSTRUCTIONS));
                _layoutId = _context.getResources().getString(R.string.InstructionsLayout);
                break;
            case RESULTS:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.RESULTS));
                _layoutId = _context.getResources().getString(R.string.ResultLayout);
                break;
            case CREDITS:
                _levelChanger.ChangeLevel(_context.getResources().getString(R.string.CREDITS));
                _layoutId = _context.getResources().getString(R.string.CreditsLayout);
                break;
            default:
                throw new IllegalArgumentException("Invalid option");
        }

        // At this point we have bare level reference in LevelChanger._currentLevel.
        // To flash that out, we need access to Xml file that stores data about level
        // lookout, and objects that populate it.
        // We'll use that little helper from StackOverflow to get unique
        // integer key for needed resource...
        int _id = 0;
        try {
            _id = getResId(_layoutId, R.xml.class);
        } catch (Exception e) {
            if(LoggerConfig.ON) {
                Log.i(LOG, "ERROR!!! Unable to get unique ResourceID. " + e.getMessage());
            }
        }

        //... and then create instance of CreateLevelAsyncClass
        // that will flesh out our skeleton of a level.
        CreateLevelAsync _flashNewLevel = new CreateLevelAsync();

        // We must pass instance of AsyncTask.class to LevelFactory instance
        // if we want info about progress, and we definitely want that...
        _levelFactory.setProgressReporter(_flashNewLevel);

        // ... before we start flashing of a level in background.
        _flashNewLevel.execute(_id);
        //
        // TODO: Inject levelChanger._currentLevel into Activity
        //
    }

    /**
     * Get current GameState
     * @return - current GameState object.
     *
     * - I'm not too sure that this will ever be used,
     *   but it is much better to have it here for a time being
     *   then look after it at some point in the future.
     */
    public GameState getGameState(){
        return _state;
    }


    /**
     * Class that will create level async
     *
     *  Did this more as a exercise then there was real need.
     *  It is hard to think about usefulness of async method for create level.
     *  Main point of async methods is to release load form main UI thread,
     *  but if there is no possibility to play whatsoever, where is a point?
     *
     *  To be decided if this will stay.
     *
     */
    class CreateLevelAsync extends AsyncTask<Integer, Integer, String> {

        /**
         * Default constructor, same as IDE would create
         */
        CreateLevelAsync(){
            super();
        }

        /**
         * Method that is called before real work is committed.
         * Use: check if ProgressDialog that will serve as user indicator
         * of activity is passed from main activity, and create one if it is not.
         */
        @Override
        protected void onPreExecute() {
            try {
                if (_progress == null) {
                    _progress = new ProgressDialog(_context);
                    _progress.setTitle("Loading");
                    _progress.setMessage("Please wait...");
                    _progress.setProgress(0);
                    _progress.setMax(100);
                    _progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                            return true;
                        }
                    });
                }
                _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                _progress.show();
                _progress.setCancelable(true);
            } catch (Exception e){
                e.getStackTrace();
            }
        }

        /**
         * Real work is done here: Passing level layout xml to
         * LevelFactory class,  through LevelChanger class.
         * @param inputs - Only 1, always, unique ID of level Xml layout. Integer.
         * @return - something that can bring us into onPostExecute. In this case,
         *           String = "0", but that really is not important.
         */
        @Override
        protected String doInBackground(Integer... inputs){

            _levelChanger.setLevelLayoutId(inputs[0]);
            return "0";
        }

        /**
         * Since AsyncTask.class onProgressUpdate is with protected access level,
         * we need one helper method to change that in public. Method does just that -
         * calls  onProgressUpdate overridden method.
         * @param progress - progress increment. Integer. Just 1 parameter always.
         */
        public void reportProgress(Integer... progress){
            onProgressUpdate(progress);
        }

        /**
         * Overridden method from AsyncTask.class.
         * @param progress - progress increment. Integer. Just 1 parameter always.
         */
        @Override
        protected void onProgressUpdate(Integer... progress){
            if(_progress != null) {
                _progress.incrementProgressBy(progress[0]);
            }
            if(LoggerConfig.ON) {
                Log.i(LOG, "Progress update, Progress = " + progress[0]);
            }
        }

        /**
         * Overridden method from AsyncTask.class, called when doInBackground
         * finishes execution. We'll use this to call method from GameManager
         * that will do real injection of data from LevelChanger._currentLevel
         * into default activity.
         * @param result - String. Can be anything. Not used at the moment,
         *                 nor it will be in foreseeable future.
         */
        @Override
        protected void onPostExecute(String result){
            if(_progress != null) {
                _progress.dismiss();
            }
            if(LoggerConfig.ON) {
                Log.i(LOG, "AsyncCreateLevel, Done");
            }

            //
            // TODO: Initiate injection of created level into activity
            //
        }

    }

    /**
     * Piece of code from StackOverflow.
     * Returns int for resource name and class.
     *
     * 2 days here, works quite well, so it will stay.
     */
    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
