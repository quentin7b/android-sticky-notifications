package fr.quentinklein.stickynotifs.manager;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;

/**
 * Created by qklein<qklein@eliocity.com> on 30/07/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class StickyNotificationManager {

    @RootContext
    Context mContext;

    @Pref
    NotificationPreferences_ mNotificationPreferences;

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> mStickyNotifications;

    public synchronized List<StickyNotification> getNotifications() {
        try {
            return mStickyNotifications.queryForAll();
        } catch (Exception e) {
            logError(e);
            return new ArrayList<>(0);
        }
    }

    public synchronized List<StickyNotification> getNotifications(StickyNotification.Defcon defcon) {
        try {
            List<StickyNotification> notifications = mStickyNotifications.queryForAll();
            List<StickyNotification> defconNotifications = new ArrayList<>();
            for (StickyNotification notification : notifications) {
                if (notification.getDefcon().equals(defcon)) {
                    defconNotifications.add(notification);
                }
            }
            return defconNotifications;
        } catch (Exception e) {
            logError(e);
            return new ArrayList<>(0);
        }
    }

    public StickyNotification getNotification(final int noteId) {
        try {
            return mStickyNotifications.queryForId(noteId);
        } catch (Exception e) {
            logError(e);
            return null;
        }
    }

    public synchronized StickyNotification saveNotification(StickyNotification stickyNotification) {
        try {
            if (stickyNotification.getId() > 0) {
                mStickyNotifications.update(stickyNotification);
            } else {
                int newId = mStickyNotifications.create(stickyNotification);
                stickyNotification.setId(newId);
            }
            return stickyNotification;
        } catch (Exception e) {
            logError(e);
            return stickyNotification;
        }
    }

    public synchronized void deleteNotification(StickyNotification notification) {
        try {
            mStickyNotifications.delete(notification);
        } catch (Exception e) {
            // Sad
            logError(e);
        }
    }

    private void logError(Exception e) {
        if (!BuildConfig.DEBUG && mNotificationPreferences.analytics().get()) {
            EasyTracker.getInstance(mContext).send(
                    MapBuilder.createException(
                            new StandardExceptionParser(mContext, null)
                                    // Context and optional collection of package names to be used in reporting the exception.
                                    .getDescription(Thread.currentThread().getName(),
                                            // The name of the thread on which the exception occurred.
                                            e),                                  // The exception.
                            false
                    ).build()
            );
        }
    }

}
