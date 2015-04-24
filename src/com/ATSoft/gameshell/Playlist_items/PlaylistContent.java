package com.ATSoft.gameshell.Playlist_items;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.ATSoft.GameFramework.Util.LoggerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 *
 */
public class PlaylistContent {
    /**
     * Logger constant
     */
    private static final String LOG="PlaylistContent";

    /**
     * An array of sample (dummy) items.
     */
    public static List<PlaylistItem> ITEMS = new ArrayList<PlaylistItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, PlaylistItem> ITEM_MAP = new HashMap<String, PlaylistItem>();

    /**
     * passed context
     */
    private static Context _setContext;
    private static PlaylistContent _plk;
    private PlaylistContent(Context cont){
        if(LoggerConfig.ON) {
            Log.i(LOG, "Constructor... entering.");
        }
        _setContext = cont;
        try {
            String[] proj = {"*"};
            Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor playListCursor = _setContext.getContentResolver().query(tempPlaylistURI, proj, null, null, null);

            if (playListCursor != null) {
                while(playListCursor.moveToNext()){
                    String _playlistIdValue = Long.toString(playListCursor.getLong(playListCursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
                    String _playlistName = playListCursor.getString(playListCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                    addItem(new PlaylistItem(_playlistIdValue, _playlistName));
                }
            }
            playListCursor.close();
        } catch (Exception e){
            if(LoggerConfig.ON) {
                Log.e(LOG, "Error in retrieve play lists, " + e.getMessage());
            }
        }

    }
    public static PlaylistContent getPlayListContent(Context cont) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "Access method... entering.");
        }
        if(_plk == null){
            _plk = new PlaylistContent(cont);
        }
        return _plk;
    }


    private static void addItem(PlaylistItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class PlaylistItem {
        public String id;
        public String content;

        public PlaylistItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
