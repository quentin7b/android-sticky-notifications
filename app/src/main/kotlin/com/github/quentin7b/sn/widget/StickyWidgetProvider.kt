package com.github.quentin7b.sn.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.ui.MainActivity


class StickyWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_UPDATE_MESSAGE_LIST = "UPDATE_MESSAGE_LIST"

        fun triggerNoteListWidgetUpdate(context: Context) {
            val appContext = context.applicationContext
            val widgetManager = AppWidgetManager.getInstance(appContext)
            val widget = ComponentName(appContext, StickyWidgetProvider::class.java)
            val widgetIds = widgetManager.getAppWidgetIds(widget)

            val intent = Intent(context, StickyWidgetProvider::class.java)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
            intent.action = ACTION_UPDATE_MESSAGE_LIST
            context.sendBroadcast(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (action == ACTION_UPDATE_MESSAGE_LIST) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val rv = RemoteViews(context.packageName, R.layout.widget_sticky)
            rv.setTextViewText(R.id.widget_head_title, context.getString(R.string.app_name))

            val intent = Intent(context, StickyWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            rv.setRemoteAdapter(R.id.widget_list, intent)

            rv.setPendingIntentTemplate(R.id.widget_list, viewActionTemplatePendingIntent(context))
            rv.setOnClickPendingIntent(R.id.widget_new_note, composeActionPendingIntent(context))

            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }

    private fun viewActionTemplatePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
                .setAction(MainActivity.ACTION_NOTIFICATION)
        // set action view

        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun composeActionPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        // set action new

        return PendingIntent.getActivity(context, 0, intent, 0)
    }

}
