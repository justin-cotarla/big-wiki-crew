package org.wikipedia.feed.categories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;



import org.wikipedia.R;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.page.PageActivity;
import org.wikipedia.page.PageTitle;
import org.wikipedia.search.SearchResult;
import org.wikipedia.views.ViewUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

import static org.wikipedia.feed.categories.CategoriesResultActivity.CATEGORY_RESULT;
import static org.wikipedia.feed.categories.CategoriesResultActivity.CATEGORY_TOPIC;

public class CategoriesResultFragment extends Fragment {
    private Unbinder unbinder;
    private String categoryTopic;
    private ArrayList<SearchResult> categoryResult;

    @BindView(R.id.categories_results_list) ListView categoryResultList;

    @NonNull
    public static CategoriesResultFragment newInstance(String categoryTopic) {
        CategoriesResultFragment instance = new CategoriesResultFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_TOPIC, categoryTopic);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_categories_result, container, false);
        unbinder = ButterKnife.bind(this, view);
        categoryTopic = requireActivity().getIntent().getStringExtra(CATEGORY_TOPIC);
        categoryResult = requireActivity().getIntent().getParcelableArrayListExtra(CATEGORY_RESULT);

        categoryResultList = view.findViewById(R.id.categories_results_list);
        CategoriesResultAdapter customerAdapter = new CategoriesResultAdapter();
        categoryResultList.setAdapter(customerAdapter);

        // TODO: category result title

        return view;
    }

    class CategoriesResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return categoryResult.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryResult.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_categories_result, null);
            TextView categoryTitle = (TextView) convertView.findViewById(R.id.item_categories_result_title);
            categoryTitle.setText(categoryResult.get(position).getPageTitle().getDisplayText());
            ViewUtil.loadImageUrlInto(convertView.findViewById(R.id.item_categories_result_image),
                    categoryResult.get(position).getPageTitle().getThumbUrl());
            return convertView;
        }
    }

    private BaseAdapter getAdapter() {
        return (BaseAdapter) categoryResultList.getAdapter();
    }

    @OnItemClick(R.id.categories_results_list) void onItemClick(ListView view, int position) {
        PageTitle item = ((SearchResult) getAdapter().getItem(position)).getPageTitle();
        HistoryEntry historyEntry = new HistoryEntry(item, HistoryEntry.SOURCE_CATEGORY);
        startActivity(PageActivity.newIntentForCurrentTab(requireContext(), historyEntry, historyEntry.getTitle()));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }
}
