package fr.quentinklein.stickynotifs.ui.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.ui.fragments.NoteFragment;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteDeletedListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteSavedListener;
import fr.quentinklein.stickynotifs.widget.StickyWidgetProvider;

/**
 * Created by quentin on 19/07/2014.
 * List of notifications
 *
 * @see fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment
 */
@EActivity(R.layout.activity_list_notes)
@OptionsMenu(R.menu.notes)
public class NotesListActivity extends ActionBarActivity implements NoteSavedListener, NoteChanedListener, HideNoteListener, NoteDeletedListener, NotesListFragment.TwoPartProvider {

    @FragmentById(R.id.notes_fragment)
    NotesListFragment fragment;

    /**
     * Not used for now (special tablet layout)
     */
    @FragmentById(R.id.note_fragment)
    NoteFragment noteFragment;

    AboutDialog aboutDialog;


    @Override
    public void hideNote() {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.hideNote();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragment.refreshNotesList();
        reloadWidgets();
    }

    /**
     * Functions
     */

    @AfterViews
    void log() {
        EasyTracker.getInstance(this).activityStart(this);
        fragment.refreshNotesList();
        reloadWidgets();
    }


    /**
     * Menu items
     */

    @OptionsItem(R.id.action_new)
    void addNote() {
        startActivity(new Intent(NotesListActivity.this, NoteActivity_.class));
    }

    @OptionsItem(R.id.action_about)
    void about() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(this);
        }
        aboutDialog.show();
    }

    @OptionsItem(R.id.action_delete)
    void delete() {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.deleteNote();
        }
        reloadWidgets();
    }

    /**
     * Interfaces
     */

    @Override
    public void noteSaved(int noteId) {
        fragment.refreshNotesList();
        reloadWidgets();
    }

    @Override
    public void noteSelected(int noteId) {
        if (noteFragment != null && noteFragment.isInLayout()) {
            noteFragment.noteSelected(noteId);
        } else {
            startActivity(new Intent(this, NoteActivity_.class).putExtra(NoteActivity_.NOTIFICATION_ID_EXTRA, noteId));
        }
    }

    @Override
    public void noteDeleted(int noteId) {
        fragment.refreshNotesList();
        reloadWidgets();
    }

    /**
     * Tablet mode provider
     *
     * @return true if the master-detail on tablet is on
     */
    @Override
    public boolean isTwoPartMode() {
        return (noteFragment != null && noteFragment.isInLayout());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        aboutDialog.dismiss();
    }

    private void reloadWidgets() {
        /*Log.i(NotesListActivity.class.getSimpleName(), "ReloadWidgets");
        // Refresh widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        final int[] stickyWidgetIds =
                appWidgetManager.getAppWidgetIds(
                        new ComponentName(getApplicationContext(), StickyWidgetProvider.class));
        // Notify dataset changed
        appWidgetManager.notifyAppWidgetViewDataChanged(stickyWidgetIds, R.layout.widget_sticky);*/
        sendBroadcast(new Intent(this, StickyWidgetProvider.class)
                .setAction(StickyWidgetProvider.UPDATE_LIST));
    }

    /**
     * Special dialog
     */
    private static class AboutDialog extends Dialog {

        private Context context;

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.about_rate:
                        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            context.startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            EasyTracker.getInstance(context.getApplicationContext()).send(
                                    MapBuilder.createException(
                                            new StandardExceptionParser(getContext(), null)
                                                    // Context and optional collection of package names to be used in reporting the exception.
                                                    .getDescription(Thread.currentThread().getName(),
                                                            // The name of the thread on which the exception occurred.
                                                            e),                                  // The exception.
                                            false
                                    ).build()
                            );
                            context.startActivity(
                                    new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())
                                    )
                            );
                        }
                        break;
                    case R.id.about_complain:
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:klein.quentin@gmail.com"));

                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"klein.quentin@gmail.com"});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.complain_subject));
                        emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.complain_message));

                        try {
                            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.complain_choose)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getContext(), R.string.complain_error, Toast.LENGTH_SHORT).show();
                            EasyTracker.getInstance(context.getApplicationContext()).send(
                                    MapBuilder.createException(
                                            new StandardExceptionParser(getContext(), null)
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
        };

        public AboutDialog(Context context) {
            super(context, android.R.style.Theme_Holo_Dialog);
            this.context = context;
            setTitle(R.string.action_about);
            setContentView(R.layout.dialog_activity_main_about);
            // Get version name
            String versionName = "?";
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionName = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(AboutDialog.class.getSimpleName(), "Unable to find version name", e);
                EasyTracker.getInstance(context.getApplicationContext()).send(
                        MapBuilder.createException(
                                new StandardExceptionParser(getContext(), null)
                                        // Context and optional collection of package names to be used in reporting the exception.
                                        .getDescription(Thread.currentThread().getName(),
                                                // The name of the thread on which the exception occurred.
                                                e),                                  // The exception.
                                false
                        ).build()
                );
            }

            TextView version = (TextView) findViewById(R.id.about_version);
            version.setText(context.getString(R.string.about_version) + ' ' + versionName);

            // Buttons
            findViewById(R.id.about_rate).setOnClickListener(listener);
            findViewById(R.id.about_complain).setOnClickListener(listener);
        }

    }
}
