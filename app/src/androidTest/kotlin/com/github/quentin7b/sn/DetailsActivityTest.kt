package com.github.quentin7b.sn

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.ActivityResultMatchers.hasResultCode
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.app.AppCompatActivity
import com.github.quentin7b.sn.ui.DetailsActivity
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
    fun testTitleHintIsDisplayed() {
        // https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
        // onView(withId(R.id.note_title_et)).check(matches(withHint(R.string.hint_title)))
    }

    @Test
    fun testContentIsDisplayed() {
        onView(withId(R.id.note_content_et)).check(matches(isDisplayed()))
    }

    @Test
    fun testContentHintIsDisplayed() {
        // https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
        // onView(withId(R.id.note_content_et)).check(matches(withHint(R.string.hint_details)))
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
    fun testDefconLevelWorks() {
        onView(withId(R.id.level_btn))
                .perform(click())
        onView(withId(R.id.useless_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.normal_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.important_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.ultra_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun testSaveBtnIsDisplayed() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun testTitleEmptyError() {
        // https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
        /*
        onView(withId(R.id.note_title_et)).perform(clearText(), typeText(""))
        onView(withId(R.id.fab))
                .perform(click())

        val resources = InstrumentationRegistry.getContext().resources
        val titleEmptyError = resources.getString(R.string.error_title_empty)

        onView(withId(R.id.note_title_et)).check(matches(hasErrorText(titleEmptyError)))

        */
    }

    @Test
    fun testNoteOkIntent() {
        onView(withId(R.id.note_title_et)).perform(clearText(), typeText("Test Notification"))
        onView(withId(R.id.fab)).perform(click())

        assertThat(activity.activityResult, hasResultCode(AppCompatActivity.RESULT_OK))
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