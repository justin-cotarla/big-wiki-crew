package org.wikipedia.espresso.page;

import android.support.annotation.NonNull;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@LargeTest
@RunWith(AndroidJUnit4.class)
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

        waitFor(WAIT_FOR_2000);

        // On search page, enter keyword "wikipedia"
        ViewInteraction searchAutoComplete = onView(
                allOf(withId(R.id.search_src_text),
                        childAtPosition(allOf(withId(R.id.search_plate), childAtPosition(withId(R.id.search_edit_frame), 1)), 0),
                        isDisplayed())
        );
        searchAutoComplete.perform(replaceText("wikipedia"), closeSoftKeyboard());

        waitFor(WAIT_FOR_2000);

        // On search results page, click on first result
        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.search_results_list), childAtPosition(withId(R.id.search_results_container), 1)))
                .atPosition(0);
        constraintLayout.perform(click());

        waitFor(WAIT_FOR_2000);

        // On article page, click on chat room button
        ViewInteraction chatRoomButton = onView(
                allOf(withClassName(is("android.support.design.widget.FloatingActionButton")),
                        childAtPosition(withId(R.id.chat_room_layout), 0),
                        isDisplayed())
        );
        chatRoomButton.perform(click());

        waitFor(WAIT_FOR_2000);

        // Check if first message is "hello world"
        onView(withId(R.id.chat_message_view))
                .check(matches(atPosition(0, hasDescendant(withText("hello world")))));

        // Type in randomized test string in chat box
        String testString = "test " + (int) (Math.random() * 100);
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.chat_text_input),
                        childAtPosition(
                                allOf(withId(R.id.chat_text_input_layout),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                3)),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(testString), closeSoftKeyboard());

        // Click send button
        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.chat_send_btn),
                        childAtPosition(
                                allOf(withId(R.id.chat_text_input_layout),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                3)),
                                1),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        // Check that sent message appears
        onView(withId(R.id.chat_message_sent_text))
                .check(matches(withText(testString)));

        // Close chat dialog
        ViewInteraction appCompatImageButton5 = onView(
                allOf(withId(R.id.chat_close_btn),
                        childAtPosition(
                                allOf(withId(R.id.chat_title_layout),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        // Check that current view is PageActivity
        ViewInteraction imageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.page_toolbar),
                                        childAtPosition(
                                                withId(R.id.page_toolbar_container),
                                                0)),
                                0),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
