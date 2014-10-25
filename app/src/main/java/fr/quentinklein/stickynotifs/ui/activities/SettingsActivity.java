package fr.quentinklein.stickynotifs.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;

import com.google.analytics.tracking.android.EasyTracker;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.sql.SQLException;

import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;

/**
 * Created by quentin on 23/10/14.
 */
@EActivity
public class SettingsActivity extends ActionBarActivity {

    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

    @Pref
    NotificationPreferences_ preferences;

    @Bean
    NotificationHelper notificationHelper;

    @ViewById(R.id.concat_switch)
    SwitchCompat concatSwitch;

    @ViewById(R.id.filter_switch)
    SwitchCompat filterSwitch;

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.filter_switch:
                    preferences.hideFilter().put(!isChecked);
                    break;
                case R.id.concat_switch:
                    if (isChecked != preferences.concatNotifications().get()) {
                        preferences.concatNotifications().put(isChecked);
                        try {
                            notificationHelper.hideGroupedNotifications();
                            notificationHelper.showNotifications(stickyNotificationDao.queryForAll());
                        } catch (SQLException e) {
                            Log.e(SettingsActivity.class.getSimpleName(), "Taratata", e);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        EasyTracker.getInstance(this).activityStart(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @AfterViews
    void initSwitch() {
        concatSwitch.setOnCheckedChangeListener(checkedChangeListener);
        concatSwitch.setChecked(preferences.concatNotifications().get());

        filterSwitch.setOnCheckedChangeListener(checkedChangeListener);
        filterSwitch.setChecked(!preferences.hideFilter().get());
    }

    @OptionsItem(android.R.id.home)
    void goBack() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.go_to_right);
    }
}
