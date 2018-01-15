package com.github.quentin7b.sn.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.ui.MainActivity

import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE

class StickyWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (UPDATE_LIST == intent.action || ACTION_APPWIDGET_UPDATE == intent.action) {
            updateWidget(context)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val rv = RemoteViews(context.packageName, R.layout.widget_sticky)
            rv.setRemoteAdapter(android.R.id.list, Intent(context, StickyWidgetService::class.java))

            val pendingIntent = PendingIntent.getActivity(context, 0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT)

            rv.setPendingIntentTemplate(android.R.id.list, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(
                        ComponentName(context, StickyWidgetProvider::class.java)
                ),
                android.R.id.list)
    }

    companion object {
        val UPDATE_LIST = "UPDATE_LIST"
    }
}
