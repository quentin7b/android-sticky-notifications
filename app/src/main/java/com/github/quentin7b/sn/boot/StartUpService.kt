package com.github.quentin7b.sn.boot

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

import com.github.quentin7b.sn.NotificationHelper
import com.github.quentin7b.sn.database.DatabaseHelper


/**
 * Show notifications (called by receiver when boot is done)
 */
class StartUpService : Service() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, StartUpService::class.java)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Show notifications and stop itself
        NotificationHelper.showNotifications(
                applicationContext,
                DatabaseHelper(applicationContext).database.all
        )

        stopSelf()
        // Return
        return super.onStartCommand(intent, flags, startId)
    }
}
