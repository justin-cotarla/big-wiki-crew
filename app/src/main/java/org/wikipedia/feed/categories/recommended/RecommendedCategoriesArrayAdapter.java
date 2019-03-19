package org.wikipedia.feed.categories.recommended;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.wikipedia.R;
import org.wikipedia.dataclient.mwapi.MwQueryPage;

import java.util.List;

public class RecommendedCategoriesArrayAdapter extends ArrayAdapter<MwQueryPage.Category> {

    private List<MwQueryPage.Category> categories;
    private LayoutInflater inflater;

    public RecommendedCategoriesArrayAdapter(Context context, List<MwQueryPage.Category> categories) {
        super(context, R.layout.recommended_categories_row, categories);
        this.categories = categories;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.recommended_categories_row, parent, false);
        TextView title = view.findViewById(R.id.recommended_row_title);
        String name = categories.get(position).title();
        title.setText(name.substring(name.indexOf(':') + 1));
        return view;
    }
}
