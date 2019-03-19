package org.wikipedia.feed.categories.recommended;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.feed.model.CardType;
import org.wikipedia.feed.model.ListCard;

import java.util.ArrayList;
import java.util.List;

public class RecommendedCategoriesListCard extends ListCard<RecommendedCategoriesItemCard> {

    public RecommendedCategoriesListCard(@NonNull List<RecommendedCategoriesItem> categories, int age, @NonNull WikiSite wiki) {
        super(toItemCards(categories, wiki), wiki);
    }

    @NonNull @Override public CardType type() {
        return CardType.RECOMMENDED_CATEGORIES;
    }

    @NonNull @VisibleForTesting static List<RecommendedCategoriesItemCard> toItemCards(@NonNull List<RecommendedCategoriesItem> items, @NonNull WikiSite wiki) {
        List<RecommendedCategoriesItemCard> itemCards = new ArrayList<>();
        for (RecommendedCategoriesItem item : items) {
            itemCards.add(new RecommendedCategoriesItemCard(item, wiki));
        }
        return itemCards;
    }

    @Override protected int dismissHashCode() {
        // fixme
        return wikiSite().hashCode();
    }
}
