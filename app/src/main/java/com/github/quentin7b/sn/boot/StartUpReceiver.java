package com.github.quentin7b.sn.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Start a service that show up notifications when the phone is ready to go
 */
public class StartUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start the service to show notifications
            context.startService(new Intent(context, StartUpService.class));
        }
    }
}
