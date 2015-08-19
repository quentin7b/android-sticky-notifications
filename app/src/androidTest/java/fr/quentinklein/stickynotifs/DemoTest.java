package fr.quentinklein.stickynotifs;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.squareup.spoon.Spoon;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.quentinklein.stickynotifs.ui.activities.NotesListActivity_;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DemoTest {

    @Rule
    public ActivityTestRule<NotesListActivity_> mActivityRule = new ActivityTestRule<>(NotesListActivity_.class);


    @Test
    public void test_0() {
        try {
            onView(withText(mActivityRule.getActivity().getString(R.string.help_me)))
                    .check(matches(isDisplayed()))
                    .perform(click())
                    .check(matches(not(isDisplayed())));
        } catch (Exception e) {
            // DO nothing
        }
    }

    @Test
    public void test_1_EmptyNotifications() {
        Spoon.screenshot(mActivityRule.getActivity(), "Empty_Screen");
    }

    @Test
    public void test_2_AddNotification() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.note_title)).check(matches(isDisplayed()));
        Spoon.screenshot(mActivityRule.getActivity(), "Add_note_empty_screen");
        String[] normalNotification = mActivityRule.getActivity().getResources().getStringArray(R.array.normal_notification);
        addNote(normalNotification[0], normalNotification[1]);
        Spoon.screenshot(mActivityRule.getActivity(), "Add_note_filled_screen");
    }

    private void addNote(String title, String content) {
        onView(withId(R.id.note_title))
                .perform(click(), typeText(title));

        onView(withId(R.id.stick))
                .perform(click());

        onView(withId(R.id.content))
                .perform(click(), typeText(content));
    }
}
