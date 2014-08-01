package fr.quentinklein.stickynotifs.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.OrmLiteDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;

/**
 * Created by quentin on 21/07/2014.
 * Show notifications (called by receiver when boot is done)
 *
 * @see fr.quentinklein.stickynotifs.boot.StartUpReceiver
 */
@EService
public class StartUpService extends Service {

    @Bean
    NotificationHelper notificationHelper;

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<StickyNotification> stickyNotifications = new ArrayList<StickyNotification>(0);
        try {
            stickyNotifications = stickyNotificationDao.queryForAll();
        } catch (SQLException e) {
            Log.e(StartUpService.class.getSimpleName(), "Error while fetching notes", e);
            // Log it to GA
            EasyTracker.getInstance(getApplicationContext()).send(
                    MapBuilder.createException(
                            new StandardExceptionParser(this, null)
                                    // Context and optional collection of package names to be used in reporting the exception.
                                    .getDescription(Thread.currentThread().getName(),
                                            // The name of the thread on which the exception occurred.
                                            e),                                  // The exception.
                            false
                    ).build()
            );
        }
        // Show notifications
        notificationHelper.showNotifications(stickyNotifications);
        return super.onStartCommand(intent, flags, startId);
    }
}
