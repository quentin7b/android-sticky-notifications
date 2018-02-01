package com.github.quentin7b.sn

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import com.github.quentin7b.sn.database.model.StickyNotification

fun addNote(title: String, content: String, defcon: StickyNotification.Defcon) {
    onView(ViewMatchers.withId(R.id.main_fab))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

    onView(ViewMatchers.withId(R.id.note_title_et))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.typeText(title), ViewActions.closeSoftKeyboard())
    onView(ViewMatchers.withId(R.id.note_content_et))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.typeText(content), ViewActions.closeSoftKeyboard())

    onView(ViewMatchers.withId(R.id.level_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

    when (defcon) {
        StickyNotification.Defcon.USELESS -> onView(ViewMatchers.withId(R.id.useless_btn)).perform(click())
        StickyNotification.Defcon.NORMAL -> onView(ViewMatchers.withId(R.id.normal_btn)).perform(click())
        StickyNotification.Defcon.IMPORTANT -> onView(ViewMatchers.withId(R.id.important_btn)).perform(click())
        StickyNotification.Defcon.ULTRA -> onView(ViewMatchers.withId(R.id.ultra_btn)).perform(click())
        else -> pressBack()
    }

    onView(ViewMatchers.withId(R.id.fab))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

}