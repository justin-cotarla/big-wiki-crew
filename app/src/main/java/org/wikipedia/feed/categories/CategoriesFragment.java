package org.wikipedia.feed.categories;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.wikipedia.R;
import org.wikipedia.dataclient.ServiceFactory;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.dataclient.mwapi.MwQueryPage;
import org.wikipedia.feed.categories.recommended.RecommendedCategoriesArrayAdapter;
import org.wikipedia.feed.categories.recommended.RecommendedCategoriesClient;
import org.wikipedia.feed.categories.result.CategoriesResultActivity;
import org.wikipedia.search.SearchResult;
import org.wikipedia.search.SearchResults;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static org.wikipedia.feed.categories.CategoriesActivity.WIKISITE;

public class CategoriesFragment extends Fragment {
    private static final int BATCH_SIZE = 20;
    private Unbinder unbinder;
    private WikiSite wiki;
    private CompositeDisposable disposables = new CompositeDisposable();

    @BindView(R.id.recommended_categories_list)
    ListView recommendedCategoriesListView;
    @BindView(R.id.recommended_categories_title)
    TextView recommendedCategoriesTitle;


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

        populateRecommendedCategories(getContext());

        if (recommendedCategoriesTitle.getVisibility() != GONE && recommendedCategoriesListView.getVisibility() != GONE) {
            recommendedCategoriesListView.setOnItemClickListener((parent, view1, position, id) -> {
                MwQueryPage.Category item = (MwQueryPage.Category) parent.getItemAtPosition(position);
                String name = item.title();
                searchOnCategory(name.substring(name.indexOf(':') + 1));
            });
        }

        GridLayout topCategories = view.findViewById(R.id.categories_grid_layout);
        setupCategoriesGrid(topCategories);

        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        disposables.clear();
        super.onDestroyView();
    }

    private void setupCategoriesGrid(GridLayout grid) {
        for (int i = 0; i < grid.getChildCount(); i++) {
            CardView card = (CardView) grid.getChildAt(i);
            card.setOnClickListener(v -> {
                TextView category = v.findViewWithTag(getString(R.string.category_card_text));
                searchOnCategory(category.getText().toString());
            });
        }
    }

    private Boolean isAPortalCategory(String title) {
        return title.contains("Portal");
    }

    private void populateRecommendedCategories(Context context) {
        RecommendedCategoriesClient recommendedCategoriesClient = new RecommendedCategoriesClient();
        recommendedCategoriesClient.request(wiki, pageTitles -> {
            if (pageTitles.size() > 0) {
                RecommendedCategoriesArrayAdapter categoriesAdapter = new RecommendedCategoriesArrayAdapter(context, pageTitles);
                recommendedCategoriesListView.setAdapter(categoriesAdapter);
            }
            else {
                recommendedCategoriesListView.setVisibility(GONE);
                recommendedCategoriesTitle.setVisibility(GONE);
            }
        });
    }

    private void searchOnCategory(String category) {
        disposables.add(ServiceFactory.get(wiki).getPagesInCategory("Category:" + category.replace(" ", "_"), BATCH_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.success() && response.query().pages() != null) {
                        List<MwQueryPage> pages = response.query().pages();
                        pages.removeIf(page -> isAPortalCategory(page.title()));
                        return new SearchResults(pages, wiki, response.continuation(), null);
                    }
                    return new SearchResults();
                })
                .subscribe(results -> {
                    ArrayList<SearchResult> categoryResult = (ArrayList<SearchResult>)results.getResults();
                    startActivity(CategoriesResultActivity.newIntent(requireContext(), category, categoryResult));
                }, caught -> {
                    Toast.makeText(requireActivity(), caught.getMessage(), Toast.LENGTH_LONG).show();
                }));
    }
}
