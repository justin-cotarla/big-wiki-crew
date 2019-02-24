package org.wikipedia.feed.categories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wikipedia.R;
import org.wikipedia.dataclient.WikiSite;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.wikipedia.feed.categories.CategoriesActivity.WIKISITE;

public class CategoriesFragment extends Fragment {
    private Unbinder unbinder;
    private WikiSite wiki;

    @NonNull
    public static CategoriesFragment newInstance(WikiSite wikiSite) {
        CategoriesFragment instance = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putParcelable(WIKISITE, wikiSite);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, view);
        wiki = requireActivity().getIntent().getParcelableExtra(WIKISITE);

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }
}
