package org.wikipedia.feed.categories;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.wikipedia.activity.SingleFragmentActivity;
import org.wikipedia.search.SearchResult;

import java.util.ArrayList;

public class CategoriesResultActivity extends SingleFragmentActivity<CategoriesResultFragment> {
    public static final String CATEGORY_TOPIC = "categoryTopic";
    public static final String CATEGORY_RESULT = "categoryResult";

    public static Intent newIntent(@NonNull Context context, @NonNull String categoryTopic, @NonNull ArrayList<SearchResult> categoryResult) {
        return new Intent(context, CategoriesResultActivity.class)
                .putExtra(CATEGORY_TOPIC, categoryTopic)
                .putParcelableArrayListExtra(CATEGORY_RESULT, categoryResult);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0f);
    }

    @Override
    protected CategoriesResultFragment createFragment() {
        return CategoriesResultFragment.newInstance();
    }
}
