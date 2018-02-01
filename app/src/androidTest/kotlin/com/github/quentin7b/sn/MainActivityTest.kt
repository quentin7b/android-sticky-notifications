package com.github.quentin7b.sn

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.swipeLeft
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.github.quentin7b.sn.database.model.StickyNotification
import com.github.quentin7b.sn.ui.MainActivity
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    companion object {
        @ClassRule
        @JvmField
        val localeTestRule = LocaleTestRule()
    }

    @Rule
    @JvmField
    val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java, true, true)

    @Before
    fun setUp() {
    }

    @Test
    fun testEmpty() {
        onView(withId(R.id.notes_snlv)).check(matches(isDisplayed()))
        Screengrab.screenshot("home_empty")
    }

    @Test
    fun testComplete() {
        val resources = InstrumentationRegistry.getContext().resources

        val noteTitle1 = resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_1_title)
        val noteTitle2 = resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_2_title)
        val noteTitle3 = resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_3_title)
        val noteTitle4 = resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_4_title)
        addNote(noteTitle1,
                resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_1_content),
                StickyNotification.Defcon.ULTRA)
        addNote(noteTitle2,
                resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_2_content),
                StickyNotification.Defcon.IMPORTANT)
        addNote(noteTitle3,
                resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_3_content),
                StickyNotification.Defcon.NORMAL)
        addNote(noteTitle4,
                resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_4_content),
                StickyNotification.Defcon.USELESS)

        Screengrab.screenshot("home_full")

        onView(withId(R.id.notes_snlv))
                .perform(
                        actionOnItem<RecyclerView.ViewHolder>(withText(noteTitle1), swipeLeft()),
                        actionOnItem<RecyclerView.ViewHolder>(withText(noteTitle2), swipeLeft()),
                        actionOnItem<RecyclerView.ViewHolder>(withText(noteTitle3), swipeLeft()),
                        actionOnItem<RecyclerView.ViewHolder>(withText(noteTitle4), swipeLeft())
                )


        assert(true)
    }


}