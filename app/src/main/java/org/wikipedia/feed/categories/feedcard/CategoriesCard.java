package org.wikipedia.feed.categories.feedcard;

import android.support.annotation.NonNull;

import org.wikipedia.feed.model.Card;
import org.wikipedia.feed.model.CardType;

public class CategoriesCard extends Card {
    @NonNull
    @Override
    public CardType type() {
        return CardType.CATEGORIES;
    }
}
