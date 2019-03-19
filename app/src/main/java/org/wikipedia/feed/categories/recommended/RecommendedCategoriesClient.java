package org.wikipedia.feed.categories.recommended;

import android.support.annotation.NonNull;

import org.wikipedia.dataclient.ServiceFactory;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.dataclient.mwapi.MwQueryPage;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.page.bottomcontent.MainPageReadMoreTopicTask;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RecommendedCategoriesClient {

    public interface Delegate {
        void run(List<MwQueryPage.Category> categories);
    }

    private final int HISTORY_ENTRY_INDEX = 0;
    private final int CATEGORY_LIMIT = 5;

    private CompositeDisposable disposables = new CompositeDisposable();

    public void request(WikiSite wiki, Delegate callback) {

        disposables.add(Observable.fromCallable(new MainPageReadMoreTopicTask(HISTORY_ENTRY_INDEX))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entry -> getCategoriesForHistoryEntry(entry, wiki, callback)));
    }

    private void getCategoriesForHistoryEntry(@NonNull final HistoryEntry entry, WikiSite wiki,
                                        final Delegate callback) {

        disposables.add(ServiceFactory.get(wiki).getCategoriesInPage(entry.getTitle().toString(), CATEGORY_LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.success() && response.query().pages() != null) {
                         return response.query().pages().get(0).categories();
                    }
                    return Collections.emptyList();
                })
                .subscribe(results -> {
                    callback.run((List<MwQueryPage.Category>) results);
                }));
    }

}
