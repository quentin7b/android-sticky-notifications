package fr.quentinklein.stickynotifs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity_;

/**
 * Created by quentin on 19/07/2014.
 * Help showing notifications on screen
 */
@EBean
public class NotificationHelper {

    @RootContext
    Context context;
    @SystemService
    NotificationManager mNotificationManager;
    Bitmap uselessBitmap, normalBitmap, importantBitmap, ultraBitmap;
    @Pref
    NotificationPreferences_ preferences;

    /**
     * Show notifications in action bar
     *
     * @param notifications the full list of notifications
     * @see fr.quentinklein.stickynotifs.boot.StartUpService#onStartCommand(android.content.Intent, int, int)
     */
    public void showNotifications(final List<StickyNotification> notifications) {
        hideAll();
        if (preferences.concatNotifications().get()) {
            // Concat the notifications
            List<StickyNotification> ultra = getDefconNotifications(notifications, StickyNotification.Defcon.ULTRA);
            List<StickyNotification> important = getDefconNotifications(notifications, StickyNotification.Defcon.IMPORTANT);
            List<StickyNotification> normal = getDefconNotifications(notifications, StickyNotification.Defcon.NORMAL);
            List<StickyNotification> useless = getDefconNotifications(notifications, StickyNotification.Defcon.USELESS);
            if (ultra != null && ultra.size() > 0) {
                showGroupedNotifications(ultra, StickyNotification.Defcon.ULTRA);
            }
            if (important != null && important.size() > 0) {
                showGroupedNotifications(important, StickyNotification.Defcon.IMPORTANT);
            }
            if (normal != null && normal.size() > 0) {
                showGroupedNotifications(normal, StickyNotification.Defcon.NORMAL);
            }
            if (useless != null && useless.size() > 0) {
                showGroupedNotifications(useless, StickyNotification.Defcon.USELESS);
            }
        } else {
            // Solo notifications
            for (StickyNotification notification : notifications) {
                if (notification.isNotification()) {
                    showNotification(notification);
                } else {
                    hideNotification(notification);
                }
            }
        }
    }

    private void showNotification(StickyNotification stick) {
        NotificationCompat.Builder mBuilder =
                getBaseBuilder(stick);
        mNotificationManager.notify(stick.getId(), mBuilder.build());
    }

    /**
     * Generate a notification with a bunch of other notifications.
     *
     * @param notifications the bunch of notifications to show
     * @param defcon        the defcon needed
     */
    private void showGroupedNotifications(List<StickyNotification> notifications, StickyNotification.Defcon defcon) {
        if (notifications.size() > 1) {
            // If there are more than one notification, Generate the Global notification.
            NotificationCompat.Builder mBuilder =
                    getListBuilder(notifications);
            mNotificationManager.notify(-defcon.ordinal(), mBuilder.build());
        } else {
            // There is only one notification, show it
            showNotification(notifications.get(0));
        }
    }

    private void hideNotification(StickyNotification stickyNotification) {
        hideNotification(stickyNotification.getId());
    }

    public void hideNotification(int id) {
        mNotificationManager.cancel(id);
    }

    public void hideAll() {
        mNotificationManager.cancelAll();
    }

    public void hideGroupedNotifications() {
        mNotificationManager.cancel(-StickyNotification.Defcon.ULTRA.ordinal());
        mNotificationManager.cancel(-StickyNotification.Defcon.IMPORTANT.ordinal());
        mNotificationManager.cancel(-StickyNotification.Defcon.NORMAL.ordinal());
        mNotificationManager.cancel(-StickyNotification.Defcon.USELESS.ordinal());
    }

    private NotificationCompat.Builder getListBuilder(List<StickyNotification> notifications) {
        StickyNotification notification = new StickyNotification();
        notification.setId(notifications.get(0).getDefcon().ordinal());
        notification.setTitle(context.getResources().getQuantityString(R.plurals.grouped_notification_title, notifications.size(), notifications.size()));
        notification.setNotification(true);
        notification.setDefcon(notifications.get(0).getDefcon());

        NotificationCompat.InboxStyle notificationCompat = new NotificationCompat.InboxStyle();
        for (StickyNotification notificationItem : notifications) {
            String suffix = (!notificationItem.getContent().isEmpty() ? " - " + notificationItem.getContent() : "");
            Spannable wordToSpan = new SpannableString(notificationItem.getTitle() + suffix);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), 0, notificationItem.getTitle().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            notificationCompat.addLine(wordToSpan);
        }
        notificationCompat.setSummaryText(context.getString(R.string.app_name));


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(notification.getTitle())
                        .setContentText(context.getString(R.string.app_name))
                        .setSmallIcon(getSmallIconResource(notification))
                        .setOngoing(true)
                        .setStyle(notificationCompat);

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
                case ULTRA:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_red));
                    break;
                case IMPORTANT:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_orange));
                    break;
                case NORMAL:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_green));
                    break;
                case USELESS:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_blue));
            }
        } else {
            mBuilder.setLargeIcon(getColorSquareResource(notification));
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
                case ULTRA:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_red));
                    break;
                case IMPORTANT:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_orange));
                    break;
                case NORMAL:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_green));
                    break;
                case USELESS:
                    mBuilder.setColor(context.getResources().getColor(R.color.color_blue));
            }
        } else {
            mBuilder.setLargeIcon(getColorSquareResource(notification));
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

    public int getSmallIconResource(StickyNotification notification) {
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


    /**
     * Return notifications with a particular defcon.
     *
     * @param notifications the full list of notifications
     * @param defcon        the defcon needed
     * @return the filtered list
     * @see fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment#refreshNotesList()
     */
    public static List<StickyNotification> getDefconNotifications(
            List<StickyNotification> notifications, StickyNotification.Defcon defcon) {
        ArrayList<StickyNotification> defconNotifications = new ArrayList<StickyNotification>();
        for (StickyNotification notification : notifications) {
            if (defcon.equals(notification.getDefcon())) {
                defconNotifications.add(notification);
            }
        }
        return defconNotifications;
    }

}
