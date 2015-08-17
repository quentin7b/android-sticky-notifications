package fr.quentinklein.stickynotifs.model;

import android.support.annotation.ColorRes;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import fr.quentinklein.stickynotifs.R;

/**
 * Created by quentin on 23/10/14.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface NotificationPreferences {

    @DefaultBoolean(false)
    boolean concatNotifications();

    @DefaultBoolean(false)
    boolean analytics();

    @DefaultBoolean(false)
    boolean askedForHelp();

    @DefaultInt(R.color.colorPrimary)
    @ColorRes
    int colorPrimary();

    @DefaultInt(R.color.colorPrimaryDark)
    @ColorRes
    int colorPrimatyDark();
}