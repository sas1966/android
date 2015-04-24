package com.ATSoft.gameshell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import com.ATSoft.GameFramework.Util.GameResourceManager;
import com.ATSoft.GameFramework.Util.LoggerConfig;


/**
 * An activity representing a list of playlists. This activity
 * has different presentations for handset and tablet-size devices. On
 * handhelds, the activity presents a list of items, which when touched,
 * lead to a representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link playlistListFragment} and the item details
 * (if present) is a
 * <p/>
 * This activity also implements the required
 * {@link playlistListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class playlistListActivity extends ActionBarActivity
        implements playlistListFragment.Callbacks {
    /**
     * Logger constant
     */
    private static final String LOG="PlaylistACTIVITY";


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "onCreate... entering.");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_list);
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((playlistListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.playlist_list))
                    .setActivateOnItemClick(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "onOptionItemSelected... entering.");
        }
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link playlistListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if(LoggerConfig.ON) {
            Log.i(LOG, "OnItemSelected Callback function... entering.");
        }
        // Set playback list and start playing
        Intent inty = new Intent(this, OptionActivity.class);
        inty.putExtra("PlaylistID", id);
        if(getParent() == null) {
            setResult(Activity.RESULT_OK, inty);
        } else {
            getParent().setResult(Activity.RESULT_OK, inty);
        }
        finish();
    }

    @Override
    protected void onUserLeaveHint(){
        if(true) {
            startService(GameResourceManager.Music.Stop());
            Intent intent = new Intent(getApplicationContext(), startActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            intent.putExtra("SENDER", "PlayListActivity");
            startActivity(intent);
            finish();
        }
    }
}

