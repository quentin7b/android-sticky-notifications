package com.github.quentin7b.sn.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StickyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Dao<StickyNotification, Integer> database;
    private List<StickyNotification> notificationList;

    /**
     * Create a widget factory and return it
     *
     * @param context the app context
     * @throws SQLException an exception if dao hasn't been created
     */
    public StickyWidgetRemoteViewsFactory(Context context) throws SQLException {
        this.context = context;
        database = new DatabaseHelper(context).getDao(StickyNotification.class);
    }

    private void updateWidgetListView() {
        // Update list
        try {
            notificationList = database.queryForAll();
            Collections.sort(notificationList);
        } catch (SQLException e) {
            Log.e(StickyWidgetRemoteViewsFactory.class.getSimpleName(), "Error while retrieving list", e);
            if (notificationList == null) {
                notificationList = new ArrayList<>(0);
            }
        }
    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        Log.i(StickyWidgetRemoteViewsFactory.class.getSimpleName(), "Dataset changed");
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        notificationList.clear();
    }

    @Override
    public int getCount() {
        if (notificationList != null) {
            return notificationList.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.widget_row);
        StickyNotification notification = notificationList.get(position);
        remoteView.setTextViewText(android.R.id.text1, notification.getTitle());
        remoteView.setTextViewText(android.R.id.text2, notification.getContent());
        /*remoteView.setImageViewResource(R.id.color_icon, getIconResource(notification));
        remoteView.setImageViewResource(R.id.color_view, getColorResource(notification));*/
        remoteView.setOnClickFillInIntent(
                R.id.widget_row,
                new Intent().putExtra(StickyWidgetProvider.EXTRA_ID, notification.getId())
        );
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
