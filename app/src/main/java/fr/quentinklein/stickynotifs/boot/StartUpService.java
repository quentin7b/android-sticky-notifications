package fr.quentinklein.stickynotifs.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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
            Log.e(StartUpService.class.getSimpleName(), "Errow while requesting notes", e);
        }
        List<StickyNotification> urgentNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.ULTRA);
        List<StickyNotification> importantNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.IMPORTANT);
        List<StickyNotification> normalNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.NORMAL);
        List<StickyNotification> uselessNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.USELESS);


        showNotifications(uselessNotifications);
        showNotifications(normalNotifications);
        showNotifications(importantNotifications);
        showNotifications(urgentNotifications);

        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotifications(List<StickyNotification> notifications) {
        for (final StickyNotification stickyNotification : notifications) {
            if (stickyNotification.isNotification()) {
                notificationHelper.showNotification(stickyNotification.getId());
            } else {
                notificationHelper.hideNotification(stickyNotification.getId());
            }
        }
    }
}
