package com.github.quentin7b.sn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.database.DatabaseHelper
import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.view.StickyNoteFullView
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.themedAppBarLayout
import org.jetbrains.anko.support.v4.nestedScrollView


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

    private var ui: DetailsActivityUI? = null
    private var notification: StickyNotification? = null
    private var databaseHelper: DatabaseHelper.StickyDao? = null
    private var showTransition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = DetailsActivityUI()
        ui!!.setContentView(this)

        setSupportActionBar(ui!!.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        notification = intent.getParcelableExtra(EXTRA_NOTE)
        if (notification == null) {
            notification = StickyNotification()
        }

        showTransition = intent.getBooleanExtra(EXTRA_TRANSITION, false)

        databaseHelper = DatabaseHelper(this).database

        ui!!.title.setText(notification?.title)
        ui!!.title.setSelection(notification?.title!!.length)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
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

    private fun goMain() {
        if (showTransition) {
            supportFinishAfterTransition()
        } else {
            NavUtils.navigateUpFromSameTask(this)
        }
    }

    private fun notificationIsValid(notification: StickyNotification): Boolean {
        return !notification.title.trim { it <= ' ' }.isEmpty()
    }

    class DetailsActivityUI : AnkoComponent<DetailsActivity> {

        lateinit var toolbar: Toolbar
        lateinit var title: EditText
        var details: StickyNoteFullView = StickyNoteFullView.newInstance()

        override fun createView(ui: AnkoContext<DetailsActivity>): View {
            return with(ui) {

                verticalLayout {

                    themedAppBarLayout(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                        fitsSystemWindows = true

                        toolbar = toolbar {
                            popupTheme = R.style.ThemeOverlay_AppCompat_Light
                        }.lparams(width = matchParent, height = wrapContent)

                        title = themedEditText(R.style.Base_Theme_Sticky_TitleTextLabel) {
                            hintResource = R.string.hint_title
                            textSize = 16f
                        }.lparams(width = matchParent, height = wrapContent) {
                            leftMargin = dip(16)
                            rightMargin = dip(16)
                        }

                    }.lparams(width = matchParent, height = wrapContent)

                    nestedScrollView {

                        frameLayout {
                            id = R.id.detailsContent
                            owner.supportFragmentManager.beginTransaction().replace(R.id.detailsContent, details).commit()
                        }.lparams(width = matchParent, height = matchParent)

                    }.lparams(width = matchParent, height = matchParent, weight = 1f)

                }

            }
        }

    }

}
