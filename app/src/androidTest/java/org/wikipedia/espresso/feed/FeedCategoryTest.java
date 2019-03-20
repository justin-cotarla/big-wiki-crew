package org.wikipedia.espresso.feed;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wikipedia.R;
import org.wikipedia.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.wikipedia.espresso.util.ViewTools.WAIT_FOR_2000;
import static org.wikipedia.espresso.util.ViewTools.childAtPosition;
import static org.wikipedia.espresso.util.ViewTools.viewIsDisplayed;
import static org.wikipedia.espresso.util.ViewTools.waitFor;
import static org.wikipedia.espresso.util.ViewTools.whileWithMaxSteps;

@SuppressWarnings("checkstyle:magicnumber")
@RunWith(AndroidJUnit4.class)
public class FeedCategoryTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testScrollToCategoriesCard() throws Exception {
        // Wait until the feed is displayed
        waitFor(WAIT_FOR_2000);

        testCategoriesCardInFeed();

        waitFor(WAIT_FOR_2000);

        // Wait until the top categories are displayed
        whileWithMaxSteps(
                () -> !viewIsDisplayed(R.id.categories_scroll_view),
                () -> waitFor(WAIT_FOR_2000));

        testTopCardsExist();
        waitFor(WAIT_FOR_2000);

        testBrowseOnCategory();
    }

    private void testCategoriesCardInFeed() {
        onView(withId(R.id.fragment_feed_feed)).perform(RecyclerViewActions.scrollTo(
                withClassName(endsWith("CategoriesCardView"))
        ));
        onView(withClassName(endsWith("CategoriesCardView"))).perform(click());
    }

    private void testTopCardsExist() {
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

    private void testBrowseOnCategory() {
        onView(
                childAtPosition(
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.categories_grid_layout),
                                        0
                                ),
                                0
                        ),
                        1
                )).perform(click());

        waitFor(2000);

        onView(childAtPosition(
                childAtPosition(
                        withId(R.id.action_bar_container),
                        0
                ),
                0
        )).check(matches(withText("Culture")));

        onView(withId(R.id.decor_content_parent)).check(matches(isDisplayed()));
    }

    private boolean categoryCardIsDisplayed() {
        final boolean[] isDisplayed = {true};

        onView(withClassName(endsWith("CategoriesCardView")))
                .withFailureHandler((Throwable error, Matcher<View> viewMatcher) -> isDisplayed[0] = false)
                .check(matches(isDisplayed()));

        return isDisplayed[0];
    }
}
