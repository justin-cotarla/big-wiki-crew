package org.wikipedia.feed.categories.recommended;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.json.annotations.Required;
import org.wikipedia.page.PageTitle;

public final class RecommendedCategoriesItem {
    @SuppressWarnings("unused,NullableProblems") @Required @NonNull private String name;
    @Required @NonNull private PageTitle pageTitle;

    public RecommendedCategoriesItem(String name, PageTitle pageTitle) {
        this.name = name;
        this.pageTitle = pageTitle;
    }

    @NonNull public String name() {
        return name;
    }

    @Nullable public Uri thumb() {
        return Uri.parse(pageTitle.getThumbUrl());
    }
}
