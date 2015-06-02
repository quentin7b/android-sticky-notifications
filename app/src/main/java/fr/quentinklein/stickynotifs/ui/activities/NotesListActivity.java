package fr.quentinklein.stickynotifs.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.boot.StartUpService_;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment_;
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
public class NotesListActivity extends AppCompatActivity
        implements NoteSavedListener, NoteChanedListener, NoteDeletedListener {

    public static final String EXTRA_NOTE_ID = "note_id";

    @Pref
    NotificationPreferences_ preferences;

    private NotesListFragment
            mAllNotesFragment = NotesListFragment_.builder().defcon(null).build(),
            mUltraNotesFragment = NotesListFragment_.builder().defcon(StickyNotification.Defcon.ULTRA).build(),
            mImportantNotesFragment = NotesListFragment_.builder().defcon(StickyNotification.Defcon.IMPORTANT).build(),
            mNormalNotesFragment = NotesListFragment_.builder().defcon(StickyNotification.Defcon.NORMAL).build(),
            mUselessNotesFragment = NotesListFragment_.builder().defcon(StickyNotification.Defcon.USELESS).build();

    @AfterViews
    void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        assert viewPager != null;
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            tabLayout.setElevation(px);
            fab.setElevation(px);
        }

        if (!preferences.askedForHelp().get()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.settings_help_dev)
                    .content(R.string.settings_help_dev_explain)
                    .positiveText(R.string.help_me)
                    .negativeText(R.string.no_thanks)
                    .cancelable(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            preferences.askedForHelp().put(true);
                            preferences.analytics().put(true);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            preferences.askedForHelp().put(true);
                            preferences.analytics().put(false);
                        }
                    })
                    .show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
        startService(new Intent(this, StartUpService_.class));
        reloadWidgets();
    }

    /**
     * Initialize the viewpager with the levels
     *
     * @param viewPager the view pager to init
     */
    private void setupViewPager(ViewPager viewPager) {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mAllNotesFragment;
                    case 1:
                        return mUltraNotesFragment;
                    case 2:
                        return mImportantNotesFragment;
                    case 3:
                        return mNormalNotesFragment;
                    case 4:
                        return mUselessNotesFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                int textRes = 0;
                switch (position) {
                    case 0:
                        textRes = R.string.all;
                        break;
                    case 1:
                        textRes = R.string.ultra;
                        break;
                    case 2:
                        textRes = R.string.important;
                        break;
                    case 3:
                        textRes = R.string.normal;
                        break;
                    case 4:
                        textRes = R.string.useless;
                        break;

                }
                return getString(textRes);
            }
        });
    }

    /**
     * Menu items.
     */
    @Click(R.id.fab)
    public final void addNote() {
        startActivity(new Intent(NotesListActivity.this, NoteActivity_.class));
    }

    @OptionsItem(R.id.action_about)
    void about() {
        startActivity(new Intent(this, AboutActivity_.class));
    }

    @OptionsItem(R.id.action_settings)
    void configure() {
        startActivity(new Intent(this, SettingsActivity_.class));
    }

    /**
     * Interfaces.
     */

    @Override
    public void noteSaved(int noteId) {
        loadNotes();
        reloadWidgets();
    }

    @Override
    public void noteSelected(int noteId) {
        startActivity(new Intent(this, NoteActivity_.class).putExtra(NoteActivity_.NOTIFICATION_ID_EXTRA, noteId));
    }

    @Override
    public void noteDeleted(int noteId) {
        // fragment.refreshNotesList();
        reloadWidgets();
    }

    private void loadNotes() {
        mAllNotesFragment.refreshNotesList();
        mUltraNotesFragment.refreshNotesList();
        mImportantNotesFragment.refreshNotesList();
        mNormalNotesFragment.refreshNotesList();
        mUselessNotesFragment.refreshNotesList();
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
}
