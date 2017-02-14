package com.github.quentin7b.sn.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.github.quentin7b.sn.R;

public class StickyWidgetProvider extends AppWidgetProvider {

    public final static String EXTRA_ID = "id";
    public final static String UPDATE_LIST = "UPDATE_LIST";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (UPDATE_LIST.equalsIgnoreCase(intent.getAction())) {
            updateWidget(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, StickyWidgetService.class);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_sticky);
            rv.setRemoteAdapter(android.R.id.list, intent);
            // Handle click
            // Intent activityIntent = new Intent(context, NotesListActivity_.class).putExtra(NotesListActivity.EXTRA_NOTE_ID, appWidgetId);
            // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            // rv.setPendingIntentTemplate(android.R.id.list, pendingIntent);
            //
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, StickyWidgetProvider.class)
                ),
                android.R.id.list);
    }
}
