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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.waitFor;

@LargeTest
@SuppressWarnings("checkstyle:magicnumber")
@RunWith(AndroidJUnit4.class)
public class ViewArticleNotesTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void viewArticleNotesTest() {
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

        // On article page, click on view notes button
        ViewInteraction viewNotesButton = onView(
                allOf(withId(R.id.article_menu_view_notes),
                        childAtPosition(withClassName(is("org.wikipedia.page.PageActionTabLayout")), 5),
                        isDisplayed())
        );
        viewNotesButton.perform(click());

        // Check that Notes Viewer Dialog is shown
        ViewInteraction notesViewerDialog = onView(
          allOf(withId(R.id.dialog_notes_viewer),
                  isDisplayed())
        );
        notesViewerDialog.check(matches(isDisplayed()));
    }
}
