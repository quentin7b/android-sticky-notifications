package fr.quentinklein.stickynotifs.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity_;

/**
 * Created by quentin on 13/08/2014.
 */
public class StickyWidgetProvider extends AppWidgetProvider {

    public final static String EXTRA_ID = "id";
    public final static String UPDATE_LIST = "UPDATE_LIST";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(UPDATE_LIST)) {
            updateWidget(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(StickyWidgetProvider.class.getSimpleName(), "Update widgets " + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, StickyWidgetService.class);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            //intent.setData(Uri.parse("custom://" + intent.toUri(Intent.URI_INTENT_SCHEME) + "/" + appWidgetId));

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_sticky);
            rv.setRemoteAdapter(android.R.id.list, intent);
            // Handle click
            Intent activityIntent = new Intent(context, NotesListActivity_.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(android.R.id.list, pendingIntent);
            //
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StickyWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, android.R.id.list);
    }
}
