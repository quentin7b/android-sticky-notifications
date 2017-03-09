package com.github.quentin7b.sn.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.github.quentin7b.sn.NotificationHelper;
import com.github.quentin7b.sn.database.DatabaseHelper;


/**
 * Show notifications (called by receiver when boot is done)
 *
 * @see com.github.quentin7b.sn.boot.StartUpReceiver
 */
public class StartUpService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Show notifications and stop itself
        NotificationHelper.showNotifications(
                getApplicationContext(),
                new DatabaseHelper(getApplicationContext()).getDatabase().getAll()
        );

        stopSelf();
        // Return
        return super.onStartCommand(intent, flags, startId);
    }
}
