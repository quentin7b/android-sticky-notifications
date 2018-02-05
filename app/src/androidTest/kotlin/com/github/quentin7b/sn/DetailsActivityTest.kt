package com.github.quentin7b.sn

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.quentin7b.sn.ui.DetailsActivity
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule


@RunWith(AndroidJUnit4::class)
class DetailsActivityTest {

    companion object {
        @ClassRule
        @JvmField
        val localeTestRule = LocaleTestRule()
    }

    @Rule
    @JvmField
    val activity = ActivityTestRule<DetailsActivity>(DetailsActivity::class.java)

    @Test
    fun testTitleIsDisplayed() {
        onView(withId(R.id.note_title_et)).check(matches(isDisplayed()))
    }

    @Test
    fun testContentIsDisplayed() {
        onView(withId(R.id.note_content_et)).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckboxIsDisplayed() {
        onView(withId(R.id.notification_cb)).check(matches(isDisplayed()))
    }

    @Test
    fun testDefconLevelIsDisplayed() {
        onView(withId(R.id.level_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun testSaveBtnIsDisplayed() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    /*
    @Test
    fun testFull() {
        val resources = InstrumentationRegistry.getContext().resources

        onView(withId(R.id.note_title_et)).perform(ViewActions.typeText(resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_1_title)), closeSoftKeyboard())
        onView(withId(R.id.note_content_et)).perform(ViewActions.typeText(resources.getString(fr.quentinklein.stickynotifs.debug.test.R.string.note_1_content)), closeSoftKeyboard())
        onView(withId(R.id.notification_cb)).perform(click())
        onView(withId(R.id.level_btn)).perform(click())
        onView(withId(R.id.normal_btn)).check(matches(isDisplayed())).perform(click())

        Screengrab.screenshot("details_full")

        assert(true)
    }
    */

}