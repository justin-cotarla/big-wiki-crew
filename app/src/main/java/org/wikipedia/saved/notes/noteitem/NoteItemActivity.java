package org.wikipedia.saved.notes.noteitem;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.wikipedia.activity.SingleFragmentActivity;
import org.wikipedia.page.PageTitle;

public class NoteItemActivity extends SingleFragmentActivity<NoteItemFragment> {
    public static final String NOTE_CONTENT = "noteContent";
    public static final String NOTE_PAGE = "notePage";

    public static Intent newIntent(@NonNull Context context, @NonNull String content, @NonNull PageTitle page) {
        return new Intent(context, NoteItemActivity.class)
                .putExtra(NOTE_CONTENT, content)
                .putExtra(NOTE_PAGE, page);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0f);
    }

    @Override
    protected NoteItemFragment createFragment() {
        return NoteItemFragment.newInstance();
    }
}
