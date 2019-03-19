package org.wikipedia.feed.categories.recommended;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.feed.view.FeedAdapter;
import org.wikipedia.feed.view.HorizontalScrollingListCardItemView;
import org.wikipedia.feed.view.HorizontalScrollingListCardView;
import org.wikipedia.views.DefaultViewHolder;
import org.wikipedia.views.ItemTouchHelperSwipeAdapter;

import java.util.List;

public class RecommendedCategoriesListCardView extends HorizontalScrollingListCardView<RecommendedCategoriesListCard>
        implements ItemTouchHelperSwipeAdapter.SwipeableView {
    public interface Callback {
        void onRecommendedCategorySelect(@NonNull RecommendedCategoriesItemCard card, @NonNull HorizontalScrollingListCardItemView view);
    }

    private Callback callback;

    public RecommendedCategoriesListCardView(@NonNull Context context, Callback callback) {
        super(context);
        setAllowOverflow(true);
        this.callback = callback;
    }

    @Override
    public void setCard(@NonNull RecommendedCategoriesListCard card) {
        super.setCard(card);
        header();
        set(new RecyclerAdapter(card.items()));
        setLayoutDirectionByWikiSite(card.wikiSite(), getLayoutDirectionView());
    }

    private void header() {
        headerView().setVisibility(GONE);
    }

    private class RecyclerAdapter extends HorizontalScrollingListCardView.RecyclerAdapter<RecommendedCategoriesItemCard> {
        RecyclerAdapter(@NonNull List<RecommendedCategoriesItemCard> items) {
            super(items);
        }

        @Nullable
        @Override
        protected FeedAdapter.Callback callback() {
            return getCallback();
        }

        @Override
        public void onBindViewHolder(@NonNull DefaultViewHolder<HorizontalScrollingListCardItemView> holder, int i) {
            final RecommendedCategoriesItemCard card = item(i);
            holder.getView().setText(card.text());
            holder.getView().setImage(card.image());
            holder.getView().setOnClickListener((view) -> {
                if (callback != null) {
                    callback.onRecommendedCategorySelect(card, (HorizontalScrollingListCardItemView) view);
                }
            });
        }
    }
}