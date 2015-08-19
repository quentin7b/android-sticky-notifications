package fr.quentinklein.stickynotifs.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteSavedListener;

/**
 * Created by quentin on 20/07/2014.
 * Note creation/edition page
 */
@EFragment(R.layout.fragment_note)
public class NoteFragment extends Fragment implements NoteChanedListener {

    private EventBus mEventBus;

    @Bean
    StickyNotificationManager mStickyNotificationManager;
    @ViewById(R.id.stick)
    SwitchCompat noteSwitch;
    @ViewById(R.id.note_title)
    EditText noteTitle;
    @ViewById(R.id.content)
    EditText noteContent;
    @ViewById(R.id.useless)
    ImageButton uselessButton;
    @ViewById(R.id.normal_btn)
    ImageButton normalButton;
    @ViewById(R.id.important)
    ImageButton importantButton;
    @ViewById(R.id.ultra)
    ImageButton ultraButton;
    StickyNotification.Defcon notificationDefcon;
    /**
     * Notification to use
     */
    StickyNotification notification;
    /**
     * Used to know if we should update or insert notification
     */
    boolean isEditing;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = new EventBus();
    }

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
            noteTitle.setText(notification.getTitle());
            noteContent.setText(notification.getContent());
            noteSwitch.setChecked(notification.isNotification());
            checkIcon(notification.getDefcon());
            isEditing = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.post(new DefconEvent(notification.getDefcon()));
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
        if (!title.trim().isEmpty()) {
            notification.setContent(noteContent.getText().toString());
            notification.setDefcon(notificationDefcon);
            notification.setTitle(noteTitle.getText().toString());
            notification.setNotification(noteSwitch.isChecked());
            mStickyNotificationManager.saveNotification(notification);
            Toast.makeText(getActivity(), R.string.note_saved, Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof NoteSavedListener) {
                ((NoteSavedListener) getActivity()).noteSaved(notification.getId());
            }
        } else {
            String error = getActivity().getString(R.string.create_title_empty);
            noteTitle.setError((CharSequence) error);
        }
    }

    @Click({R.id.useless, R.id.normal, R.id.important, R.id.ultra})
    void defconChanged(View view) {
        StickyNotification.Defcon newDefcon = null;
        switch (view.getId()) {
            case R.id.useless:
                newDefcon = StickyNotification.Defcon.USELESS;
                break;
            case R.id.normal:
                newDefcon = StickyNotification.Defcon.NORMAL;
                break;
            case R.id.important:
                newDefcon = StickyNotification.Defcon.IMPORTANT;
                break;
            case R.id.ultra:
                newDefcon = StickyNotification.Defcon.ULTRA;
                break;
        }
        checkIcon(newDefcon);
        mEventBus.post(new DefconEvent(newDefcon));
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
        notification = mStickyNotificationManager.getNotification(noteId);
        refreshElements();
    }

    /**
     * Delete the current note
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NoteActivity#deleteNote()
     */
    public void deleteNote() {
        if (isEditing) {
            int oldId = notification.getId();
            mStickyNotificationManager.deleteNotification(notification);
            Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof NoteDeletedListener) {
                ((NoteDeletedListener) getActivity()).noteDeleted(oldId);
            }
        } else {
            // Delete note while creating
            getActivity().finish();
        }
    }

    public EventBus getBus() {
        return mEventBus;
    }

    public class DefconEvent {
        public final StickyNotification.Defcon newDefcon;

        public DefconEvent(StickyNotification.Defcon defcon) {
            newDefcon = defcon;
        }
    }

}
