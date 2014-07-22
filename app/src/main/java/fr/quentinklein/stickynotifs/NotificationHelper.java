package fr.quentinklein.stickynotifs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;
import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity_;

/**
 * Created by quentin on 19/07/2014.
 */
@EBean
public class NotificationHelper {

    @RootContext
    Context context;

    @SystemService
    NotificationManager mNotificationManager;

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

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

    public void showNotification(int id) {
        try {
            StickyNotification stick = stickyNotificationDao.queryForId(id);
            NotificationCompat.Builder mBuilder =
                    getBaseBuilder(stick);
            mNotificationManager.notify(id, mBuilder.build());
        } catch (SQLException e) {
            Log.e(NotificationHelper.class.getSimpleName(), "Error while retribving", e);
            EasyTracker.getInstance(context.getApplicationContext()).send(
                    MapBuilder.createException(
                            new StandardExceptionParser(context, null)
                                    // Context and optional collection of package names to be used in reporting the exception.
                                    .getDescription(Thread.currentThread().getName(),
                                            // The name of the thread on which the exception occurred.
                                            e),                                  // The exception.
                            false
                    ).build()
            );
        }
    }

    public void hideNotification(int id) {
        mNotificationManager.cancel(id);
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
                        .setLargeIcon(getColorSquareResource(notification));

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
