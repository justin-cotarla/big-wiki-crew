package org.wikipedia.saved.notes;

import android.animation.LayoutTransition;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import org.wikipedia.R;
import org.wikipedia.WikipediaApp;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.main.MainActivity;
import org.wikipedia.page.PageActivity;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;
import org.wikipedia.saved.notes.noteitem.NoteItemActivity;
import org.wikipedia.util.DimenUtil;
import org.wikipedia.views.DrawableItemDecoration;
import org.wikipedia.views.MarginItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotesFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.notes_content_container) ViewGroup contentContainer;
    @BindView(R.id.notes_list_list) RecyclerView notesListView;

    private List<Note> notes = new ArrayList<>();
    private NoteListAdapter adapter = new NoteListAdapter();

    private NoteListItemCallback listItemCallback = new NoteListItemCallback();

    @NonNull
    public static Fragment newInstance() {
        return new NotesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        unbinder = ButterKnife.bind(this, view);

        notesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        notesListView.setAdapter(adapter);
        notesListView.addItemDecoration(new DrawableItemDecoration(requireContext(), R.attr.list_separator_drawable, false));
        notesListView.addItemDecoration(new MarginItemDecoration(0, 0, 0, DimenUtil.roundedDpToPx(DimenUtil.getDimension(R.dimen.floating_queue_container_height))) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == adapter.getItemCount() - 1
                        && ((MainActivity) requireActivity()).isFloatingQueueEnabled()
                        && notes.size() > 1) {
                    super.getItemOffsets(outRect, view, parent, state);
                }
            }
        });

        contentContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        return view;
    }

    @Override
    public void onDestroyView() {
        notesListView.setAdapter(null);
        unbinder.unbind();
        unbinder = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotes();
    }

    private void getNotes() {
        // TODO: hook up to note service to get notes
        // For now, fill with fake note data
        notes.add(new Note("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", new PageTitle("Nipsey Hussle",
                WikipediaApp.getInstance().getWikiSite(),
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Soundtrack_Beat_Battle_Judging_Panel_March2011_%28cropped%29.jpg/320px-Soundtrack_Beat_Battle_Judging_Panel_March2011_%28cropped%29.jpg",
                "Eritrean American rapper"), new Date()));
        notes.add(new Note("Test note 2", WikipediaApp.getInstance().getWikiSite(), "Test Article 2", new Date()));
    }

    private void deleteNote(@NonNull Note note) {
        // TODO: hook up to note service for note deletion
    }

    private class NoteListItemCallback implements NoteListItemView.Callback {
        @Override
        public void onClick(@NonNull Note note) {
            startActivity(NoteItemActivity.newIntent(requireContext(), note.content(), note.getPageTitle()));
        }

        @Override
        public void onDelete(@NonNull Note note) {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
            alert.setMessage(R.string.notes_list_delete_confirm);
            alert.setPositiveButton(android.R.string.yes, (dialog, id) -> deleteNote(note));
            alert.setNegativeButton(android.R.string.no, null);
            alert.create().show();
        }

        @Override
        public void onRedirect(@NonNull Note note) {
            PageTitle article = note.getPageTitle();
            HistoryEntry historyEntry = new HistoryEntry(article, HistoryEntry.SOURCE_SAVED_NOTES);
            startActivity(PageActivity.newIntentForCurrentTab(requireContext(), historyEntry, historyEntry.getTitle()));
        }

    }

    private class NoteListItemHolder extends RecyclerView.ViewHolder {
        private NoteListItemView itemView;

        NoteListItemHolder(NoteListItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        void bindItem(Note note) {
            itemView.setNote(note, NoteListItemView.Description.SUMMARY);
            itemView.setThumbnailVisible(true);
        }

        public NoteListItemView getView() {
            return itemView;
        }
    }

    private final class NoteListAdapter extends RecyclerView.Adapter<NoteListItemHolder> {
        @Override
        public int getItemCount() {
            return notes.size();
        }

        @Override
        public NoteListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
            NoteListItemView view = new NoteListItemView(getContext());
            return new NoteListItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteListItemHolder holder, int pos) {
            holder.bindItem(notes.get(pos));
        }

        @Override public void onViewAttachedToWindow(@NonNull NoteListItemHolder holder) {
            super.onViewAttachedToWindow(holder);
            holder.getView().setCallback(listItemCallback);
        }

        @Override public void onViewDetachedFromWindow(@NonNull NoteListItemHolder holder) {
            holder.getView().setCallback(null);
            super.onViewDetachedFromWindow(holder);
        }
    }
}
