package org.wikipedia.feed.categories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.wikipedia.R;
import org.wikipedia.dataclient.ServiceFactory;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.dataclient.mwapi.MwQueryPage;
import org.wikipedia.history.SearchActionModeCallback;
import org.wikipedia.search.SearchResult;
import org.wikipedia.search.SearchResults;
import org.wikipedia.views.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static org.wikipedia.feed.categories.CategoriesActivity.WIKISITE;

public class CategoriesFragment extends Fragment {
    private static final int BATCH_SIZE = 20;

    private Unbinder unbinder;
    private WikiSite wiki;
    private CompositeDisposable disposables = new CompositeDisposable();
    private CategoriesFragment.SearchCallback searchActionModeCallback = new SearchCallback();

    @BindView(R.id.categories_toolbar) Toolbar categoriesToolbar;
    @BindView(R.id.categories_scroll_view) ScrollView container;
    @BindView(R.id.categories_search_results_list) ListView searchResultsList;

    private List<String> searchResults = new ArrayList<>();

    private boolean searching = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        SearchResultAdapter adapter = new SearchResultAdapter(inflater);
        searchResultsList.setAdapter(adapter);
        searchResultsList.setOnItemClickListener((parent, view1, position, id) -> {
            searchOnCategory(searchResults.get(position));
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && searching) {
                searching = false;
                this.container.setVisibility(View.VISIBLE);
                searchResultsList.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_feed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feed_search:
                getAppCompatActivity().startSupportActionMode(searchActionModeCallback);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) getActivity();
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

    private BaseAdapter getAdapter() {
        return (BaseAdapter) searchResultsList.getAdapter();
    }

    private class SearchCallback extends SearchActionModeCallback {
        @Nullable private ActionMode actionMode;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            ViewUtil.finishActionModeWhenTappingOnView(getView(), actionMode);
            ViewUtil.finishActionModeWhenTappingOnView(container, actionMode);

            return super.onCreateActionMode(mode, menu);
        }

        @Override
        protected void onQueryChange(String s) {
            if (s.length() == 0) {
                searching = false;
                container.setVisibility(View.VISIBLE);
                searchResultsList.setVisibility(View.GONE);
                return;
            }
            disposables.add(ServiceFactory.get(wiki).searchForCategory(s, BATCH_SIZE, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(response -> {
                        if (response != null && response.success() && response.query().categories() != null) {
                            return new CategoriesSearchResults(response.query().categories(), response.continuation());
                        }
                        return new CategoriesSearchResults();
                    })
                    .subscribe(results -> {
                        searching = true;
                        container.setVisibility(View.GONE);
                        searchResultsList.setVisibility(View.VISIBLE);
                        searchResults = results.getResults();
                        getAdapter().notifyDataSetChanged();
                    }, caught -> {
                        Toast.makeText(requireActivity(), caught.getMessage(), Toast.LENGTH_LONG).show();
                    })
            );
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            super.onDestroyActionMode(mode);
            actionMode = null;
        }

        @Override
        protected String getSearchHintString() {
            return getString(R.string.search_hint_categories);
        }

        @Override
        protected boolean finishActionModeIfKeyboardHiding() {
            return true;
        }
    }


    private final class SearchResultAdapter extends BaseAdapter {
        private final LayoutInflater inflater;

        SearchResultAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public Object getItem(int position) {
            return searchResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_category_search_result, parent, false);
            }
            String categoryTitle = (String) getItem(position);

            TextView categorySearchResultTitle = convertView.findViewById(R.id.item_categories_result_title);
            categorySearchResultTitle.setText(categoryTitle);

            return convertView;
        }
    }
}
