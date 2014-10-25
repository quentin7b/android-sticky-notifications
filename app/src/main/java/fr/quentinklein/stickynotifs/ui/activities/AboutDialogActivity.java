package fr.quentinklein.stickynotifs.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;

import fr.quentinklein.stickynotifs.R;

/**
 * Created by quentin on 25/10/14.
 */
@EActivity
@WindowFeature(Window.FEATURE_NO_TITLE)
public class AboutDialogActivity extends ActionBarActivity {

    @Click({R.id.about_rate, R.id.about_complain})
    void buttonClick(View view) {
        switch (view.getId()) {
            case R.id.about_rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    EasyTracker.getInstance(getApplicationContext()).send(
                            MapBuilder.createException(
                                    new StandardExceptionParser(AboutDialogActivity.this, null)
                                            // Context and optional collection of package names to be used in reporting the exception.
                                            .getDescription(Thread.currentThread().getName(),
                                                    // The name of the thread on which the exception occurred.
                                                    e),                                  // The exception.
                                    false
                            ).build()
                    );
                    startActivity(
                            new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())
                            )
                    );
                }
                break;
            case R.id.about_complain:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:klein.quentin@gmail.com"));

                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"klein.quentin@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.complain_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.complain_message));

                try {
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.complain_choose)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(AboutDialogActivity.this, R.string.complain_error, Toast.LENGTH_SHORT).show();
                    EasyTracker.getInstance(getApplicationContext()).send(
                            MapBuilder.createException(
                                    new StandardExceptionParser(AboutDialogActivity.this, null)
                                            // Context and optional collection of package names to be used in reporting the exception.
                                            .getDescription(Thread.currentThread().getName(),
                                                    // The name of the thread on which the exception occurred.
                                                    ex),                                  // The exception.
                                    false
                            ).build()
                    );
                    Log.e(NotesListActivity.class.getSimpleName(), "Error while complaining", ex);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_dialog);
        EasyTracker.getInstance(this).activityStart(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.action_about));

        String versionName = "?";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(AboutDialogActivity.class.getSimpleName(), "Unable to find version name", e);
            EasyTracker.getInstance(getApplicationContext()).send(
                    MapBuilder.createException(
                            new StandardExceptionParser(AboutDialogActivity.this, null)
                                    // Context and optional collection of package names to be used in reporting the exception.
                                    .getDescription(Thread.currentThread().getName(),
                                            // The name of the thread on which the exception occurred.
                                            e),                                  // The exception.
                            false
                    ).build()
            );
        }

        TextView version = (TextView) findViewById(R.id.about_version);
        version.setText(getString(R.string.about_version) + ' ' + versionName);
    }
}
