package com.github.quentin7b.sn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.database.DatabaseHelper
import com.github.quentin7b.sn.database.model.StickyNotification
import kotlinx.android.synthetic.main.activity_detail.*


class DetailsActivity : AppCompatActivity() {

    companion object {

        private val EXTRA_NOTE = "com.github.quentin7b.sn.EXTRA_NOTE"
        private val EXTRA_TRANSITION = "com.github.quentin7b.sn.EXTRA_TRANSITION"

        fun newIntent(context: Context, notification: StickyNotification, withTransition: Boolean): Intent {
            return Intent(context, DetailsActivity::class.java)
                    .putExtra(EXTRA_NOTE, notification)
                    .putExtra(EXTRA_TRANSITION, withTransition)
        }
    }

    private var notification: StickyNotification? = null
    private var databaseHelper: DatabaseHelper.StickyDao? = null
    private var showTransition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        ViewCompat.setTransitionName(fab, getString(R.string.transition_fab))


        notification = intent.getParcelableExtra(EXTRA_NOTE)
        if (notification == null) {
            notification = StickyNotification()
        }

        showTransition = intent.getBooleanExtra(EXTRA_TRANSITION, false)


        fab.setOnClickListener {
            val notificationId = notification?.id
            notification = sticky_nfv.notification
            notification?.id = notificationId!!
            notification?.title = note_title_et?.text.toString()
            if (notification?.title?.trim { it <= ' ' }?.isEmpty() == false) {
                // note_title_et_parent.error = null
                databaseHelper?.save(notification)
                setResult(RESULT_OK)
                goMain()
            } else {
                note_title_et_parent?.error = getString(R.string.error_title_empty)
            }
        }

        databaseHelper = DatabaseHelper(this).database

        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                if (notification!!.id > 0) {
                    val cloneNotification = StickyNotification(notification)
                    databaseHelper?.delete(notification)
                    setResult(MainActivity.RESULT_DELETED,
                            Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, cloneNotification))
                }
                goMain()
                return true
            }
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                goMain()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (showTransition) {
            supportFinishAfterTransition()
        } else {
            setResult(MainActivity.RESULT_FINISH)
            finish()
        }
    }

    private fun initViews() {
        note_title_et?.setText(notification?.title)
        note_title_et?.setSelection(notification?.title!!.length)

        sticky_nfv.notification = notification
    }

    private fun goMain() {
        if (showTransition) {
            supportFinishAfterTransition()
        } else {
            NavUtils.navigateUpFromSameTask(this)
        }
    }
}
