package fr.quentinklein.stickynotifs.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.BuildConfig;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;

/**
 * Created by quentin on 25/10/14.
 */
@EActivity
public class AboutActivity extends AppCompatActivity {

    @Pref
    NotificationPreferences_ preferences;

    @Click(R.id.about_rate)
    void buttonClick() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())
                    )
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitle(R.string.action_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String versionName = "?";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(AboutActivity.class.getSimpleName(), "Unable to find version name", e);
        }

        TextView version = (TextView) findViewById(R.id.about_version);
        version.setText(getString(R.string.about_version) + ' ' + versionName);
    }

    @AfterViews
    void gcm() {
        if (!BuildConfig.DEBUG && preferences.analytics().get()) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @OptionsItem(android.R.id.home)
    @Override
    public void onBackPressed() {
        finish();
    }
}
