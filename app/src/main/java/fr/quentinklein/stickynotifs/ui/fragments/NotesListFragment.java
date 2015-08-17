package fr.quentinklein.stickynotifs.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import fr.quentinklein.stickynotifs.DefconUtils;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;

/**
 * Created by quentin on 20/07/2014.
 * All notes list
 */
@EFragment(R.layout.fragment_list_notes)
public class NotesListFragment extends Fragment {

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
                            Paint paint = new Paint();
                            paint.setColor(getResources().getColor(DefconUtils.getDefconColorResource(visibleNotifications.get(viewHolder.getAdapterPosition()).getDefcon())));
                            float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);
                            float bitmapWidth = bitmap.getWidth();
                            c.drawRect(
                                    (float) itemView.getRight() + dX,
                                    (float) itemView.getTop(),
                                    (float) itemView.getRight(),
                                    (float) itemView.getBottom(), paint);
                            c.drawBitmap(bitmap, ((float) itemView.getRight() - bitmapWidth) - 72f, (float) itemView.getTop() + height, paint);
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
        adapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar
                .make(recyclerView, "Notification has been removed", Snackbar.LENGTH_LONG)
                .setAction("cancel ?", new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mStickyNotificationManager.saveNotification(copyNotification);
                        visibleNotifications.add(copyNotification);
                        notifications.add(copyNotification);
                        Collections.sort(visibleNotifications);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(DefconUtils.getDefconColorResource(copyNotification.getDefcon())));
        TextView snackBarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackBarTextView.setTextColor(Color.WHITE);
        snackbar.show(); // Don’t forget to show!
    }

    /**
     * Reload the list of notifications to display
     *
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#noteSaved(int)
     * @see fr.quentinklein.stickynotifs.ui.activities.NotesListActivity#onResume()
     */
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
        Collections.sort(notifications);
        visibleNotifications.addAll(notifications);
        adapter.notifyDataSetChanged();
        if (notifications.isEmpty()) {
            // hide detail
            if (getActivity() instanceof HideNoteListener) {
                ((HideNoteListener) getActivity()).hideNote();
            }
        }
    }

    public void onNewTextFilter(String filter) {
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

        //***
        // Global
        //***

        @Override
        public int getItemCount() {
            return visibleNotifications.size();
        }

    }
}
