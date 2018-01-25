package com.github.quentin7b.sn.widget

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
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

class StickyWidgetRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val database: DatabaseHelper.StickyDao = DatabaseHelper(context).database
    private var notificationList: ArrayList<StickyNotification> = ArrayList()

    override fun onCreate() {
        // nothing to do
    }

    override fun onDataSetChanged() {
        notificationList.clear()
        notificationList.addAll(database.all.sorted())
    }

    override fun onDestroy() {
        notificationList.clear()
    }

    override fun getCount(): Int {
        return notificationList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.widget_row)
        val notification = notificationList[position]
        remoteView.setTextViewText(R.id.note_title_tv, notification.title)
        remoteView.setTextColor(R.id.note_title_tv, ContextCompat.getColor(context, ColorHelper.getDefconColor(notification.defcon)))
        remoteView.setTextViewText(R.id.note_description_tv, notification.content)

        remoteView.setViewVisibility(R.id.note_notif_iv,
                if (notification.isNotification)
                    View.VISIBLE
                else
                    View.INVISIBLE
        )

        Log.i("Widget", "Put extra $notification for $position")
        val fillInIntent = Intent()
                .putExtra(MainActivity.EXTRA_NOTIFICATION_ID, notification.id)
        remoteView.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return notificationList[position].id.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
