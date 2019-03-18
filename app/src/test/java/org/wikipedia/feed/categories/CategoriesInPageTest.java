package org.wikipedia.feed.categories;

import com.google.gson.stream.MalformedJsonException;

import org.junit.Test;
import org.wikipedia.dataclient.mwapi.MwQueryPage;
import org.wikipedia.test.MockRetrofitTest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class CategoriesInPageTest extends MockRetrofitTest {
    private static final int BATCH_SIZE = 20;

    private Observable<List<MwQueryPage>> getObservable() {
        return getApiService().getCategoriesInPage("Jackie Chan", BATCH_SIZE)
                .map(response -> {
                    if (response != null && response.success() && response.query().pages() != null) {
                        return response.query().pages();
                    }
                    return new ArrayList<>();
                });
    }

    @Test
    public void testRequestSuccessNoContinuation() throws Throwable {
        enqueueFromFile("categories_in_page.json");
        TestObserver<List<MwQueryPage>> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertComplete()
                .assertNoErrors()
                .assertValue(result -> result.get(0).categories().get(0).title().equals("Category:1954 births"));
    }

    @Test
    public void testRequestResponseApiError() throws Throwable {
        enqueueFromFile("api_error.json");
        TestObserver<List<MwQueryPage>> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test
    public void testRequestResponseFailure() throws Throwable {
        enqueue404();
        TestObserver<List<MwQueryPage>> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(Exception.class);
    }

    @Test
    public void testRequestResponseMalformed() throws Throwable {
        server().enqueue("(╯°□°）╯︵ ┻━┻");
        TestObserver<List<MwQueryPage>> observer = new TestObserver<>();
        getObservable().subscribe(observer);

        observer.assertError(MalformedJsonException.class);
    }
}
