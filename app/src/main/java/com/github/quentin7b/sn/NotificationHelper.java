package com.github.quentin7b.sn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Help showing notifications on screen
 */
public class NotificationHelper {

    private static final Integer ID = 14628;

    public static void showNotifications(Context context, final List<StickyNotification> notifications) {
        List<StickyNotification> toShowNotifications = filterAndSortNotifications(notifications);
        if (!toShowNotifications.isEmpty()) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(ID, (toShowNotifications.size() > 1)
                            ? getListBuilder(context, toShowNotifications).build()
                            : getSingleBuilder(context, toShowNotifications.get(0)).build());
        }
    }

    private static List<StickyNotification> filterAndSortNotifications(List<StickyNotification> notifications) {
        List<StickyNotification> toShow = new ArrayList<>();
        for (StickyNotification notification : notifications) {
            if (notification.isNotification()) {
                toShow.add(notification);
            }
        }
        Collections.sort(toShow);
        return toShow;
    }

    private static NotificationCompat.Builder getSingleBuilder(Context context, StickyNotification notification) {
        NotificationCompat.Builder builder = generateBuilder(
                context,
                notification.getTitle(),
                notification.getContent(),
                new NotificationCompat.BigTextStyle().bigText(notification.getContent()));

        builder.setContentIntent(PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class)
                        .setAction(MainActivity.ACTION_NOTIFICATION)
                        .putExtra(MainActivity.EXTRA_NOTIFICATION, notification),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));

        return builder;
    }

    private static NotificationCompat.Builder getListBuilder(Context context, List<StickyNotification> notifications) {
        NotificationCompat.InboxStyle notificationCompat = new NotificationCompat.InboxStyle();
        for (StickyNotification note : notifications) {
            String suffix = (!note.getContent().isEmpty() ? " - " + note.getContent() : "");
            Spannable wordToSpan = new SpannableString(note.getTitle() + suffix);
            wordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, ColorHelper.getDefconColor(note.getDefcon()))), 0, note.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, note.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            notificationCompat.addLine(wordToSpan);
        }
        notificationCompat.setSummaryText(context.getString(R.string.app_name));

        NotificationCompat.Builder builder = generateBuilder(
                context,
                context.getResources().getQuantityString(R.plurals.grouped_notification_title, notifications.size(), notifications.size()),
                context.getString(R.string.app_name),
                notificationCompat);

        builder.setContentIntent(PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));

        return builder;
    }

    private static NotificationCompat.Builder generateBuilder(Context context, String title, String text, NotificationCompat.Style style) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.small_icon)
                        .setOngoing(true)
                        .setStyle(style);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        mBuilder.setOngoing(true);
        return mBuilder;
    }

}
