package com.github.quentin7b.sn.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.github.quentin7b.sn.NotificationHelper
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.database.DatabaseHelper
import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.view.StickyNoteRecyclerView
import com.github.quentin7b.sn.widget.StickyWidgetProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), StickyNoteRecyclerView.NoteListener {

    private var databaseHelper: DatabaseHelper.StickyDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        databaseHelper = DatabaseHelper(this).database
        ViewCompat.setTransitionName(main_fab, getString(R.string.transition_fab))

        main_fab?.setOnClickListener { showNote(null, true) }
        notes_snlv?.setNoteListener(this)
    }

    override fun onResume() {
        super.onResume()
        when (intent.action) {
            ACTION_NOTIFICATION -> {
                val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
                if (notificationId != -1) {
                    val notification = databaseHelper!!.one(notificationId.toInt())
                    showNote(notification!!, false)
                } else {
                    Log.i("MainActivity", "Notification action but no id passed $notificationId")
                }
            }
            ACTION_COMPOSE -> {
                showNote(StickyNotification(), false)
            }
            else -> {
                loadNotifications()
                // refresh widgets
                StickyWidgetProvider.triggerNoteListWidgetUpdate(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DETAIL_RC -> if (resultCode == RESULT_DELETED) {
                val note = data!!.getParcelableExtra<StickyNotification>(EXTRA_NOTIFICATION)
                confirmNoteDeleted(note)
            } else if (resultCode == RESULT_FINISH) {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun confirmNoteDeleted(note: StickyNotification) {
        showSnackbar(
                cla as View,
                R.string.notification_deleted,
                Snackbar.LENGTH_LONG, android.R.string.cancel
        ) {
            databaseHelper!!.save(note)
            showSnackbar(
                    cla as View,
                    R.string.notification_restored,
                    Snackbar.LENGTH_SHORT, -1, null)
            loadNotifications()
        }
    }

    private fun loadNotifications() {
        val notifications = databaseHelper!!.all
        notes_snlv!!.setNotificationsList(notifications)
        NotificationHelper.showNotifications(this@MainActivity, notifications)
    }

    override fun onNoteSelected(note: StickyNotification) {
        showNote(note, true)
    }

    override fun onNoteSwiped(note: StickyNotification) {
        databaseHelper!!.delete(note)
        loadNotifications()
        confirmNoteDeleted(StickyNotification(note)) // As a new one
    }

    private fun showNote(note: StickyNotification?, withTransition: Boolean) {
        if (note_snfv == null) {
            ActivityCompat.startActivityForResult(
                    this,
                    if (note !== null)
                        DetailsActivity.newIntent(this, note, withTransition)
                    else
                        DetailsActivity.newIntent(this, withTransition),
                    DETAIL_RC,
                    if (withTransition)
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, main_fab as View, getString(R.string.transition_fab))
                                .toBundle()
                    else
                        null)
        } else {
            // note_snfv?.notification = note
        }
    }

    private fun showSnackbar(anchor: View, @StringRes message: Int, duration: Int, @StringRes actionMessage: Int, actionListener: ((View) -> Unit)?) {
        val snackbar = Snackbar
                .make(anchor, message, duration)
        if (actionMessage != -1) {
            snackbar.setAction(actionMessage, actionListener)
        }
        val view = snackbar.view
        val tv = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
        tv.setTextColor(Color.WHITE)
        snackbar.show()
    }

    companion object {
        private const val DETAIL_RC = 8471
        const val RESULT_DELETED = 1234
        const val RESULT_FINISH = 5678
        const val ACTION_NOTIFICATION = "com.github.quentin7b.sn.ACTION_NOTIFICATION"
        const val ACTION_COMPOSE = "com.github.quentin7b.sn.ACTION_COMPOSE"
        const val EXTRA_NOTIFICATION = "com.github.quentin7b.sn.EXTRA_NOTE"
        const val EXTRA_NOTIFICATION_ID = "com.github.quentin7b.sn.EXTRA_NOTE_ID"
    }
}
