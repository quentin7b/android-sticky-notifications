package com.github.quentin7b.sn.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Start a service that show up notifications when the phone is ready to go
 */
class StartUpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Start the service to show notifications
            context.startService(StartUpService.newIntent(context))
        }
    }
}
