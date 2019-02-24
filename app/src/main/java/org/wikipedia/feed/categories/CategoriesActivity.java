package org.wikipedia.feed.categories;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.wikipedia.activity.SingleFragmentActivity;
import org.wikipedia.dataclient.WikiSite;

public class CategoriesActivity extends SingleFragmentActivity<CategoriesFragment> {
    public static final String WIKISITE = "wikisite";

    public static Intent newIntent(@NonNull Context context, @NonNull WikiSite wikiSite) {
        return new Intent(context, CategoriesActivity.class)
                .putExtra(WIKISITE, wikiSite);
    }

    @Override
    protected CategoriesFragment createFragment() {
        return CategoriesFragment.newInstance(getIntent().getParcelableExtra(WIKISITE));
    }
}
