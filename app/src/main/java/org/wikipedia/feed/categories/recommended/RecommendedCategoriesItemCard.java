package org.wikipedia.feed.categories.recommended;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.feed.model.Card;
import org.wikipedia.feed.model.CardType;
import org.wikipedia.util.StringUtil;

public class RecommendedCategoriesItemCard extends Card {
    @NonNull private RecommendedCategoriesItem categoriesItem;
    @NonNull private WikiSite wiki;

    RecommendedCategoriesItemCard(@NonNull RecommendedCategoriesItem item, @NonNull WikiSite wiki) {
        this.categoriesItem = item;
        this.wiki = wiki;
    }

    @NonNull public RecommendedCategoriesItem item() {
        return categoriesItem;
    }

    @NonNull public WikiSite wikiSite() {
        return wiki;
    }

    @Nullable @Override public Uri image() {
        return categoriesItem.thumb();
    }

    @NonNull @Override public CardType type() {
        return CardType.RECOMMENDED_CATEGORIES;
    }

    @NonNull public CharSequence text() {
        return StringUtil.fromHtml(categoriesItem.name());
    }
}
