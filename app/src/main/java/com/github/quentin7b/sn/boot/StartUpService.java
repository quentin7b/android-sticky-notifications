package com.github.quentin7b.sn.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.quentin7b.sn.NotificationHelper;
import com.github.quentin7b.sn.database.DatabaseHelper;

import java.sql.SQLException;


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
        NotificationHelper helper = new NotificationHelper(getApplicationContext());
        DatabaseHelper.StickyDao dao;
        try {
            dao = new DatabaseHelper(getApplicationContext()).getDatabase();
            // Show notifications and stop itself
            helper.showNotifications(dao.getAll());
        } catch (SQLException e) {
            Log.e("", "", e);
        }

        stopSelf();
        // Return
        return super.onStartCommand(intent, flags, startId);
    }
}
