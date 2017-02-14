package com.github.quentin7b.sn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.view.StickyNoteFullView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "fr.quentinklein.stickynotifs.EXTRA_NOTE";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.note_title_et)
    AppCompatEditText noteTitle;

    @BindView(R.id.sticky_nfv)
    StickyNoteFullView noteFullView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private StickyNotification notification;

    public static Intent getIntent(Context context, @Nullable StickyNotification notification) {
        Intent intent = new Intent(context, DetailsActivity.class);
        if (notification != null) {
            intent.putExtra(EXTRA_NOTE, notification);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        this.notification = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (this.notification == null) {
            this.notification = new StickyNotification();
        }

        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        ViewCompat.setTransitionName(this.fab, getString(R.string.transition_fab));

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                onDeleteNote();
            case android.R.id.home:
                supportFinishAfterTransition();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        this.noteTitle.setText(this.notification.getTitle());
        this.noteTitle.setSelection(this.notification.getTitle().length());

        this.noteFullView.setContent(this.notification.getContent());
        this.noteFullView.setNotification(this.notification.isNotification());
        this.noteFullView.setDefcon(this.notification.getDefcon());
    }

    @OnClick(R.id.fab)
    void onSaveNote() {
        try {
            this.notification.setTitle(this.noteTitle.getText().toString());
            this.notification.setContent(this.noteFullView.getContent());
            this.notification.setNotification(this.noteFullView.isNotification());
            this.notification.setDefcon(this.noteFullView.getDefcon());
            new DatabaseHelper(this).getDatabase().save(this.notification);
            setResult(MainActivity.RESULT_CODES.NOTE_CREATED);
        } catch (Exception e) {
            Log.e("", "", e);
        }
        supportFinishAfterTransition();
    }

    void onDeleteNote() {
        if (this.notification.getId() > 0) {
            final StickyNotification cloneNotification = new StickyNotification(this.notification);
            try {
                new DatabaseHelper(this).getDatabase().delete(this.notification);

                setResult(MainActivity.RESULT_CODES.NOTE_DELETED,
                        new Intent().putExtra(MainActivity.EXTRA_NOTE, cloneNotification));
            } catch (Exception e) {
                Log.e("", "", e);
            }
        }
        supportFinishAfterTransition();
    }

}
