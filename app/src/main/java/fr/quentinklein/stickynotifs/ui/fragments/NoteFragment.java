package fr.quentinklein.stickynotifs.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;
import fr.quentinklein.stickynotifs.ui.listeners.ChangeIconListener;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteSavedListener;

/**
 * Created by quentin on 20/07/2014.
 * Note creation/edition page
 */
@EFragment(R.layout.fragment_note)
public class NoteFragment extends Fragment implements NoteChanedListener {

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;
    @ViewById(R.id.stick)
    SwitchCompat noteSwitch;
    @ViewById(R.id.title)
    EditText noteTitle;
    @ViewById(R.id.content)
    EditText noteContent;
    @ViewById(R.id.useless)
    ImageButton uselessButton;
    @ViewById(R.id.normal)
    ImageButton normalButton;
    @ViewById(R.id.important)
    ImageButton importantButton;
    @ViewById(R.id.ultra)
    ImageButton ultraButton;
    StickyNotification.Defcon notificationDefcon;
    @Pref
    NotificationPreferences_ preferences;
    /**
     * Notification to use
     */
    StickyNotification notification;
    /**
     * Used to know if we should update or insert notification
     */
    boolean isEditing;


    @AfterViews
    void refreshElements() {
        if (notification != null) {
            Log.i(NoteFragment.class.getSimpleName(), "Note is not null");
            isEditing = true;
            noteTitle.setText(notification.getTitle());
            noteTitle.setSelection(notification.getTitle().length());
            noteContent.setText(notification.getContent());
            noteContent.setSelection(notification.getContent().length());
            noteSwitch.setChecked(notification.isNotification());
            checkIcon(notification.getDefcon());
        } else {
            Log.i(NoteFragment.class.getSimpleName(), "Note is null");
            notification = new StickyNotification();
            noteTitle.setText(null);
            noteContent.setText(null);
            noteSwitch.setChecked(true);
            checkIcon(StickyNotification.Defcon.NORMAL);
            isEditing = false;
        }
    }

    private void checkIcon(StickyNotification.Defcon defcon) {
        uselessButton.setBackgroundResource(R.drawable.circle_grey);
        normalButton.setBackgroundResource(R.drawable.circle_grey);
        importantButton.setBackgroundResource(R.drawable.circle_grey);
        ultraButton.setBackgroundResource(R.drawable.circle_grey);
        switch (defcon) {
            case USELESS:
                uselessButton.setBackgroundResource(R.drawable.circle_blue);
                break;
            case NORMAL:
                normalButton.setBackgroundResource(R.drawable.circle_green);
                break;
            case IMPORTANT:
                importantButton.setBackgroundResource(R.drawable.circle_orange);
                break;
            case ULTRA:
                ultraButton.setBackgroundResource(R.drawable.circle_red);
                break;
        }
        notificationDefcon = defcon;
    }

    public void saveNote() {
        String title = noteTitle.getText().toString();
        if (title != null && !title.trim().isEmpty()) {
            notification.setContent(noteContent.getText().toString());
            notification.setDefcon(notificationDefcon);
            notification.setTitle(noteTitle.getText().toString());
            notification.setNotification(noteSwitch.isChecked());
            notification.setDao(stickyNotificationDao);
            try {
                if (isEditing) {
                    notification.update();
                } else {
                    notification.create();
                }
                Toast.makeText(getActivity(), R.string.note_saved, Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof NoteSavedListener) {
                    ((NoteSavedListener) getActivity()).noteSaved(notification.getId());
                }
            } catch (SQLException exception) {
                Log.e(NoteFragment.class.getSimpleName(), "Error while saving note", exception);
                Toast.makeText(getActivity(), R.string.note_saved_error, Toast.LENGTH_SHORT).show();
                if (!BuildConfig.DEBUG && preferences.analytics().get()) {
                    EasyTracker.getInstance(getActivity().getApplicationContext()).send(
                            MapBuilder.createException(
                                    new StandardExceptionParser(getActivity(), null)
                                            // Context and optional collection of package names to be used in reporting the exception.
                                            .getDescription(Thread.currentThread().getName(),
                                                    // The name of the thread on which the exception occurred.
                                                    exception),                                  // The exception.
                                    false
                            ).build()
                    );
                }
            }
        } else {
            String error = getActivity().getString(R.string.create_title_empty);
            noteTitle.setError((CharSequence) error);
        }
    }

    @Click({R.id.useless, R.id.normal, R.id.important, R.id.ultra})
    void defconChanged(View view) {
        switch (view.getId()) {
            case R.id.useless:
                checkIcon(StickyNotification.Defcon.USELESS);
                break;
            case R.id.normal:
                checkIcon(StickyNotification.Defcon.NORMAL);
                break;
            case R.id.important:
                checkIcon(StickyNotification.Defcon.IMPORTANT);
                break;
            case R.id.ultra:
                checkIcon(StickyNotification.Defcon.ULTRA);
                break;
        }
    }

    /**
     * Fill the fields with a specific notification
     *
     * @param noteId the notification to use
     * @see fr.quentinklein.stickynotifs.ui.activities.NoteActivity#init()
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#noteSelected(int)
     */
    @Override
    public void noteSelected(int noteId) {
        try {
            notification = stickyNotificationDao.queryForId(noteId);
            refreshElements();
        } catch (SQLException e) {
            Log.e(NoteFragment.class.getSimpleName(), "Can't retrieve note", e);
            Toast.makeText(getActivity(), R.string.note_retrive_error, Toast.LENGTH_SHORT).show();
            if (!BuildConfig.DEBUG && preferences.analytics().get()) {
                EasyTracker.getInstance(getActivity().getApplicationContext()).send(
                        MapBuilder.createException(
                                new StandardExceptionParser(getActivity(), null)
                                        // Context and optional collection of package names to be used in reporting the exception.
                                        .getDescription(Thread.currentThread().getName(),
                                                // The name of the thread on which the exception occurred.
                                                e),                                  // The exception.
                                false
                        ).build()
                );
            }
        }
    }

    /**
     * Delete the current note
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NoteActivity#deleteNote()
     */
    public void deleteNote() {
        if (isEditing) {
            try {
                int oldId = notification.getId();
                notification.delete();
                Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof NoteDeletedListener) {
                    ((NoteDeletedListener) getActivity()).noteDeleted(oldId);
                }
            } catch (SQLException e) {
                Log.e(NoteFragment.class.getSimpleName(), "Error while deleting note", e);
                Toast.makeText(getActivity(), R.string.note_deleted_error, Toast.LENGTH_SHORT).show();

                if (!BuildConfig.DEBUG && preferences.analytics().get()) {
                    EasyTracker.getInstance(getActivity().getApplicationContext()).send(
                            MapBuilder.createException(
                                    new StandardExceptionParser(getActivity(), null)
                                            // Context and optional collection of package names to be used in reporting the exception.
                                            .getDescription(Thread.currentThread().getName(),
                                                    // The name of the thread on which the exception occurred.
                                                    e),                                  // The exception.
                                    false
                            ).build()
                    );
                }
            }
        } else {
            // Delete note while creating
            getActivity().finish();
        }
    }

}
