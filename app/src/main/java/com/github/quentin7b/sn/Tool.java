package com.github.quentin7b.sn;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.github.quentin7b.sn.database.model.StickyNotification;

import java.util.Locale;

/**
 * Created by quentin on 09/03/2017.
 */

public class Tool {

    public static Locale getLocale(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return configuration.getLocales().get(0);
        } else {
            //noinspection deprecation
            return configuration.locale;
        }
    }

    public static boolean notificationIsValid(StickyNotification notification) {
        return !notification.getTitle().trim().isEmpty();
    }
}
