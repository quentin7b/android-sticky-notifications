package com.github.quentin7b.sn.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.quentin7b.sn.ColorHelper;
import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.MainActivity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StickyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private DatabaseHelper.StickyDao database;
    private List<StickyNotification> notificationList;

    /**
     * Create a widget factory and return it
     *
     * @param context the app context
     */
    public StickyWidgetRemoteViewsFactory(Context context) {
        this.context = context;
        database = new DatabaseHelper(context).getDatabase();
    }

    private void updateWidgetListView() {
        notificationList = database.getAll();
        Collections.sort(notificationList);
    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        notificationList.clear();
    }

    @Override
    public int getCount() {
        return (notificationList != null)
                ? notificationList.size()
                : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_row);
        StickyNotification notification = notificationList.get(position);
        remoteView.setTextViewText(R.id.note_title_tv, notification.getTitle());
        remoteView.setTextColor(R.id.note_title_tv, ContextCompat.getColor(context, ColorHelper.getDefconColor(notification.getDefcon())));
        remoteView.setTextViewText(R.id.note_description_tv, notification.getContent());

        if (!notification.isNotification()) {
            remoteView.setImageViewBitmap(R.id.note_notif_iv, null);
        }

        Intent fillInIntent = new Intent()
                .setAction(MainActivity.ACTION_NOTIFICATION)
                .putExtra(MainActivity.EXTRA_NOTIFICATION, notification);
        remoteView.setOnClickFillInIntent(R.id.widget_row, fillInIntent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        // Auto
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return notificationList.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
