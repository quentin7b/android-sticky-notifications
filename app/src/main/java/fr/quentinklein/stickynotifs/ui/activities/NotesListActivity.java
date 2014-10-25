package fr.quentinklein.stickynotifs.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.analytics.tracking.android.EasyTracker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.fragments.NoteFragment;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteSavedListener;
import fr.quentinklein.stickynotifs.widget.StickyWidgetProvider;

/**
 * Created by quentin on 19/07/2014.
 * List of notifications
 *
 * @see fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment
 */
@EActivity
@OptionsMenu(R.menu.notes)
public class NotesListActivity extends ActionBarActivity implements NoteSavedListener, NoteChanedListener, HideNoteListener, NoteDeletedListener, NotesListFragment.TwoPartProvider {

    public static final String EXTRA_NOTE_ID = "note_id";

    @FragmentById(R.id.notes_fragment)
    NotesListFragment fragment;

    @ViewById(R.id.spinner)
    Spinner spinner;

    @ViewById(R.id.spinner_layout)
    RelativeLayout filterLayout;

    /**
     * Not used for now (special tablet layout)
     */
    @FragmentById(R.id.note_fragment)
    NoteFragment noteFragment;

    @Pref
    NotificationPreferences_ preferences;


    @Override
    public void hideNote() {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.hideNote();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSpinnerHidden = filterLayout.getVisibility() == View.GONE;
        if (isSpinnerHidden) {
            // Spinner is hidden
            if (!preferences.hideFilter().get()) {
                // It should not be hidden
                filterLayout.setVisibility(View.VISIBLE);
                spinner.setSelection(0);
                fragment.setDefconFilter(null);
                fragment.refreshNotesList();
            }
        } else {
            // Spinner is visible
            if (preferences.hideFilter().get()) {
                // It should be hidden
                filterLayout.setVisibility(View.GONE);
                spinner.setSelection(0);
                fragment.setDefconFilter(null);
                fragment.refreshNotesList();
            }
        }
        reloadWidgets();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notes);
        EasyTracker.getInstance(this).activityStart(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    /**
     * Functions
     */


    @AfterViews
    void log() {
        fragment.refreshNotesList();
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_data, R.layout.adapter_spinner_filter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // No filter;
                        fragment.setDefconFilter(null);
                        fragment.refreshNotesList();
                        break;
                    case 1:
                        // Only ultra
                        fragment.setDefconFilter(StickyNotification.Defcon.ULTRA);
                        fragment.refreshNotesList();
                        break;
                    case 2:
                        // Only important
                        fragment.setDefconFilter(StickyNotification.Defcon.IMPORTANT);
                        fragment.refreshNotesList();
                        break;
                    case 3:
                        // Only base
                        fragment.setDefconFilter(StickyNotification.Defcon.NORMAL);
                        fragment.refreshNotesList();
                        break;
                    case 4:
                        // Only useless
                        fragment.setDefconFilter(StickyNotification.Defcon.USELESS);
                        fragment.refreshNotesList();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);
        reloadWidgets();
    }


    /**
     * Menu items.
     */
    public final void addNote() {
        startActivity(new Intent(NotesListActivity.this, NoteActivity_.class));
        overridePendingTransition(R.anim.pop_from_bottom_right, R.anim.nothing);
    }

    @OptionsItem(R.id.action_about)
    void about() {
        startActivity(new Intent(this, AboutDialogActivity_.class));
    }

    @OptionsItem(R.id.action_settings)
    void configure() {
        startActivity(new Intent(this, SettingsActivity_.class));
        overridePendingTransition(R.anim.pop_from_top, R.anim.nothing);
    }

    @OptionsItem(R.id.action_delete)
    void delete() {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.deleteNote();
        }
    }

    /**
     * Interfaces.
     */

    @Override
    public void noteSaved(int noteId) {
        Log.i(NotesListActivity.class.getSimpleName(), "Note saved");
        fragment.refreshNotesList();
        reloadWidgets();
    }

    @Override
    public void noteSelected(int noteId) {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.noteSelected(noteId);
        } else {
            startActivity(new Intent(this, NoteActivity_.class).putExtra(NoteActivity_.NOTIFICATION_ID_EXTRA, noteId));
        }
    }

    @Override
    public void noteDeleted(int noteId) {
        Log.i(NotesListActivity.class.getSimpleName(), "Note deleted");
        fragment.refreshNotesList();
        reloadWidgets();
    }

    /**
     * Tablet mode provider
     *
     * @return true if the master-detail on tablet is on
     */
    @Override
    public boolean isTwoPartMode() {
        return (noteFragment != null && noteFragment.isInLayout());
    }

    private void reloadWidgets() {
        /*Log.i(NotesListActivity.class.getSimpleName(), "ReloadWidgets");
        // Refresh widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] stickyWidgetIds =
                appWidgetManager.getAppWidgetIds(
                        new ComponentName(getApplicationContext(), StickyWidgetProvider.class));
        // Notify dataset changed
        appWidgetManager.notifyAppWidgetViewDataChanged(stickyWidgetIds, R.layout.widget_sticky);*/
        sendBroadcast(new Intent(this, StickyWidgetProvider.class)
                .setAction(StickyWidgetProvider.UPDATE_LIST));
    }
}
