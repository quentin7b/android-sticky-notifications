package com.github.quentin7b.sn

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.MainActivity

import java.util.ArrayList
import java.util.Collections

/**
 * Help showing notifications on screen
 */
object NotificationHelper {

    private val ID = 14628

    fun showNotifications(context: Context, notifications: List<StickyNotification>) {
        val toShowNotifications = filterAndSortNotifications(notifications)
        if (!toShowNotifications.isEmpty()) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .notify(ID, if (toShowNotifications.size > 1)
                        getListBuilder(context, toShowNotifications).build()
                    else
                        getSingleBuilder(context, toShowNotifications[0]).build())
        } else {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .cancelAll()
        }
    }

    private fun filterAndSortNotifications(notifications: List<StickyNotification>): List<StickyNotification> {
        val toShow = ArrayList<StickyNotification>()
        for (notification in notifications) {
            if (notification.isNotification) {
                toShow.add(notification)
            }
        }
        Collections.sort(toShow)
        return toShow
    }

    private fun getSingleBuilder(context: Context, notification: StickyNotification): NotificationCompat.Builder {
        val builder = generateBuilder(
                context,
                notification.title,
                notification.content,
                NotificationCompat.BigTextStyle().bigText(notification.content))

        builder.setContentIntent(PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java)
                        .setAction(MainActivity.ACTION_NOTIFICATION)
                        .putExtra(MainActivity.EXTRA_NOTIFICATION, notification),
                PendingIntent.FLAG_UPDATE_CURRENT
        ))

        return builder
    }

    private fun getListBuilder(context: Context, notifications: List<StickyNotification>): NotificationCompat.Builder {
        val notificationCompat = NotificationCompat.InboxStyle()
        for (note in notifications) {
            val suffix = if (!note.content.isEmpty()) " - " + note.content else ""
            val wordToSpan = SpannableString(note.title + suffix)
            wordToSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, ColorHelper.getDefconColor(note.defcon))), 0, note.title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            wordToSpan.setSpan(StyleSpan(Typeface.BOLD), 0, note.title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            notificationCompat.addLine(wordToSpan)
        }
        notificationCompat.setSummaryText(context.getString(R.string.app_name))

        val builder = generateBuilder(
                context,
                context.resources.getQuantityString(R.plurals.grouped_notification_title, notifications.size, notifications.size),
                context.getString(R.string.app_name),
                notificationCompat)

        builder.setContentIntent(PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        ))

        return builder
    }

    private fun generateBuilder(context: Context, title: String, text: String, style: NotificationCompat.Style): NotificationCompat.Builder {
        val mBuilder = NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.small_icon)
                .setOngoing(true)
                .setStyle(style)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.priority = Notification.PRIORITY_MAX
        }

        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        mBuilder.setOngoing(true)
        return mBuilder
    }

}
