package fr.quentinklein.stickynotifs.ui.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

import java.sql.SQLException;

import fr.quentinklein.stickynotifs.R;
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
public class NoteFragment extends Fragment implements NoteChanedListener, HideNoteListener {

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;
    @ViewById(R.id.radios)
    RadioGroup radioGroup;
    @ViewById(R.id.stick)
    Switch noteSwitch;
    @ViewById(R.id.title)
    EditText noteTitle;
    @ViewById(R.id.content)
    EditText noteContent;
    @ViewById(R.id.note_layout)
    RelativeLayout layout;
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
        layout.setVisibility(View.VISIBLE);
        if (notification != null) {
            Log.i(NoteFragment.class.getSimpleName(), "Note is not null");
            isEditing = true;
            noteTitle.setText(notification.getTitle());
            noteTitle.setSelection(notification.getTitle().length());
            noteContent.setText(notification.getContent());
            noteContent.setSelection(notification.getContent().length());
            noteSwitch.setChecked(notification.isNotification());
            switch (notification.getDefcon()) {
                case USELESS:
                    radioGroup.check(R.id.useless);
                    break;
                case NORMAL:
                    radioGroup.check(R.id.normal);
                    break;
                case IMPORTANT:
                    radioGroup.check(R.id.important);
                    break;
                case ULTRA:
                    radioGroup.check(R.id.ultra);
                    break;
            }
            defconChanged();
        } else {
            Log.i(NoteFragment.class.getSimpleName(), "Note is null");
            notification = new StickyNotification();
            noteTitle.setText(null);
            noteContent.setText(null);
            noteSwitch.setChecked(true);
            radioGroup.check(R.id.normal);
            isEditing = false;
        }
    }

    @Click(R.id.validate)
    void saveNote() {
        String title = noteTitle.getText().toString();
        if (title != null && title.length() > 0) {
            notification.setContent(noteContent.getText().toString());
            StickyNotification.Defcon defcon;
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.useless:
                    defcon = StickyNotification.Defcon.USELESS;
                    break;
                case R.id.normal:
                    defcon = StickyNotification.Defcon.NORMAL;
                    break;
                case R.id.important:
                    defcon = StickyNotification.Defcon.IMPORTANT;
                    break;
                case R.id.ultra:
                    defcon = StickyNotification.Defcon.ULTRA;
                    break;
                default:
                    defcon = StickyNotification.Defcon.NORMAL;
                    break;
            }
            notification.setDefcon(defcon);
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
        } else {
            String error = getActivity().getString(R.string.create_title_empty);
            noteTitle.setError((CharSequence) error);
        }
    }

    @Click({R.id.useless, R.id.normal, R.id.important, R.id.ultra})
    void defconChanged() {
        if (getActivity() instanceof ChangeIconListener) {
            ChangeIconListener listener = (ChangeIconListener) getActivity();
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.useless:
                    listener.setActivityIcon(R.drawable.blue_square_paper);
                    break;
                case R.id.normal:
                    listener.setActivityIcon(R.drawable.green_square_paper);
                    break;
                case R.id.important:
                    listener.setActivityIcon(R.drawable.orange_square_paper);
                    break;
                case R.id.ultra:
                    listener.setActivityIcon(R.drawable.red_square_paper);
                    break;
            }
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

    /**
     * For tablet master-detail layout
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#hideNote()
     */
    @Override
    public void hideNote() {
        layout.setVisibility(View.INVISIBLE);
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
     * Not used for now
     */
    public void clear() {
        notification = null;
        refreshElements();
    }
}
