package org.wikipedia.espresso.page;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;
import org.wikipedia.main.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;

//@LargeTest
@RunWith(AndroidJUnit4.class)
//@SuppressWarnings("checkstyle:magicnumber")
public class ChatRoomTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void chatRoomTest() {
        // On main page, click search bar
        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.search_container),
                        childAtPosition(childAtPosition(withId(R.id.fragment_feed_feed), 0), 0),
                        isDisplayed())
        );
        linearLayout.perform(click());

        sleepDelay();

        // On search page, enter keyword "wikipedia"
        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        childAtPosition(allOf(withId(R.id.search_plate), childAtPosition(withId(R.id.search_edit_frame), 1)), 0),
                        isDisplayed())
        );
        searchAutoComplete.perform(replaceText("wikipedia"), closeSoftKeyboard());

        sleepDelay();

        // On search results page, click on first result
        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.search_results_list), childAtPosition(withId(R.id.search_results_container), 1)))
                .atPosition(0);
        constraintLayout.perform(click());

        sleepDelay();

        // On article page, click on chat room button
        ViewInteraction chatRoomButton = onView(
                allOf(withClassName(is("android.support.design.widget.FloatingActionButton")),
                        childAtPosition(withId(R.id.chat_room_layout), 0),
                        isDisplayed())
        );
        chatRoomButton.perform(click());
    }

    private void sleepDelay() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
