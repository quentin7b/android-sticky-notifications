package fr.quentinklein.stickynotifs.widget;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;

/**
 * Created by quentin on 04/08/2014.
 */
public class StickyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;

    Dao<StickyNotification, Integer> stickyNotificationDao;

    List<StickyNotification> notificationList;

    private int appWidgetId;

    /**
     * Create a widget factory and return it
     *
     * @param context the app context
     * @throws SQLException an exception if dao hasn't been created
     */
    public StickyWidgetRemoteViewsFactory(Context context) throws SQLException {
        this.context = context;
        stickyNotificationDao = new DatabaseHelper(context).getDao(StickyNotification.class);
    }

    private void updateWidgetListView() {
        // Update list
        try {
            notificationList = stickyNotificationDao.queryForAll();
            Collections.sort(notificationList);
        } catch (SQLException e) {
            Log.e(StickyWidgetRemoteViewsFactory.class.getSimpleName(), "Error while retrieving list", e);
            if (notificationList == null) {
                notificationList = new ArrayList<StickyNotification>(0);
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
        remoteView.setImageViewResource(android.R.id.icon, getIconResource(notification));
        /*remoteView.setOnClickFillInIntent(
                android.R.id.text1,
                new Intent().putExtra(StickyWidgetProvider.EXTRA_ID, notification.getId())
        );*/
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
