package fr.quentinklein.stickynotifs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity_;

/**
 * Created by quentin on 19/07/2014.
 */
@EBean
public class NotificationHelper {

    static List<Integer> showedNotifications = new ArrayList<Integer>();
    @RootContext
    Context context;
    @SystemService
    NotificationManager mNotificationManager;
    Bitmap uselessBitmap, normalBitmap, importantBitmap, ultraBitmap;

    public static List<StickyNotification> getDefconsNotifications(
            List<StickyNotification> notifications, StickyNotification.Defcon defcon) {
        ArrayList<StickyNotification> defconNotifications = new ArrayList<StickyNotification>();
        for (StickyNotification notification : notifications) {
            if (defcon.equals(notification.getDefcon())) {
                defconNotifications.add(notification);
            }
        }
        return defconNotifications;
    }

    public void showNotifications(List<StickyNotification> notifications) {
        for (StickyNotification notification : notifications) {
            if (notification.isNotification()) {
                showNotification(notification);
            } else {
                hideNotification(notification);
            }
        }
    }

    private void showNotification(StickyNotification stick) {
        NotificationCompat.Builder mBuilder =
                getBaseBuilder(stick);
        mNotificationManager.notify(stick.getId(), mBuilder.build());
        showedNotifications.add(stick.getId());
    }

    private void hideNotification(StickyNotification stickyNotification) {
        hideNotification(stickyNotification.getId());
    }

    public void hideNotification(int id) {
        mNotificationManager.cancel(id);
        showedNotifications.remove(id);
    }

    public void hideAll() {
        mNotificationManager.cancelAll();
    }

    private NotificationCompat.Builder getBaseBuilder(StickyNotification notification) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getContent())
                        .setSmallIcon(getSmallIconResource(notification))
                        .setOngoing(true)
                        .setLargeIcon(getColorSquareResource(notification))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getContent()));
        /*
                        .addAction(R.drawable.ic_action_edit_white, context.getString(R.string.action_edit), null)
                        .addAction(R.drawable.ic_menu_delete_white, context.getString(R.string.create_delete), null);
        */
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
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, NotesListActivity_.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotesListActivity_.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        return mBuilder;
    }

    private int getSmallIconResource(StickyNotification notification) {
        switch (notification.getDefcon()) {
            case USELESS:
                return R.drawable.blue_square_paper_small;
            case NORMAL:
                return R.drawable.green_square_paper_small;
            case IMPORTANT:
                return R.drawable.orange_square_paper_small;
            case ULTRA:
                return R.drawable.red_square_paper_small;
            default:
                return R.drawable.ic_launcher;
        }
    }

    private Bitmap getColorSquareResource(StickyNotification notification) {
        int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
        int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
        switch (notification.getDefcon()) {
            case USELESS:
                if (uselessBitmap == null) {
                    uselessBitmap = Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_square_paper),
                            width, height, false);
                }
                return uselessBitmap;
            case NORMAL:
                if (normalBitmap == null) {
                    normalBitmap = Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.green_square_paper),
                            width, height, false);
                }
                return normalBitmap;
            case IMPORTANT:
                if (importantBitmap == null) {
                    importantBitmap = Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.orange_square_paper),
                            width, height, false);
                }
                return importantBitmap;
            case ULTRA:
                if (ultraBitmap == null) {
                    ultraBitmap = Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.red_square_paper),
                            width, height, false);
                }
                return ultraBitmap;
            default:
                return null;
        }
    }

}
