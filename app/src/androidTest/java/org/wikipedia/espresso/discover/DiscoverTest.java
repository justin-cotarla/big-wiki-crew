package org.wikipedia.espresso.discover;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;
import org.wikipedia.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.viewIsDisplayed;
import static org.wikipedia.espresso.util.ViewTools.waitFor;
import static org.wikipedia.espresso.util.ViewTools.whileWithMaxSteps;

@SuppressWarnings("checkstyle:magicnumber")
@RunWith(AndroidJUnit4.class)
public class DiscoverTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testScrollToDiscoverCard() throws Exception {
        // Wait until the feed is displayed
        whileWithMaxSteps(
                () -> !viewIsDisplayed(R.id.fragment_feed_feed),
                () -> waitFor(WAIT_FOR_2000));

        testDiscoverCardInFeed();
        waitFor(2000);
        testDiscoverButtonsDisplayed();
        waitFor(2000);
        testDiscoverButtons();
    }

    private void testDiscoverCardInFeed() {
        waitFor(2000);

        whileWithMaxSteps(
                () -> !discoverCardIsDisplayed(),
                () -> {
                    onView(withId(R.id.fragment_feed_feed)).perform(ViewActions.swipeUp());
                    waitFor(1000);
                },
                10
        );
        waitFor(2000);
        onView(withClassName(endsWith("RandomCardView"))).perform(click());
    }

    private boolean discoverCardIsDisplayed() {
        final boolean[] isDisplayed = {true};

        onView(withClassName(endsWith("RandomCardView")))
                .withFailureHandler((Throwable error, Matcher<View> viewMatcher) -> isDisplayed[0] = false)
                .check(matches(isDisplayed()));

        return isDisplayed[0];
    }

    private void testDiscoverButtonsDisplayed() {
        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.spinner_layout),
                        childAtPosition(
                                allOf(withId(R.id.random_coordinator_layout),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.random_back_button),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.random_next_button), withContentDescription("Load another article"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.random_save_button), withContentDescription("Add to reading list"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                2),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));

    }
    
    private void testDiscoverButtons () {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.random_next_button), withContentDescription("Load another article"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction viewPagerWithVelocity = onView(
                allOf(withId(R.id.random_item_pager),
                        childAtPosition(
                                allOf(withId(R.id.random_coordinator_layout),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                1),
                        isDisplayed()));
        viewPagerWithVelocity.perform(swipeLeft());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.random_back_button),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction viewPagerWithVelocity2 = onView(
                allOf(withId(R.id.random_item_pager),
                        childAtPosition(
                                allOf(withId(R.id.random_coordinator_layout),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                1),
                        isDisplayed()));
        viewPagerWithVelocity2.perform(swipeRight());

    }
}