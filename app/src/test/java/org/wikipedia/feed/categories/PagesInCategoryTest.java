package org.wikipedia.feed.categories;

import com.google.gson.stream.MalformedJsonException;

import org.junit.Test;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.test.MockRetrofitTest;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import org.wikipedia.search.SearchResults;

public class PagesInCategoryTest extends MockRetrofitTest {
    private static final WikiSite TESTWIKI = new WikiSite("test.wikimedia.org");
    private static final int BATCH_SIZE = 20;

    private Observable<SearchResults> getObservable() {
        return getApiService().getPagesInCategory("Category:Physics", BATCH_SIZE)
                .map(response -> {
                    if (response != null && response.success() && response.query().pages() != null) {
                        return new SearchResults(response.query().pages(), TESTWIKI, response.continuation(), null);
                    }
                    return new SearchResults();
                });
    }

    @Test public void testRequestSuccessNoContinuation() throws Throwable {
        enqueueFromFile("pages_in_category.json");
        TestObserver<SearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete()
                .assertNoErrors()
                .assertValue(result -> result.getResults().get(0).getPageTitle().getDisplayText().equals("Physics"));
    }

    @Test public void testRequestSuccessWithContinuation() throws Throwable {
        enqueueFromFile("pages_in_category.json");
        TestObserver<SearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete().assertNoErrors()
                .assertValue(result -> result.getContinuation().get("continue").equals("gcmcontinue||")
                        && result.getContinuation().get("gcmcontinue")
                        .equals("page|313f312d4f4b4543043731294f042d2947292d394f59011a01dc19|51215300"));
    }

    @Test public void testRequestResponseApiError() throws Throwable {
        enqueueFromFile("api_error.json");
        TestObserver<SearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test public void testRequestResponseFailure() throws Throwable {
        enqueue404();
        TestObserver<SearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test public void testRequestResponseMalformed() throws Throwable {
        server().enqueue("'");
        TestObserver<SearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(MalformedJsonException.class);
    }

}
