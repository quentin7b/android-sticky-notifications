package fr.quentinklein.stickynotifs.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.quentinklein.stickynotifs.APIUtils;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.boot.StartUpService_;
import fr.quentinklein.stickynotifs.manager.StickyNotificationManager;
import fr.quentinklein.stickynotifs.model.NotificationPreferences_;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment;
import fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment_;
import fr.quentinklein.stickynotifs.widget.StickyWidgetProvider;

/**
 * Created by quentin on 19/07/2014.
 * List of notifications
 *
 * @see fr.quentinklein.stickynotifs.ui.fragments.NotesListFragment
 */
@EActivity(R.layout.activity_list_notes)
public class NotesListActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "note_id";

    @Pref
    NotificationPreferences_ preferences;

    @Bean
    StickyNotificationManager mStickyNotificationsManager;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private NotesListFragment mAllNotesFragment = NotesListFragment_.builder().build();

    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            LocalBroadcastManager.getInstance(NotesListActivity.this).sendBroadcast(
                    new Intent(NotesListFragment.FILTER_EVENT).putExtra("extra_filter", s)
            );
            return true;
        }
    };

    @AfterViews
    void init() {
        setSupportActionBar(mToolbar);
        getSupportFragmentManager().beginTransaction().add(R.id.container, mAllNotesFragment).commit();
        showHelpDialog();
        mAllNotesFragment.getBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
        startService(new Intent(this, StartUpService_.class));
        reloadWidgets();
        int primaryColor = APIUtils.getColor(this, preferences.colorPrimary().get());
        int primaryDarkColor = APIUtils.getColor(this, preferences.colorPrimatyDark().get());
        mToolbar.setBackgroundColor(primaryColor);
        // Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(primaryDarkColor);
            window.setNavigationBarColor(primaryDarkColor);
        }
        // Change fab color
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes, menu);
        // Handle the search menu
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(mQueryTextListener);
        }
        return super.onCreateOptionsMenu(menu);
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

    public void onEvent(NotesListFragment.NoteSavedEvent event) {
        reloadWidgets();
    }

    public void onEvent(NotesListFragment.NoteAddedEvent event) {
        reloadWidgets();
    }

    public void onEvent(NotesListFragment.NoteDeletedEvent event) {
        reloadWidgets();
    }

    public void onEvent(NotesListFragment.NoteSelectedEvent event) {
        startActivity(new Intent(this, NoteActivity_.class).putExtra(NoteActivity_.NOTIFICATION_ID_EXTRA, event.noteId));
    }

    private void showHelpDialog() {
        if (!preferences.askedForHelp().get()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.settings_help_dev)
                    .content(R.string.settings_help_dev_explain)
                    .positiveText(R.string.help_me)
                    .negativeText(R.string.no_thanks)
                    .cancelable(false)
                    .theme(Theme.LIGHT)
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

    private void loadNotes() {
        mAllNotesFragment.refreshNotesList();
    }

    private void reloadWidgets() {
        sendBroadcast(new Intent(this, StickyWidgetProvider.class)
                .setAction(StickyWidgetProvider.UPDATE_LIST));
    }
}
