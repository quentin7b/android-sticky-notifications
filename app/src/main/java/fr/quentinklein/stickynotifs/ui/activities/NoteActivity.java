package fr.quentinklein.stickynotifs.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.analytics.tracking.android.EasyTracker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
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
@EActivity(R.layout.activity_note)
@OptionsMenu(R.menu.note)
public class NoteActivity extends AppCompatActivity implements NoteSavedListener, NoteDeletedListener, ChangeIconListener {

    /**
     * Not used for now
     */
    @Extra
    int notificationId = -1;

    @Pref
    NotificationPreferences_ preferences;

    @FragmentById(R.id.note_fragment)
    NoteFragment fragment;

    @Bean
    NotificationHelper notificationHelper;

    /**
     * Functions
     */

    @AfterViews
    void init() {
        if (!BuildConfig.DEBUG && preferences.analytics().get()) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        boolean edition = notificationId != -1;

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle(edition ? R.string.edit_title : R.string.create_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_done_white_24dp);

        if (edition) {
            fragment.noteSelected(notificationId);
        }
    }

    /**
     * Menu items
     */

    @OptionsItem(android.R.id.home)
    void homeSelected() {
        fragment.saveNote();
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
