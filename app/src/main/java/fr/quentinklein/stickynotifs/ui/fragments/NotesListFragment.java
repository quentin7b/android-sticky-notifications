package fr.quentinklein.stickynotifs.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;
import com.melnykov.fab.FloatingActionButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;
import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;

/**
 * Created by quentin on 20/07/2014.
 * All notes list
 */
@EFragment(R.layout.fragment_list_notes)
public class NotesListFragment extends Fragment {
    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

    @ViewById(R.id.sticky_list)
    ListView cardsView;

    @ViewById(R.id.add_card_fab)
    FloatingActionButton addCardButton;

    @Bean
    NotificationHelper notificationHelper;

    StickyNotificationAdapter adapter;
    List<StickyNotification> notifications;

    List<StickyNotification> debugNotifications;

    private StickyNotification.Defcon mDefconFilter = null;

    @AfterViews
    void attachButton() {
        addCardButton.attachToListView(cardsView);
        notifications = new ArrayList<StickyNotification>(0);
        adapter = new StickyNotificationAdapter(getActivity().getLayoutInflater(), notifications);
        cardsView.setAdapter(adapter);
        if (BuildConfig.DEBUG) {
            debugNotifications = new ArrayList<StickyNotification>(4);
            String[] ultraInfo = getResources().getStringArray(R.array.ultra_notification);
            StickyNotification ultra = new StickyNotification();
            ultra.setId(0);
            ultra.setDefcon(StickyNotification.Defcon.ULTRA);
            ultra.setTitle(ultraInfo[0]);
            ultra.setContent(ultraInfo[1]);
            ultra.setNotification(true);
            debugNotifications.add(ultra);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshNotesList();
    }

    @Click(R.id.add_card_fab)
    void addCard() {
        if (getActivity() instanceof NotesListActivity) {
            ((NotesListActivity) getActivity()).addNote();
        }
    }

    /**
     * Reload the list of notifications to display
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#noteSaved(int)
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#onResume()
     */
    public void refreshNotesList() {
        try {
            if (notifications == null) {
                notifications = new ArrayList<StickyNotification>();
            }
            notifications.clear();
            if (mDefconFilter == null) {
                notifications.addAll(stickyNotificationDao.queryForAll());
            } else {
                notifications.addAll(NotificationHelper.getDefconNotifications(stickyNotificationDao.queryForAll(), mDefconFilter));
            }
            Collections.sort(notifications);
            notificationHelper.showNotifications(notifications);
            adapter.notifyDataSetChanged();
        } catch (SQLException e) {
            Log.e(NotesListFragment.class.getSimpleName(), "Error while requesting notes", e);
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
        if (notifications.size() == 0) {
            // hide detail
            if (getActivity() instanceof HideNoteListener) {
                ((HideNoteListener) getActivity()).hideNote();
            }
        }
    }

    /**
     * Will be used with special tablet layout
     */
    public static interface TwoPartProvider {
        public boolean isTwoPartMode();
    }

    static class ViewHolder {
        public TextView stickyTitle;
        public TextView stickyDescription;
        public ImageView stickyColor;
        public Button editButton;
        public Button deleteButton;
    }

    private class StickyNotificationAdapter extends ArrayAdapter<StickyNotification> {

        private LayoutInflater layoutInflater;

        public StickyNotificationAdapter(final LayoutInflater layoutInflater, final List<StickyNotification> objects) {
            super(getActivity(), R.layout.card_sticky, objects);
            this.layoutInflater = layoutInflater;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                rowView = layoutInflater.inflate(R.layout.card_sticky, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.stickyTitle = (TextView) rowView.findViewById(R.id.card_title);
                viewHolder.stickyDescription = (TextView) rowView.findViewById(R.id.card_extra);
                viewHolder.stickyColor = (ImageView) rowView.findViewById(R.id.color_view);
                viewHolder.editButton = (Button) rowView.findViewById(R.id.edit_note);
                viewHolder.deleteButton = (Button) rowView.findViewById(R.id.delete_note);
                rowView.setTag(viewHolder);
            }

            // fill data
            final StickyNotification notification = getItem(position);
            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.stickyTitle.setText(notification.getTitle());
            holder.stickyTitle.setTextColor(Color.parseColor(notification.getHexColor()));
            holder.stickyDescription.setText(notification.getContent());
            if (notification.isNotification()) {
                holder.stickyColor.setImageResource(getIconResource(notification));
            } else {
                holder.stickyColor.setImageDrawable(null);
            }
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int id = notification.getId();
                        notification.delete();
                        Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
                        notificationHelper.hideAll();
                        NotesListFragment.this.refreshNotesList();
                        if (getActivity() instanceof NoteDeletedListener) {
                            ((NoteDeletedListener) getActivity()).noteDeleted(id);
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
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof NoteChanedListener) {
                        ((NoteChanedListener) getActivity()).noteSelected(notification.getId());
                    }
                }
            });
            return rowView;
        }

        private int getIconResource(StickyNotification notification) {
            switch (notification.getDefcon()) {
                case USELESS:
                    return R.drawable.blue_square_paper;
                case NORMAL:
                    return R.drawable.green_square_paper;
                case IMPORTANT:
                    return R.drawable.orange_square_paper;
                case ULTRA:
                    return R.drawable.red_square_paper;
                default:
                    return R.drawable.ic_launcher;
            }
        }
    }


    public void setDefconFilter(StickyNotification.Defcon defconFilter) {
        mDefconFilter = defconFilter;
        refreshNotesList();
    }
}
