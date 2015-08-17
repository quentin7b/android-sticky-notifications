package fr.quentinklein.stickynotifs.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;

import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;

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

    @Bean
    StickyNotificationManager mStickyNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Show notifications
        notificationHelper.showNotifications(mStickyNotificationManager.getNotifications());
        // Stop the service
        stopSelf();
        // Return
        return super.onStartCommand(intent, flags, startId);
    }
}
