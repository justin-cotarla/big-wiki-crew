package org.wikipedia.main;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@SuppressWarnings("checkstyle:magicnumber")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnboardNewFeaturesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onBoardNewFeaturesTest() {

        waitFor(2000);

        onView(allOf(withId(R.id.drawer_icon_menu))).perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.main_drawer_features_container),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_drawer_view),
                                        0),
                                7),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.fragment_onboarding_skip_button), withText("Skip"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.fragment_onboarding_forward_button), withContentDescription("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        2),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        testFeatureTextDisplayed("Image search");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("Text-to-speech");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("Selective translation");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("No history");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("Categories");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("Discover");
        testNextFeatureSwipe();
        testFeatureTextDisplayed("Chat");

        ViewInteraction imageView2 = onView(allOf(withId(R.id.fragment_onboarding_done_button), isDisplayed()));
        imageView2.check(matches(isDisplayed()));
    }

    private void testFeatureTextDisplayed(String textToCheck) {
        ViewInteraction textView = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText(textToCheck),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText(textToCheck)));
    }

    private void testNextFeatureSwipe() {
        ViewInteraction rtlViewPager = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager.perform(swipeLeft());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
