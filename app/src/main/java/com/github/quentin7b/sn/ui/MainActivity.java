package com.github.quentin7b.sn.ui;

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
import android.util.Log;
import android.view.View;

import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.view.StickyNoteFullView;
import com.github.quentin7b.sn.ui.view.StickyNoteListView;

import java.sql.SQLException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class MainActivity
        extends AppCompatActivity
        implements StickyNoteListView.NoteSelectedListener {

    public static final String EXTRA_NOTE = "fr.quentinklein.stickynotifs.EXTRA_NOTE";
    private static final int DETAIL_RC = 8471;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cla)
    CoordinatorLayout cla;
    @BindView(R.id.notes_snlv)
    StickyNoteListView noteListView;
    @Nullable
    @BindView(R.id.note_snfv)
    StickyNoteFullView noteFullView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.noteListView.setNoteSelectionListener(this);
        setSupportActionBar(this.toolbar);

        ViewCompat.setTransitionName(this.fab, getString(R.string.transition_fab));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DETAIL_RC:
                if (resultCode == RESULT_CODES.NOTE_CREATED) {

                } else if (resultCode == RESULT_CODES.NOTE_DELETED) {
                    final StickyNotification note = data.getParcelableExtra(EXTRA_NOTE);
                    Snackbar
                            .make(cla, "Notification has been deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        new DatabaseHelper(MainActivity.this).getDatabase().save(note);
                                        Snackbar.make(cla, "Notification has been restored!", Snackbar.LENGTH_SHORT).show();
                                        loadNotifications();
                                    } catch (SQLException e) {
                                        Log.e("", "", e);
                                    }
                                }
                            }).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadNotifications() {
        try {
            this.noteListView.setNotificationsList(new DatabaseHelper(this).getDatabase().getAll());
        } catch (SQLException e) {
            Log.e("", "", e);
        }
    }

    @Override
    public void onNoteSelected(StickyNotification note) {
        showNote(note);
    }

    @Optional
    @OnClick(R.id.fab)
    void addNoteClicked() {
        showNote(new StickyNotification());
    }

    private void showNote(StickyNotification note) {
        if (noteFullView == null) {
            ActivityCompat.startActivityForResult(
                    this,
                    DetailsActivity.getIntent(this, note),
                    DETAIL_RC,
                    ActivityOptionsCompat.
                            makeSceneTransitionAnimation(this, fab, getString(R.string.transition_fab))
                            .toBundle());
        } else {
            this.noteFullView.setTitle(note.getTitle());
            this.noteFullView.setContent(note.getContent());
        }
    }

    public static final class RESULT_CODES {
        static final int NOTE_CREATED = 364517;
        static final int NOTE_DELETED = 4726181;
    }
}
