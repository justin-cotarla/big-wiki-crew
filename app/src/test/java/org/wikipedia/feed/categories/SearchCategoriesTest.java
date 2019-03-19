package org.wikipedia.feed.categories;

import com.google.gson.stream.MalformedJsonException;

import org.junit.Test;
import org.wikipedia.feed.categories.result.CategoriesSearchResults;
import org.wikipedia.test.MockRetrofitTest;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class SearchCategoriesTest extends MockRetrofitTest {
    private static final int BATCH_SIZE = 20;

    private Observable<CategoriesSearchResults> getObservable() {
        return getApiService().searchForCategory("Marvel", BATCH_SIZE, 1)
                .map(response -> {
                    if (response != null && response.success() && response.query().categories() != null) {
                        return new CategoriesSearchResults(response.query().categories(), response.continuation());
                    }
                    return new CategoriesSearchResults();
                });
    }

    @Test
    public void testRequestSuccessNoContinuation() throws Throwable {
        enqueueFromFile("search_on_categories.json");
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete()
                .assertNoErrors()
                .assertValue(result -> result.getResults().get(0).equals("Marvel-themed areas at Disney parks"));
    }

    @Test
    public void testRequestSuccessWithContinuation() throws Throwable {
        enqueueFromFile("search_on_categories.json");
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete()
                .assertNoErrors()
                .assertValue(result -> result.getContinuation().get("continue").equals("-||")
                    && result.getContinuation().get("accontinue").equals("Marvel_Cinematic_Universe_character_lists"));
    }

    @Test
    public void testRequestSuccessNoResults() throws Throwable {
        enqueueFromFile("search_on_categories_empty.json");
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete()
                .assertNoErrors()
                .assertValue(result -> result.getResults().isEmpty());
    }

    @Test
    public void testRequestResponseApiError() throws Throwable {
        enqueueFromFile("api_error.json");
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test
    public void testRequestResponseFailure() throws Throwable {
        enqueue404();
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test
    public void testRequestResponseMalformed() throws Throwable {
        server().enqueue("(╯°□°）╯︵ ┻━┻");
        TestObserver<CategoriesSearchResults> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(MalformedJsonException.class);
    }
}
