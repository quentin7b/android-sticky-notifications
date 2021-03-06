package fr.quentinklein.stickynotifs.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.DefconUtils;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;

/**
 * Created by quentin on 20/07/2014.
 * All notes list
 */
@EFragment(R.layout.fragment_list_notes)
public class NotesListFragment extends Fragment {

    public static final String FILTER_EVENT = "fr.quentinklein.stickynotifs.filter";

    @Bean
    StickyNotificationManager mStickyNotificationManager;

    @ViewById(R.id.recyclerview)
    RecyclerView recyclerView;

    @Pref
    NotificationPreferences_ preferences;

    @Bean
    NotificationHelper notificationHelper;

    StickyNotificationAdapter adapter;
    List<StickyNotification> notifications;
    List<StickyNotification> visibleNotifications;
    private String mCurrentFilter;
    private EventBus mUpdateBus;

    public NotesListFragment() {
        mUpdateBus = new EventBus();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String filter = intent.getStringExtra("extra_filter");
                onNewTextFilter(filter);
            }
        }, new IntentFilter(FILTER_EVENT));
    }

    @AfterViews
    void initLayout() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        notifications = new ArrayList<>(0);
        adapter = new StickyNotificationAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.LEFT, ItemTouchHelper.LEFT) {

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_24dp);

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        // callback for drag-n-drop, false to skip this feature
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        // callback for swipe to dismiss, removing item from data and adapter
                        removeVisual(viewHolder.getAdapterPosition());
                    }

                    @Override
                    public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                        Log.i("Swipe", dX + ", " + dY + ", " + actionState + ", " + isCurrentlyActive);
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            View itemView = viewHolder.itemView;
                            // Draw the background
                            Paint paint = new Paint();
                            paint.setColor(getResources().getColor(DefconUtils.getDefconColorResource(visibleNotifications.get(viewHolder.getAdapterPosition()).getDefcon())));
                            float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                            float bitmapWidth = bitmap.getWidth();
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getTop(),
                                    (float) itemView.getRight(),
                                    (float) itemView.getBottom(),
                                    paint);
                            // Draw the icon
                            c.drawBitmap(bitmap, ((float) itemView.getRight() - bitmapWidth) - 72f, (float) itemView.getTop() + height, paint);
                            // Draw the shadow
                            Paint shwdowPaint = new Paint();
                            shwdowPaint.setColor(Color.BLACK);
                            shwdowPaint.setAlpha(25);
                            // on top
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getTop(),
                                    (float) itemView.getRight(),
                                    (float) itemView.getTop() + 5f,
                                    shwdowPaint
                            );
                            // on left
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getTop() + 5, // prevent collapse with top shadow
                                    (float) itemView.getRight() + dX + 5f,
                                    (float) itemView.getBottom() - 5, // prevent collapse with bottom shadow
                                    shwdowPaint
                            );
                            // on bottom
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getBottom() - 5f,
                                    (float) itemView.getRight(),
                                    (float) itemView.getBottom(),
                                    shwdowPaint
                            );

                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    }

                });
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeVisual(final int adapterPosition) {
        // Copy the item
        StickyNotification removedNotification = visibleNotifications.get(adapterPosition);
        final StickyNotification copyNotification = new StickyNotification(removedNotification);
        notifications.remove(adapterPosition);
        visibleNotifications.remove(adapterPosition);
        // adapter.notifyItemRemoved(adapterPosition);
        mStickyNotificationManager.deleteNotification(removedNotification);
        notificationHelper.showNotifications(notifications);
        mUpdateBus.post(new NoteDeletedEvent());
        adapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar
                .make(recyclerView, getString(R.string.note_has_been_removed, copyNotification.getTitle()), Snackbar.LENGTH_LONG)
                .setAction(R.string.note_has_been_removed_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mStickyNotificationManager.saveNotification(copyNotification);
                        mUpdateBus.post(new NoteAddedEvent());
                        refreshNotesList();
                        onNewTextFilter(mCurrentFilter);
                    }
                })
                .setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(DefconUtils.getDefconColorResource(copyNotification.getDefcon())));
        TextView snackBarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarTextView.setTextColor(Color.WHITE);
        snackbar.show(); // Don’t forget to show!
    }

    public void refreshNotesList() {
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        if (visibleNotifications == null) {
            visibleNotifications = new ArrayList<>();
        }
        notifications.clear();
        visibleNotifications.clear();
        notifications.addAll(mStickyNotificationManager.getNotifications());

        if (BuildConfig.DEBUG && notifications.isEmpty()) {
            String[] notif = getResources().getStringArray(R.array.normal_notification);
            mStickyNotificationManager.saveNotification(new StickyNotification(notif[0], notif[1], StickyNotification.Defcon.NORMAL, true));
            notif = getResources().getStringArray(R.array.useless_notification);
            mStickyNotificationManager.saveNotification(new StickyNotification(notif[0], notif[1], StickyNotification.Defcon.USELESS, true));
            notif = getResources().getStringArray(R.array.important_notification);
            mStickyNotificationManager.saveNotification(new StickyNotification(notif[0], notif[1], StickyNotification.Defcon.IMPORTANT, true));
            notif = getResources().getStringArray(R.array.ultra_notification);
            mStickyNotificationManager.saveNotification(new StickyNotification(notif[0], notif[1], StickyNotification.Defcon.ULTRA, true));

            notifications.addAll(mStickyNotificationManager.getNotifications());
        }
        Collections.sort(notifications);
        visibleNotifications.addAll(notifications);
        adapter.notifyDataSetChanged();
        notificationHelper.showNotifications(notifications);
    }

    private void onNewTextFilter(String filter) {
        mCurrentFilter = filter;
        if (filter != null) {
            filter = filter.trim().toLowerCase(Locale.getDefault());
            if (!filter.isEmpty()) {
                visibleNotifications.clear();
                for (StickyNotification notification : notifications) {
                    if (notification.getTitle().toLowerCase(Locale.getDefault()).contains(filter)
                            || notification.getContent().toLowerCase(Locale.getDefault()).contains(filter)) {
                        visibleNotifications.add(notification);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                visibleNotifications.clear();
                visibleNotifications.addAll(notifications);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public EventBus getBus() {
        return mUpdateBus;
    }

    private class StickyNotificationAdapter
            extends RecyclerView.Adapter<StickyNotificationAdapter.ViewHolder> {

        //***
        // Items
        //***

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.card_sticky, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.bind(visibleNotifications.get(i));
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
                        mUpdateBus.post(new NoteSelectedEvent(notification.getId()));
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

        //***
        // Global
        //***

        @Override
        public int getItemCount() {
            return visibleNotifications.size();
        }

    }

    public class NoteSavedEvent {
    }

    public class NoteDeletedEvent {
    }

    public class NoteAddedEvent {
    }

    public class NoteSelectedEvent {
        public final int noteId;

        public NoteSelectedEvent(int noteId) {
            this.noteId = noteId;
        }
    }
}
