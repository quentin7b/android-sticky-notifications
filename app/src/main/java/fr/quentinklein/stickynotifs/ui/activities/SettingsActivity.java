package fr.quentinklein.stickynotifs.ui.activities;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.analytics.tracking.android.EasyTracker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.ui.dialog.ColorDialog;

/**
 * Created by quentin on 23/10/14.
 */
@EActivity(R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    @Pref
    NotificationPreferences_ preferences;

    @Bean
    StickyNotificationManager mStickyNotificationManager;

    @Bean
    NotificationHelper notificationHelper;

    @ViewById(R.id.concat_switch)
    SwitchCompat concatSwitch;

    @ViewById(R.id.dev_switch)
    SwitchCompat devSwitch;

    @ViewById(R.id.theme_btn)
    Button themeBtn;

    @ViewById(R.id.app_toolbar)
    Toolbar toolbar;

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.concat_switch:
                    if (isChecked != preferences.concatNotifications().get()) {
                        preferences.concatNotifications().put(isChecked);
                        notificationHelper.hideGroupedNotifications();
                        notificationHelper.showNotifications(mStickyNotificationManager.getNotifications());
                    }
                    break;
                case R.id.dev_switch:
                    preferences.analytics().put(isChecked);
                    break;
            }
        }
    };

    @AfterViews
    void initScreen() {
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setWindowColors(preferences.colorPrimary().get(), preferences.colorPrimatyDark().get());

        if (!BuildConfig.DEBUG && preferences.analytics().get()) {
            EasyTracker.getInstance(this).activityStart(this);
        }
        concatSwitch.setChecked(preferences.concatNotifications().get());
        concatSwitch.setOnCheckedChangeListener(checkedChangeListener);

        devSwitch.setChecked(preferences.analytics().get());
        devSwitch.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void setWindowColors(int primaryColor, int primaryDarkColor) {
        int color = getResources().getColor(primaryColor);
        toolbar.setBackgroundColor(color);
        themeBtn.setBackgroundColor(color);
        // Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            color = getResources().getColor(primaryDarkColor);
            Window window = getWindow();
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

    @OptionsItem(android.R.id.home)
    void goBack() {
        finish();
    }

    @Click(R.id.theme_btn)
    void changeThemeClicked() {
        ColorDialog.show(this, new ColorDialog.ColorListener() {
            @Override
            public void onColorChanged(final int primaryColor, final int secondaryColor) {
                themeBtn.setBackgroundColor(getResources().getColor(primaryColor));
                setWindowColors(primaryColor, secondaryColor);
            }

            @Override
            public void onColorValidated(final int primaryColor, final int secondaryColor) {
                preferences.colorPrimary().put(primaryColor);
                preferences.colorPrimatyDark().put(secondaryColor);
                setWindowColors(primaryColor, secondaryColor);
            }

            @Override
            public void onCancel() {
                setWindowColors(preferences.colorPrimary().get(), preferences.colorPrimatyDark().get());
            }
        }, preferences.colorPrimary().get(), preferences.colorPrimatyDark().get());
    }
}
