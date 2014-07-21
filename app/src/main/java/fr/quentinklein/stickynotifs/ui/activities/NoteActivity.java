package fr.quentinklein.stickynotifs.ui.activities;

import android.support.v7.app.ActionBarActivity;

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
 */
@EActivity(R.layout.activity_note)
@OptionsMenu(R.menu.note)
public class NoteActivity extends ActionBarActivity implements NoteSavedListener, NoteDeletedListener, ChangeIconListener {

    @Extra
    int notificationId = -1;

    @FragmentById(R.id.note_fragment)
    NoteFragment fragment;

    @Bean
    NotificationHelper notificationHelper;

    @AfterViews
    void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (notificationId != -1) {
            fragment.noteSelected(notificationId);
        }
    }

    @OptionsItem(android.R.id.home)
    void homeSelected() {
        finish();
    }

    @OptionsItem(R.id.action_delete)
    void deleteNote() {
        fragment.deleteNote();
    }

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
        getSupportActionBar().setIcon(iconResource);
    }
}
