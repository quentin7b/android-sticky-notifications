package com.github.quentin7b.sn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.Tool;
import com.github.quentin7b.sn.database.DatabaseHelper;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.view.StickyNoteFullView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "com.github.quentin7b.sn.EXTRA_NOTE";
    private static final String EXTRA_TRANSITION = "com.github.quentin7b.sn.EXTRA_TRANSITION";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.note_title_et)
    TextInputEditText noteTitle;
    @BindView(R.id.note_title_et_parent)
    TextInputLayout noteTitleParent;

    @BindView(R.id.sticky_nfv)
    StickyNoteFullView noteFullView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private StickyNotification notification;
    private DatabaseHelper.StickyDao databaseHelper;
    private boolean showTransition;

    public static Intent getIntent(Context context, @Nullable StickyNotification notification, boolean withTransition) {
        Intent intent = new Intent(context, DetailsActivity.class);
        if (notification != null) {
            intent.putExtra(EXTRA_NOTE, notification);
        }
        intent.putExtra(EXTRA_TRANSITION, withTransition);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        notification = getIntent().getParcelableExtra(EXTRA_NOTE);
        showTransition = getIntent().getBooleanExtra(EXTRA_TRANSITION, false);
        if (notification == null) {
            notification = new StickyNotification();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        ViewCompat.setTransitionName(fab, getString(R.string.transition_fab));

        databaseHelper = new DatabaseHelper(this).getDatabase();

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
                return true;
            case android.R.id.home:
                setResult(MainActivity.RESULT_CANCELED);
                goMain();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (showTransition) {
            supportFinishAfterTransition();
        } else {
            setResult(MainActivity.RESULT_FINISH);
            finish();
        }
    }

    private void initViews() {
        noteTitle.setText(notification.getTitle());
        noteTitle.setSelection(notification.getTitle().length());

        noteFullView.setContent(notification.getContent());
        noteFullView.setNotification(notification.isNotification());
        noteFullView.setDefcon(notification.getDefcon());

        noteFullView.setDate(notification.getDeadLine());
    }

    @OnClick(R.id.fab)
    void onSaveNote() {
        Log.d("DA", "save");
        notification.setTitle(noteTitle.getText().toString());
        notification.setContent(noteFullView.getContent());
        notification.setNotification(noteFullView.isNotification());
        notification.setDefcon(noteFullView.getDefcon());
        notification.setDeadLine(noteFullView.getDate());
        if (Tool.notificationIsValid(notification)) {
            noteTitleParent.setError(null);
            databaseHelper.save(notification);
            setResult(MainActivity.RESULT_OK);
            goMain();
        } else {
            noteTitleParent.setError(getString(R.string.error_title_empty));
        }
    }

    void onDeleteNote() {
        if (notification.getId() > 0) {
            final StickyNotification cloneNotification = new StickyNotification(notification);
            databaseHelper.delete(notification);
            setResult(MainActivity.RESULT_DELETED,
                    new Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, cloneNotification));
        }
        goMain();
    }

    private void goMain() {
        if (showTransition) {
            supportFinishAfterTransition();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

}
