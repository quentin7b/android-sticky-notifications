package fr.quentinklein.stickynotifs.model;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by quentin on 23/10/14.
 */
@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface NotificationPreferences {

    @DefaultBoolean(false)
    boolean concatNotifications();

    @DefaultBoolean(false)
    boolean analytics();
}