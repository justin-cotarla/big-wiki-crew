package org.wikipedia.espresso.feed.categories;


import android.support.test.espresso.DataInteraction;
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
import org.wikipedia.WikipediaApp;
import org.wikipedia.feed.categories.CategoriesActivity;
import org.wikipedia.main.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_1000;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CategorySearchTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);
    private ActivityTestRule<CategoriesActivity> catergoriesActivityTestRule = new ActivityTestRule<>(CategoriesActivity.class, true, false);

    @Test
    public void categorySearchTest() {
        MainActivity activity = mainActivityTestRule.launchActivity(null);

        waitFor(WAIT_FOR_2000);

        catergoriesActivityTestRule.launchActivity(CategoriesActivity.newIntent(activity.getApplicationContext(), WikipediaApp.getInstance().getWikiSite()));

        waitFor(WAIT_FOR_2000);

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menu_feed_search), withContentDescription("Search Wikipedia"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        2),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                withId(R.id.search_src_text));
        searchAutoComplete.perform(replaceText("people"), closeSoftKeyboard());

        waitFor(WAIT_FOR_2000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.item_categories_result_title), withText("People"),
                        childAtPosition(
                                allOf(withId(R.id.categories_search_results_list),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("People")));

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.categories_search_results_list),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(0);
        appCompatTextView2.perform(click());

        waitFor(WAIT_FOR_2000);

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.item_categories_result_title), withText("Person"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.categories_results_list),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Person")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        waitFor(WAIT_FOR_1000);

        pressBack();
        pressBack();

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.top_categories), withText("Top Categories"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.categories_scroll_view),
                                        0),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Top Categories")));

        pressBack();

        waitFor(WAIT_FOR_1000);

        ViewInteraction imageView = onView(
                allOf(withId(R.id.single_fragment_toolbar_wordmark),
                        childAtPosition(
                                allOf(withId(R.id.hamburger_and_wordmark_layout),
                                        childAtPosition(
                                                withId(R.id.single_fragment_toolbar),
                                                0)),
                                1),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

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
