package fr.quentinklein.stickynotifs.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;
import fr.quentinklein.stickynotifs.ui.DividerItemDecoration;
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
    private static final String TAG = "NotesListFragment";
    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

    @ViewById(R.id.recyclerview)
    RecyclerView recyclerView;

    @FragmentArg
    StickyNotification.Defcon defcon;

    @Pref
    NotificationPreferences_ preferences;

    @Bean
    NotificationHelper notificationHelper;


    StickyNotificationAdapter adapter;
    List<StickyNotification> notifications;

    @AfterViews
    void initLayout() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        notifications = new ArrayList<>(0);
        adapter = new StickyNotificationAdapter();
        recyclerView.setAdapter(adapter);
        Drawable divider;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            divider = getResources().getDrawable(R.drawable.card_divider, getActivity().getTheme());
        } else {

            divider = getResources().getDrawable(R.drawable.card_divider);
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(divider));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshNotesList();
    }

    /**
     * Reload the list of notifications to display
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#noteSaved(int)
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#onResume()
     */
    public void refreshNotesList() {
        if (stickyNotificationDao == null) {
            return;
        } else {
            try {
                if (notifications == null) {
                    notifications = new ArrayList<>();
                }
                notifications.clear();
                List<StickyNotification> allNotifications = stickyNotificationDao.queryForAll();
                if (defcon == null) {
                    notifications.addAll(allNotifications);
                } else {
                    notifications.addAll(NotificationHelper.getDefconNotifications(allNotifications, defcon));
                }
                Collections.sort(notifications);
                adapter.notifyDataSetChanged();
            } catch (SQLException e) {
                Log.e(NotesListFragment.class.getSimpleName(), "Error while requesting notes", e);
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
            if (notifications.isEmpty()) {
                // hide detail
                if (getActivity() instanceof HideNoteListener) {
                    ((HideNoteListener) getActivity()).hideNote();
                }
            }
        }
    }

    private class StickyNotificationAdapter extends RecyclerView.Adapter<StickyNotificationAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.card_sticky, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.bind(notifications.get(i));
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            View baseView;
            TextView stickyTitle;
            TextView stickyDescription;
            ImageView stickyColor;
            ImageView stickyIcon;

            public ViewHolder(View itemView) {
                super(itemView);
                baseView = itemView;
                stickyTitle = (TextView) itemView.findViewById(R.id.card_title);
                stickyDescription = (TextView) itemView.findViewById(R.id.card_extra);
                stickyColor = (ImageView) itemView.findViewById(R.id.color_view);
                stickyIcon = (ImageView) itemView.findViewById(R.id.color_icon);
            }

            public void bind(final StickyNotification notification) {
                stickyTitle.setText(notification.getTitle());
                String description = notification.getContent();
                if (description != null && !description.trim().isEmpty()) {
                    stickyDescription.setVisibility(View.VISIBLE);
                    stickyDescription.setText(notification.getContent());
                } else {
                    stickyDescription.setVisibility(View.GONE);
                }
                stickyColor.setImageResource(getIconColor(notification));
                stickyIcon.setImageResource(notificationHelper.getSmallIconResource(notification));
                baseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof NoteChanedListener) {
                            ((NoteChanedListener) getActivity()).noteSelected(notification.getId());
                        }
                    }
                });
            }

            public int getIconColor(StickyNotification notification) {
                switch (notification.getDefcon()) {
                    case USELESS:
                        return R.drawable.circle_blue;
                    case NORMAL:
                        return R.drawable.circle_green;
                    case IMPORTANT:
                        return R.drawable.circle_orange;
                    case ULTRA:
                        return R.drawable.circle_red;
                    default:
                        return R.drawable.ic_launcher;
                }
            }
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

}
