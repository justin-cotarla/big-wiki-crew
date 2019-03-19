package org.wikipedia.feed.categories.card;

import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.feed.dataclient.DummyClient;
import org.wikipedia.feed.model.Card;

public class CategoriesClient extends DummyClient {
    @Override
    public Card getNewCard(WikiSite wiki) {
        return new CategoriesCard();
    }
}
