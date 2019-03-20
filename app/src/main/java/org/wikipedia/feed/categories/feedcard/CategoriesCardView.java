package org.wikipedia.feed.categories.feedcard;

import android.content.Context;
import android.view.View;

import org.wikipedia.R;
import org.wikipedia.feed.view.StaticCardView;

import io.reactivex.annotations.NonNull;

public class CategoriesCardView extends StaticCardView<CategoriesCard> {

    public interface Callback {
        void onCategoriesClick(@NonNull CategoriesCardView view);
    }

    public CategoriesCardView(Context context) {
        super(context);
    }

    @Override public void setCard(@NonNull CategoriesCard card) {
        super.setCard(card);
        setTitle(getString(R.string.view_categories_card_title));
        setSubtitle(getString(R.string.view_categories_card_subtitle));
        setIcon(R.drawable.ic_category_24dp);
        setContainerBackground(R.color.orange700);
        setAction(R.drawable.ic_arrow_forward_black_24dp, R.string.view_categories_card_action);
    }

    protected void onContentClick(View v) {
        if (getCallback() != null) {
            getCallback().onCategoriesClick(CategoriesCardView.this);
        }
    }

    protected void onActionClick(View v) {
        if (getCallback() != null) {
            getCallback().onCategoriesClick(CategoriesCardView.this);
        }
    }
}
