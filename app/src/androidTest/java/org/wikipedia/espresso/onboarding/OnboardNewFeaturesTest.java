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
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnboardNewFeaturesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void onBoardNewFeaturesTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        ViewInteraction textView = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Image search"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Image search")));

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

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.fragment_onboarding_forward_button), withContentDescription("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());

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

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Text-to-speech"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0)));
        textView3.check(matches(withText("Text-to-speech")));

        ViewInteraction rtlViewPager2 = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager2.perform(swipeLeft());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Selective translation"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0)));
        textView4.check(matches(withText("Selective translation")));

        ViewInteraction rtlViewPager3 = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager3.perform(swipeLeft());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("No history"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0)));
        textView5.check(matches(withText("No history")));

        ViewInteraction rtlViewPager4 = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager4.perform(swipeLeft());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Categories"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0)));
        textView6.check(matches(withText("Categories")));

        ViewInteraction rtlViewPager5 = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager5.perform(swipeLeft());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Discover"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0)));
        textView7.check(matches(withText("Discover")));

        ViewInteraction rtlViewPager6 = onView(
                allOf(withId(R.id.fragment_pager),
                        childAtPosition(
                                allOf(withId(R.id.fragment_onboarding_pager_container),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                0),
                        isDisplayed()));
        rtlViewPager6.perform(swipeLeft());

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.view_onboarding_page_primary_text), withText("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        textView8.check(matches(withText("Chat")));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.fragment_onboarding_done_button),isDisplayed()));
        imageView2.check(matches(isDisplayed()));
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
