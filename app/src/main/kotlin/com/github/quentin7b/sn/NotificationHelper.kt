package com.github.quentin7b.sn

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.MainActivity

object NotificationHelper {

    private const val ID = 14628
    private const val CHANNEL_ID = "com.github.quentin7b.stickychannel"

    fun showNotifications(context: Context, notifications: List<StickyNotification>) {
        val toShowNotifications = notifications.filter { it.isNotification }.sorted()
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
                        .putExtra(MainActivity.EXTRA_NOTIFICATION_ID, notification.id),
                PendingIntent.FLAG_UPDATE_CURRENT
        ))

        return builder
    }

    private fun getListBuilder(context: Context, notifications: List<StickyNotification>): NotificationCompat.Builder {
        val notificationCompat = NotificationCompat.InboxStyle()
        for (note in notifications) {
            val suffix = if (!note.content.isEmpty()) " - " + note.content else ""
            val wordToSpan = SpannableString(note.title + suffix)
            wordToSpan.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, ColorHelper.getDefconColor(note.defcon))),
                    0,
                    note.title.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.small_icon)
                .setStyle(style)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setPriorityNewAPI(mBuilder)
        } else {
            setPriorityOldAPI(mBuilder)
        }

        return mBuilder
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPriorityNewAPI(builder: NotificationCompat.Builder) {
        builder.priority = NotificationManager.IMPORTANCE_MAX
    }

    @Suppress("DEPRECATION")
    private fun setPriorityOldAPI(builder: NotificationCompat.Builder) {
        builder.priority = Notification.PRIORITY_MAX
    }

}
