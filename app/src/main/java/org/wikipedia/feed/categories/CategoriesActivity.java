package org.wikipedia.feed.categories;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0f);
    }

    @Override
    protected CategoriesFragment createFragment() {
        return CategoriesFragment.newInstance(getIntent().getParcelableExtra(WIKISITE));
    }
}
