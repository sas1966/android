package com.ATSoft.GameFramework.Command;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import com.ATSoft.GameFramework.Util.LoggerConfig;
import com.ATSoft.gameshell.Interfaces.ILevel;
import com.ATSoft.gameshell.util.Background;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class LevelFactory
 *
 * Purpose: populate level created into LevelChangerClass with objects for game play.
 *
 * Implementation: singleton
 *
 * How it works:
 *  -
 * Created by Aleksandar on 13.4.2015..
 */
public class LevelFactory {

    private final static String LOG = "LevelFactory";

    static LevelFactory _levelFactory;
    LevelChanger _LC;
    ILevel _currentLevel;
    Background _Bck = new Background();
    Context _context;
    List<Enemy> _enemies = new ArrayList<Enemy>();
    List<Players> _players = new ArrayList<Players>();
    public void setResourceId(int resource){
        parseXml(resource);
    }

    private GameManager.CreateLevelAsync _reporter;
    public void setProgressReporter(GameManager.CreateLevelAsync reporter){
        _reporter = reporter;
    }


    private class BckGround{
        private String _ratio;
        public int Height;
        public int Width;
        public void setRatio(String ratio){
            _ratio = ratio;
            String[] elements = ratio.split(":");
            Height = Integer.parseInt(elements[0]);
            Width = Integer.parseInt(elements[1]);
            Tiles = new int [Height] [Width];
        }
        public String Ratio(){
            return _ratio;
        }
        public int[][] Tiles;
        public String Drawable;
    }

    private class Enemy extends CharacterData {

    }

    private class Players extends CharacterData{

    }

    private class CharacterData{
        public String className;
        public String position;
        public List<String> sound = new ArrayList<String>();
        public List<String> animation = new ArrayList<String>();
        public List<Integer> duration = new ArrayList<Integer>();
    }

    private enum XML_TAGS{
        CLASSNAME,
        RATIO,
        BACKGROUND,
        ROW,
        BACKGROUND_DRAWABLE,
        ENEMIES,
        SPECIFIC_ENEMY,
        PLAYERS,
        SPECIFIC_PLAYER
    }

    private enum CHARACTERS_XML_TAGS{
        CLASSNAME,
        POSITION,
        ANIMATION,
        DURATION,
        SOUND
    }

    private String _result;

    private LevelFactory(Context context){
        _context = context;

    }

    public synchronized static LevelFactory getLevelFactory(Context context){
        if(_levelFactory == null){
            _levelFactory = new LevelFactory(context);
        }
        return _levelFactory;
    }

    public ILevel getCurrentLevel() {
        return _currentLevel;
    }

    public void setLevelBackground(Background bck){
        _currentLevel.setLevelBackground(bck);
    }

    public  String getResult(){
        return _result;
    }

    public List<Enemy> getEnemies(){
        return _enemies;
    }

    public List<Players> getPlayers(){
        return _players;
    }

    public BckGround getBackgroundData(){
        return _backGroundData;
    }

    CharacterData _character;
    BckGround _backGroundData;
    int _arrayHeight = 0;
    int _progress = 5;

    private void parseXml(int resourceID){
        try {

            _enemies = new ArrayList<Enemy>();
            _players = new ArrayList<Players>();

            _result = null;

            XmlResourceParser parser = _context.getResources().getXml(resourceID);
            String _name;
            String _text;
            XML_TAGS _switcher = XML_TAGS.CLASSNAME;
            CHARACTERS_XML_TAGS _switchCharacter = CHARACTERS_XML_TAGS.CLASSNAME;
            int _eventType = parser.getEventType();
            while(_eventType != XmlPullParser.END_DOCUMENT){

                switch (_eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        _name = parser.getName();
                        if(_name.toUpperCase().equals("CLASSNAME")){
                            _backGroundData = new BckGround();
                            _result += _name;
                            _switcher = XML_TAGS.CLASSNAME;
                        } else if(_name.toUpperCase().equals("RATIO")){
                            _result += _name;
                            _switcher=XML_TAGS.RATIO;
                        } else if(_name.toUpperCase().equals("BACKGROUND")){
                            _result += _name;
                            _switcher=XML_TAGS.BACKGROUND;
                        } else if(_name.toUpperCase().equals("ROW")){
                            _result += _name;
                            _switcher=XML_TAGS.ROW;
                        } else if(_name.toUpperCase().equals("BACKGROUND_DRAWABLE")){
                            _result += _name;
                            _switcher=XML_TAGS.BACKGROUND_DRAWABLE;
                        } else if(_name.toUpperCase().equals("ENEMIES")){
                            _result += _name;
                            _switcher=XML_TAGS.ENEMIES;
                        } else if(_name.toUpperCase().equals("PLAYERS")){

                            _character = new Players();

                            _result += _name;
                            _switcher = XML_TAGS.SPECIFIC_PLAYER;
                        } else if (_name.toUpperCase().equals("ENEMY")) {

                            _character = new Enemy();

                            _result += _name;
                            _switcher = XML_TAGS.SPECIFIC_ENEMY;
                        } else if(_name.toUpperCase().equals("CHARACHTERCLASSNAME")){
                            _result += _name;
                            _switchCharacter = CHARACTERS_XML_TAGS.CLASSNAME;
                        } else if (_name.toUpperCase().equals("POSITION")){
                            _result += _name;
                            _switchCharacter = CHARACTERS_XML_TAGS.POSITION;
                        } else if (_name.toUpperCase().equals("ANIMATION")){
                            _result += _name;
                            _switchCharacter = CHARACTERS_XML_TAGS.ANIMATION;
                        } else if (_name.toUpperCase().equals("DURATION")){
                            _result += _name;
                            _switchCharacter = CHARACTERS_XML_TAGS.DURATION;
                        } else if (_name.toUpperCase().equals("SOUND")) {
                            _result += _name;
                            _switchCharacter = CHARACTERS_XML_TAGS.SOUND;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        _text = parser.getText();
                        _result += _text;
                        switch (_switcher){
                            case CLASSNAME:

                                break;
                            case RATIO:
                                _backGroundData.setRatio(_text);
                                break;
                            case BACKGROUND:
                                _arrayHeight = 0;
                                break;
                            case ROW:
                                String[] data = _text.split("\\s+");
                                try {
                                    int _arrayWidth = 0;
                                    for (String s : data) {
                                        _backGroundData.Tiles[_arrayHeight][_arrayWidth] = Integer.parseInt(s);
                                        _arrayWidth++;
                                    }
                                    _arrayHeight++;
                                    if(_arrayHeight >= _backGroundData.Height)
                                        _arrayHeight = 0;
                                } catch (IndexOutOfBoundsException e){
                                    if(LoggerConfig.ON) {
                                        Log.e(LOG, "READING TILE DATA, Index out of range: " + e.getMessage());
                                    }
                                }
                                break;
                            case BACKGROUND_DRAWABLE:
                                    _backGroundData.Drawable = _text;
                                break;
                            case ENEMIES:
                                switch (_switchCharacter){
                                    case CLASSNAME:
                                        _character.className = _text;
                                        break;
                                    case POSITION:
                                        _character.position = _text;
                                        break;
                                    case ANIMATION:
                                        _character.animation.add(_text);
                                        break;
                                    case DURATION:
                                        String[] duration = _text.split("\\s+");
                                        for (String i : duration){
                                            _character.duration.add(Integer.parseInt(i));
                                        }
                                        break;
                                    case SOUND:
                                        _character.sound.add(_text);
                                        break;
                                }
                                break;
                            case PLAYERS:
                                switch (_switchCharacter){
                                    case CLASSNAME:
                                        _character.className = _text;
                                        break;
                                    case POSITION:
                                        _character.position = _text;
                                        break;
                                    case ANIMATION:
                                        _character.animation.add(_text);
                                        break;
                                    case DURATION:
                                        String[] duration = _text.split("\\s+");
                                        for (String i : duration){
                                            _character.duration.add(Integer.parseInt(i));
                                        }
                                        break;
                                    case SOUND:
                                        _character.sound.add(_text);
                                        break;
                                }
                                break;
						case SPECIFIC_ENEMY:
							break;
						case SPECIFIC_PLAYER:
							break;
						default:
							break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(_switcher == XML_TAGS.SPECIFIC_ENEMY) {
                            _switcher = XML_TAGS.ENEMIES;
                            if(_character != null)
                                _enemies.add((Enemy)_character);
                            //_character = null;
                        } else if(_switcher == XML_TAGS.SPECIFIC_PLAYER){
                            _switcher = XML_TAGS.PLAYERS;
                            if(_character != null)
                                _players.add((Players)_character);
                            //_character = null;
                        }

                        _result += "\n";
                        break;

                }
                _eventType = parser.next();


            }

            _reporter.reportProgress(_progress);


        } catch (XmlPullParserException e) {
            if(LoggerConfig.ON) {
                Log.e(LOG,"Xml.Parser.ERROR: "+ e.getMessage());
            }
        } catch (IOException e){
            if(LoggerConfig.ON) {
                Log.e(LOG, "Xml.Parser.ERROR: "+ e.getMessage());
            }
        }
    }

}