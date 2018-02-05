package com.github.quentin7b.sn

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.github.quentin7b.sn.ui.AboutActivity
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.locale.LocaleTestRule


@RunWith(AndroidJUnit4::class)
class AboutActivityTest {

    companion object {
        @ClassRule
        @JvmField
        val localeTestRule = LocaleTestRule()
    }

    @Rule
    @JvmField
    val activity = ActivityTestRule<AboutActivity>(AboutActivity::class.java)

    @Test
    fun testToolbarIsDisplayed() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }


    @Test
    fun testAboutSectionIsDisplayed() {
        onView(withId(R.id.about_tv)).check(matches(isDisplayed()))
    }

    @Test
    fun testVersionIsDisplayed() {
        onView(withId(R.id.versionTV)).check(matches(isDisplayed()))
    }

    @Test
    fun testAboutIsDisplayed() {
        onView(withId(R.id.about_licenses_tv)).check(matches(isDisplayed()))
    }
}