package org.wikipedia.saved.notes.noteitem;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.wikipedia.R;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.page.PageActivity;
import org.wikipedia.page.PageTitle;
import org.wikipedia.views.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static org.wikipedia.saved.notes.noteitem.NoteItemActivity.NOTE_CONTENT;
import static org.wikipedia.saved.notes.noteitem.NoteItemActivity.NOTE_PAGE;


public class NoteItemFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.notes_item_image) SimpleDraweeView noteItemImage;
    @BindView(R.id.notes_item_text) TextView noteItemText;
    @BindView(R.id.notes_item_title) TextView noteItemTitle;

    private String content;
    private PageTitle page;

    @NonNull
    public static NoteItemFragment newInstance() {
        return new NoteItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        unbinder = ButterKnife.bind(this, view);
        content = requireActivity().getIntent().getStringExtra(NOTE_CONTENT);
        page = requireActivity().getIntent().getParcelableExtra(NOTE_PAGE);
        ViewUtil.loadImageUrlInto(noteItemImage, page.getThumbUrl());
        noteItemText.setText(content);
        noteItemTitle.setText(page.getDisplayText());
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @OnClick(R.id.notes_item_redirect) void onRedirectClick() {
        HistoryEntry historyEntry = new HistoryEntry(page, HistoryEntry.SOURCE_SAVED_NOTES);
        startActivity(PageActivity.newIntentForCurrentTab(requireContext(), historyEntry, historyEntry.getTitle()));
    }

}
