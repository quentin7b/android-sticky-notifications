package com.github.quentin7b.sn.widget

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import com.github.quentin7b.sn.ColorHelper
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.database.DatabaseHelper
import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.MainActivity
import com.j256.ormlite.dao.Dao

import java.sql.SQLException
import java.util.ArrayList
import java.util.Collections

class StickyWidgetRemoteViewsFactory
/**
 * Create a widget factory and return it
 *
 * @param context the app context
 */
(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val database: DatabaseHelper.StickyDao
    private var notificationList: MutableList<StickyNotification>? = null

    init {
        database = DatabaseHelper(context).database
    }

    private fun updateWidgetListView() {
        notificationList = database.all
        Collections.sort(notificationList!!)
    }

    override fun onCreate() {
        updateWidgetListView()
    }

    override fun onDataSetChanged() {
        updateWidgetListView()
    }

    override fun onDestroy() {
        notificationList!!.clear()
    }

    override fun getCount(): Int {
        return if (notificationList != null)
            notificationList!!.size
        else
            0
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.widget_row)
        val notification = notificationList!![position]
        remoteView.setTextViewText(R.id.note_title_tv, notification.title)
        remoteView.setTextColor(R.id.note_title_tv, ContextCompat.getColor(context, ColorHelper.getDefconColor(notification.defcon)))
        remoteView.setTextViewText(R.id.note_description_tv, notification.content)

        if (!notification.isNotification) {
            remoteView.setImageViewBitmap(R.id.note_notif_iv, null)
        }

        val fillInIntent = Intent()
                .setAction(MainActivity.ACTION_NOTIFICATION)
                .putExtra(MainActivity.EXTRA_NOTIFICATION, notification)
        remoteView.setOnClickFillInIntent(R.id.widget_row, fillInIntent)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews? {
        // Auto
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return notificationList!![position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
