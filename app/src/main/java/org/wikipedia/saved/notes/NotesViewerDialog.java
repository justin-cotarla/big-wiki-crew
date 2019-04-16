package org.wikipedia.saved.notes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.wikipedia.R;
import org.wikipedia.activity.FragmentUtil;
import org.wikipedia.page.ExtendedBottomSheetDialogFragment;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;
import org.wikipedia.saved.notes.database.NoteDbHelper;
import org.wikipedia.util.ClipboardUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NotesViewerDialog extends ExtendedBottomSheetDialogFragment {
    private static final String TITLE = "title";

    @NonNull private PageTitle pageTitle;
    @NonNull private List<Note> notes;

    private RecyclerAdapter recyclerAdapter;

    @BindView(R.id.dialog_notes_empty_text) TextView emptyTextView;
    @BindView(R.id.dialog_notes_list) RecyclerView recyclerView;
    private Unbinder unbinder;

    public interface Callback {
        void onCancel();
    }

    public static NotesViewerDialog newInstance(@NonNull PageTitle title) {
        NotesViewerDialog dialog = new NotesViewerDialog();
        Bundle args = new Bundle();
        args.putParcelable(TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_notes_viewer, container);
        unbinder = ButterKnife.bind(this, rootView);

        if (notes.isEmpty()) {
            emptyTextView.setVisibility(VISIBLE);
            recyclerView.setVisibility(GONE);

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerAdapter = new RecyclerAdapter(this.getContext(), notes);
            recyclerView.setAdapter(recyclerAdapter);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTitle = getArguments().getParcelable(TITLE);
        notes = NoteDbHelper.getInstance().getNotesByArticle(pageTitle);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (callback() != null) {
            // noinspection ConstantConditions
            callback().onCancel();
        }
    }

    @Nullable
    public Callback callback() {
        return FragmentUtil.getCallback(this, Callback.class);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private Context context;
        private List<Note> notes;

        RecyclerAdapter(Context context, List<Note> notes) {
            this.context = context;
            this.notes = notes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes_on_page, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            viewHolder.note.setText(notes.get(i).content());
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
                MenuItem.OnMenuItemClickListener {
            private TextView note;

            ViewHolder(View view) {
                super(view);
                registerForContextMenu(view);
                view.setOnCreateContextMenuListener(this);
                note = view.findViewById(R.id.dialog_notes_text);
                note.setMovementMethod(new ScrollingMovementMethod());
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem copyItem = menu.add(Menu.NONE, R.id.menu_note_select_copy, 0, getString(R.string.menu_note_select_copy));
                copyItem.setOnMenuItemClickListener(this);
                MenuItem deleteItem = menu.add(Menu.NONE, R.id.menu_note_select_delete, 1, getString(R.string.menu_note_select_delete));
                deleteItem.setOnMenuItemClickListener(this);
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_note_select_copy:
                        copyNote(getAdapterPosition());
                        return true;
                    case R.id.menu_note_select_delete:
                        deleteNote(getAdapterPosition());
                        return true;
                    default:
                        return false;
                }
            }
        }

        void copyNote(final int position) {
            ClipboardUtil.setPlainText(context, "article_note", notes.get(position).content());
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        }

        void deleteNote(final int position) {
            NoteDbHelper.getInstance().deleteNote(notes.get(position));
            notes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notes.size());
        }
    }
}
