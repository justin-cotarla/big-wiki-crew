package org.wikipedia.espresso.categories;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;
import org.wikipedia.feed.categories.CategoriesActivity;

import static org.hamcrest.core.AllOf.allOf;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_1000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.viewIsDisplayed;
import static org.wikipedia.espresso.util.ViewTools.waitFor;
import static org.wikipedia.espresso.util.ViewTools.whileWithMaxSteps;

@RunWith(AndroidJUnit4.class)
public final class CategoriesActivityTest {

    @Rule
    public ActivityTestRule<CategoriesActivity> activityTestRule = new ActivityTestRule<>(CategoriesActivity.class);

    @Test
    public void testTopCategories() {
        whileWithMaxSteps(
                () -> !viewIsDisplayed(R.id.categories_scroll_view),
                () -> waitFor(WAIT_FOR_1000));

        onView(
                allOf(withId(R.id.top_categories), isDisplayed())
        ).check(matches(withText("Top Categories")));

        testCards();
    }

    private void testCards() {
        final String[] categories = {
                "Culture",
                "Geography",
                "Health",
                "History",
                "Human activities",
                "Mathematics",
                "Nature",
                "People",
                "Philosophy",
                "Religion",
                "Society",
                "Technology"
        };

        int index = 0;
        for (String category : categories) {
            onView(
                    childAtPosition(
                            childAtPosition(
                                    childAtPosition(
                                            withId(R.id.categories_grid_layout),
                                            index++
                                    ),
                                    0
                            ),
                            1
                    )).check(matches(withText(category)));
        }
    }
}
