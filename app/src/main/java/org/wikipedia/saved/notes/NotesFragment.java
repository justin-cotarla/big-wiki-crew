package org.wikipedia.saved.notes;

import android.animation.LayoutTransition;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.wikipedia.R;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.main.MainActivity;
import org.wikipedia.page.ExclusiveBottomSheetPresenter;
import org.wikipedia.page.PageActivity;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;
import org.wikipedia.saved.notes.database.NoteDbHelper;
import org.wikipedia.saved.notes.noteitem.NoteItemActivity;
import org.wikipedia.settings.Prefs;
import org.wikipedia.util.DimenUtil;
import org.wikipedia.util.FeedbackUtil;
import org.wikipedia.views.DrawableItemDecoration;
import org.wikipedia.views.MarginItemDecoration;
import org.wikipedia.views.NotesListOverflowView;

import static org.wikipedia.saved.notes.NotesListSorter.SORT_BY_ARTICLE_NAME_ASC;
import static org.wikipedia.saved.notes.NotesListSorter.SORT_BY_ARTICLE_NAME_DESC;
import static org.wikipedia.saved.notes.NotesListSorter.SORT_BY_DATE_ADDED_NEWEST;
import static org.wikipedia.saved.notes.NotesListSorter.SORT_BY_DATE_ADDED_OLDEST;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotesFragment extends Fragment implements SortNotesListDialog.Callback {

    private Unbinder unbinder;

    @BindView(R.id.notes_content_container) ViewGroup contentContainer;
    @BindView(R.id.notes_list_list) RecyclerView notesListView;

    private List<Note> notes = new ArrayList<>();
    private NoteListAdapter adapter = new NoteListAdapter();

    private NoteListItemCallback listItemCallback = new NoteListItemCallback();
    private OverflowCallback overflowCallback = new OverflowCallback();

    private ExclusiveBottomSheetPresenter bottomSheetPresenter = new ExclusiveBottomSheetPresenter();

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
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_overflow_button_notes:
                NotesListOverflowView overflowView = new NotesListOverflowView(requireContext());
                overflowView.show(((MainActivity) requireActivity()).getToolbar().findViewById(R.id.menu_overflow_button_notes), overflowCallback);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getNotes() {
        notes = NoteDbHelper.getInstance().getAllNotes();
        NotesListSorter.sort(notes, Prefs.getNotesListSortMode(SORT_BY_DATE_ADDED_NEWEST));
        adapter.notifyDataSetChanged();
    }

    private void deleteNote(@NonNull Note note) {
        if (notes != null) {
            showDeleteListUndoSnackbar(note);
            NoteDbHelper.getInstance().deleteNote(note);
            getNotes();
        }
    }

    private void showDeleteListUndoSnackbar(final Note note) {
        Snackbar snackbar = FeedbackUtil.makeSnackbar(getActivity(),
                getString(R.string.note_deleted),
                FeedbackUtil.LENGTH_DEFAULT);
        snackbar.setAction(R.string.note_deleted_undo, v -> {
            NoteDbHelper.getInstance().saveNote(note);
            getNotes();
        });
        snackbar.show();
    }

    @Override
    public void onSortOptionClick(int position) {
        sortListsBy(position);
    }

    private void sortListsBy(int option) {
        switch (option) {
            case SORT_BY_ARTICLE_NAME_ASC:
                Prefs.setNotesListSortMode(SORT_BY_ARTICLE_NAME_ASC);
                break;
            case SORT_BY_ARTICLE_NAME_DESC:
                Prefs.setNotesListSortMode(SORT_BY_ARTICLE_NAME_DESC);
                break;
            case SORT_BY_DATE_ADDED_NEWEST:
                Prefs.setNotesListSortMode(SORT_BY_DATE_ADDED_NEWEST);
                break;
            case SORT_BY_DATE_ADDED_OLDEST:
                Prefs.setNotesListSortMode(SORT_BY_DATE_ADDED_OLDEST);
                break;
            default:
                break;
        }

        getNotes();
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

    private class OverflowCallback implements NotesListOverflowView.Callback {
        @Override
        public void sortByClick() {
            bottomSheetPresenter.show(getChildFragmentManager(),
                    SortNotesListDialog.newInstance(Prefs.getNotesListSortMode(SORT_BY_DATE_ADDED_NEWEST)));
        }
    }
}
