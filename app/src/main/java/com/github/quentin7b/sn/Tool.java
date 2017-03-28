package com.github.quentin7b.sn;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.github.quentin7b.sn.database.model.StickyNotification;

import java.util.Locale;

import static com.github.quentin7b.sn.R.id.cla;

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

    public static void showSnackbar(View anchor, @StringRes int message, int duration, @StringRes int actionMessage, @Nullable View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar
                .make(anchor, message, duration);
        if (actionMessage != -1) {
            snackbar.setAction(actionMessage, actionListener);
        }
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
