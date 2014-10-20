package fr.quentinklein.stickynotifs.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.analytics.tracking.android.EasyTracker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.ui.fragments.NoteFragment;
import fr.quentinklein.stickynotifs.ui.listeners.ChangeIconListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteSavedListener;

/**
 * Created by quentin on 19/07/2014.
 * Creation / Edition of a notification
 *
 * @see fr.quentinklein.stickynotifs.ui.fragments.NoteFragment
 */
@EActivity
@OptionsMenu(R.menu.note)
public class NoteActivity extends ActionBarActivity implements NoteSavedListener, NoteDeletedListener, ChangeIconListener {

    /**
     * Not used for now
     */
    @Extra
    int notificationId = -1;

    @FragmentById(R.id.note_fragment)
    NoteFragment fragment;

    @Bean
    NotificationHelper notificationHelper;

    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        EasyTracker.getInstance(this).activityStart(this);

        toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Functions
     */

    @AfterViews
    void init() {
        if (notificationId != -1) {
            fragment.noteSelected(notificationId);
        }
    }

    /**
     * Menu items
     */

    @OptionsItem(android.R.id.home)
    void homeSelected() {
        finish();
    }

    @OptionsItem(R.id.action_delete)
    void deleteNote() {
        fragment.deleteNote();
    }

    /**
     * Interfaces
     */

    @Override
    public void noteSaved(int noteId) {
        finish();
    }

    @Override
    public void noteDeleted(int noteId) {
        notificationHelper.hideNotification(noteId);
        finish();
    }

    @Override
    public void setActivityIcon(int iconResource) {
        // getSupportActionBar().setIcon(iconResource);
        // actionBar.setIcon(iconResource);
    }
}
