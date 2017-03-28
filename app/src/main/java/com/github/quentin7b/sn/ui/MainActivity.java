package com.github.quentin7b.sn.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.quentin7b.sn.NotificationHelper;
import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.Tool;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.view.StickyNoteFullView;
import com.github.quentin7b.sn.ui.view.StickyNoteRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class MainActivity extends AppCompatActivity
        implements StickyNoteRecyclerView.NoteListener {

    private static final int DETAIL_RC = 8471;
    public static final int RESULT_DELETED = 1234;
    public static final int RESULT_FINISH = 5678;
    public static final String ACTION_NOTIFICATION = "com.github.quentin7b.sn.ACTION_NOTIFICATION";
    public static final String EXTRA_NOTIFICATION = "com.github.quentin7b.sn.EXTRA_NOTE";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cla)
    CoordinatorLayout cla;

    @BindView(R.id.notes_snlv)
    StickyNoteRecyclerView noteListView;

    @Nullable
    @BindView(R.id.note_snfv)
    StickyNoteFullView noteFullView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private DatabaseHelper.StickyDao databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        noteListView.setNoteListener(this);
        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this).getDatabase();
        ViewCompat.setTransitionName(fab, getString(R.string.transition_fab));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ACTION_NOTIFICATION.equals(getIntent().getAction())) {
            showNote((StickyNotification) getIntent().getParcelableExtra(EXTRA_NOTIFICATION), false);
        } else {
            loadNotifications();
            refreshWidgets();
        }
    }

    private void refreshWidgets() {
        sendBroadcast(new Intent().setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DETAIL_RC:
                if (resultCode == RESULT_DELETED) {
                    final StickyNotification note = data.getParcelableExtra(EXTRA_NOTIFICATION);
                    confirmNoteDeleted(note);
                } else if (resultCode == RESULT_FINISH) {
                    finish();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void confirmNoteDeleted(final StickyNotification note) {
        Tool.showSnackbar(
                cla,
                R.string.notification_deleted,
                Snackbar.LENGTH_LONG, android.R.string.cancel,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseHelper.save(note);
                        Tool.showSnackbar(
                                cla,
                                R.string.notification_restored,
                                Snackbar.LENGTH_SHORT, -1, null);
                        loadNotifications();
                    }
                });
    }

    private void loadNotifications() {
        List<StickyNotification> notifications = databaseHelper.getAll();
        noteListView.setNotificationsList(notifications);
        NotificationHelper.showNotifications(MainActivity.this, notifications);
    }

    @Override
    public void onNoteSelected(StickyNotification note) {
        showNote(note, true);
    }

    @Override
    public void onNoteSwiped(StickyNotification note) {
        databaseHelper.delete(note);
        loadNotifications();
        confirmNoteDeleted(new StickyNotification(note)); // As a new one
    }

    @Optional
    @OnClick(R.id.fab)
    void addNoteClicked() {
        showNote(new StickyNotification(), true);
    }

    private void showNote(StickyNotification note, boolean withTransition) {
        if (noteFullView == null) {
            ActivityCompat.startActivityForResult(
                    this,
                    DetailsActivity.getIntent(this, note, withTransition),
                    DETAIL_RC,
                    withTransition
                            ? ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, fab, getString(R.string.transition_fab))
                            .toBundle()
                            : null);
        } else {
            noteFullView.setTitle(note.getTitle());
            noteFullView.setContent(note.getContent());
        }
    }
}
