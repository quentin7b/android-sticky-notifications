package com.github.quentin7b.sn

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.quentin7b.sn.ui.DetailsActivity
import com.github.quentin7b.sn.ui.MainActivity
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
    fun testToolbarIsDisplayed() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerIsDisplayed() {
        onView(withId(R.id.notes_snlv)).check(matches(isDisplayed()))
    }

    @Test
    fun testFabIsDisplayed() {
        onView(withId(R.id.main_fab)).check(matches(isDisplayed()))
    }

    @Test
    fun testStartDetailActivity() {
        Intents.init()

        onView(withId(R.id.main_fab))
                .check(matches(isDisplayed()))
                .perform(click())

        intended(allOf(
                hasComponent(DetailsActivity::class.java.name),
                hasExtras(allOf(
                        hasEntry(equalTo(DetailsActivity.EXTRA_TRANSITION), equalTo(true))
                ))
        ))

        Intents.release()
    }

    /*
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
    */


}