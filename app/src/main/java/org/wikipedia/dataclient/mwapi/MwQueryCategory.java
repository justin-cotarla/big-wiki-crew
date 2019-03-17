package org.wikipedia.dataclient.mwapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.model.BaseModel;

public class MwQueryCategory extends BaseModel {
    @SuppressWarnings("unused") @NonNull private String category;
    @SuppressWarnings("unused") @Nullable private int size;
    @SuppressWarnings("unused") @Nullable private int pages;
    @SuppressWarnings("unused") @Nullable private int files;
    @SuppressWarnings("unused") @Nullable private int subcats;


    @NonNull
    public String category() {
        return category;
    }

    @Nullable
    public int size() {
        return size;
    }

    @Nullable
    public int pages() {
        return pages;
    }

    @Nullable
    public int files() {
        return files;
    }

    @Nullable
    public int subcats() {
        return subcats;
    }
}
