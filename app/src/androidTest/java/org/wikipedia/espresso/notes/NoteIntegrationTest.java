package org.wikipedia.espresso.notes;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.web.assertion.WebAssertion;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.AllOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;
import org.wikipedia.main.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.wikipedia.espresso.page.ChatRoomTest.atPosition;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_1000;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoteIntegrationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void noteConsistencyTest() {
        // On main page, click search bar
        ViewInteraction linearLayout = onView(
                AllOf.allOf(withId(R.id.search_container),
                        childAtPosition(childAtPosition(withId(R.id.fragment_feed_feed), 0), 0),
                        isDisplayed())
        );
        linearLayout.perform(click());

        waitFor(WAIT_FOR_2000);

        // On search page, enter keyword "wikipedia"
        ViewInteraction searchAutoComplete = onView(
                AllOf.allOf(withId(R.id.search_src_text),
                        childAtPosition(AllOf.allOf(withId(R.id.search_plate), childAtPosition(withId(R.id.search_edit_frame), 1)), 0),
                        isDisplayed())
        );
        searchAutoComplete.perform(replaceText("wikipedia"), closeSoftKeyboard());

        waitFor(WAIT_FOR_2000);

        // On search results page, click on first result
        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(AllOf.allOf(withId(R.id.search_results_list), childAtPosition(withId(R.id.search_results_container), 1)))
                .atPosition(0);
        constraintLayout.perform(click());

        waitFor(WAIT_FOR_2000);

        ViewInteraction observableWebView = onView(
                allOf(withId(R.id.page_web_view),
                        childAtPosition(
                                allOf(withId(R.id.page_contents_container),
                                        childAtPosition(
                                                withId(R.id.page_fragment),
                                                0)),
                                0),
                        isDisplayed()));
        observableWebView.perform(longClick());

        waitFor(WAIT_FOR_1000);

        ViewInteraction imageButton = onView(
                AllOf.allOf(withContentDescription("More options"))).inRoot(isPlatformPopup());
        imageButton.perform(click());

        ViewInteraction imageButton2 = onView(
                AllOf.allOf(withContentDescription("Save note"), isDisplayed())).inRoot(isPlatformPopup());
        imageButton2.check(matches(isDisplayed()));
        imageButton2.perform(click());

        waitFor(WAIT_FOR_1000);

        // On article page, click on view notes button
        ViewInteraction viewNotesButton = onView(
                AllOf.allOf(withId(R.id.article_menu_view_notes),
                        childAtPosition(withClassName(is("org.wikipedia.page.PageActionTabLayout")), 5),
                        isDisplayed())
        );
        viewNotesButton.perform(click());

        // Check that note is shown
        onView(withId(R.id.dialog_notes_list))
                .check(matches(atPosition(0, hasDescendant(withText("that")))));


        pressBack();

        pressBack();

        pressBack();

        pressBack();

        waitFor(WAIT_FOR_1000);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withContentDescription("Saved"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment_main_nav_tab_layout),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction tabView = onView(
                allOf(withContentDescription("Notes"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.saved_tab_layout),
                                        0),
                                1),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction noteView = onView(withId(R.id.notes_list_list))
                .check(matches(atPosition(0, hasDescendant(withText("that")))));
        noteView.perform(click());

        ViewInteraction viewArticleButtonView = onView(withId(R.id.notes_item_redirect));
        viewArticleButtonView.perform(click());
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
