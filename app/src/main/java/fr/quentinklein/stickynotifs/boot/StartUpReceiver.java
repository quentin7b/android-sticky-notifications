package fr.quentinklein.stickynotifs.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by quentin on 21/07/2014.
 */
public class StartUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(StartUpReceiver.class.getSimpleName(), "Boot complete");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, StartUpService_.class));
        }
    }
}
