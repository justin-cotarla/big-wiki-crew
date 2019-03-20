package org.wikipedia.feed.categories.result;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.dataclient.mwapi.MwQueryCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriesSearchResults {
    @NonNull private List<String> results;
    @Nullable private Map<String, String> continuation;

    public CategoriesSearchResults() {
        this.results = new ArrayList<>();
        this.continuation = null;
    }

    public CategoriesSearchResults(
            @NonNull List<MwQueryCategory> categories,
            @Nullable Map<String, String> continuation
            ) {
        List<String> catogryResults = new ArrayList<>();
        for (MwQueryCategory category : categories) {
            catogryResults.add(category.category());
        }
        this.results = catogryResults;
        this.continuation = continuation;
    }

    @NonNull public List<String> getResults() {
        return this.results;
    }

    @Nullable public Map<String, String> getContinuation() {
        return this.continuation;
    }
}
