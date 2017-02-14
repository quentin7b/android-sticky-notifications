package com.github.quentin7b.sn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.github.quentin7b.sn.database.model.StickyNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * Help showing notifications on screen
 */
public class NotificationHelper {

    private Context context;
    private NotificationManager androidNotificationManager;
    private Bitmap uselessBitmap, normalBitmap, importantBitmap, ultraBitmap;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.androidNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Show notifications in action bar
     *
     * @param notifications the full list of notifications
     */
    public void showNotifications(final List<StickyNotification> notifications) {
        hideAll();
        // Concat the notifications
        List<StickyNotification> ultra = getDefconNotifications(notifications, StickyNotification.Defcon.ULTRA);
        List<StickyNotification> important = getDefconNotifications(notifications, StickyNotification.Defcon.IMPORTANT);
        List<StickyNotification> normal = getDefconNotifications(notifications, StickyNotification.Defcon.NORMAL);
        List<StickyNotification> useless = getDefconNotifications(notifications, StickyNotification.Defcon.USELESS);

        for (StickyNotification s : ultra) {
            showNotification(s);
        }

        for (StickyNotification s : important) {
            showNotification(s);
        }
        for (StickyNotification s : normal) {
            showNotification(s);
        }
        for (StickyNotification s : useless) {
            showNotification(s);
        }
    }

    private void showNotification(StickyNotification stick) {
        NotificationCompat.Builder mBuilder =
                getBaseBuilder(stick);
        androidNotificationManager.notify(stick.getId(), mBuilder.build());
    }

    private void hideAll() {
        androidNotificationManager.cancelAll();
    }

    private NotificationCompat.Builder getBaseBuilder(StickyNotification notification) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getContent())
                        .setSmallIcon(getSmallIconResource(notification))
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getContent()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            switch (notification.getDefcon()) {
                case ULTRA:
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    break;
                case IMPORTANT:
                    mBuilder.setPriority(Notification.PRIORITY_HIGH);
                    break;
                case NORMAL:
                    mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
                    break;
                case USELESS:
                    mBuilder.setPriority(Notification.PRIORITY_LOW);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (notification.getDefcon()) {

            }
        } else {
            mBuilder.setLargeIcon(getColorSquareResource(notification));
        }
        // Creates an explicit intent for an Activity in your app
        //Intent resultIntent = new Intent(context, NotesListActivity_.class);
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(NotesListActivity_.class);
        //stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        return mBuilder;
    }

    private int getSmallIconResource(StickyNotification notification) {
        return -1;
    }

    /**
     * Return a special square for action bar icon (big icon)
     * Prevent from reloading bitmap each time
     *
     * @param notification the notification to use (square is defcon relative)
     * @return the Bitmap to show on screen
     */
    private Bitmap getColorSquareResource(StickyNotification notification) {
        int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
        int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
        return null;
    }


    /**
     * Return notifications with a particular defcon.
     *
     * @param notifications the full list of notifications
     * @param defcon        the defcon needed
     * @return the filtered list
     */
    private static List<StickyNotification> getDefconNotifications(
            List<StickyNotification> notifications, StickyNotification.Defcon defcon) {
        ArrayList<StickyNotification> defconNotifications = new ArrayList<>();
        for (StickyNotification notification : notifications) {
            if (defcon.equals(notification.getDefcon())) {
                defconNotifications.add(notification);
            }
        }
        return defconNotifications;
    }

}
