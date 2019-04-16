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
import org.wikipedia.util.ClipboardUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NotesViewerDialog extends ExtendedBottomSheetDialogFragment {
    private List<String> notes = new ArrayList<>();

    private RecyclerAdapter recyclerAdapter;

    public interface Callback {
        void onCancel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_notes_viewer, container);
        TextView emptyTextView = rootView.findViewById(R.id.dialog_notes_empty_text);
        RecyclerView recyclerView = rootView.findViewById(R.id.dialog_notes_list);

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
        // TODO: Fetch notes from notes API wrt article. These are temporary hardcoded examples.
        notes.add("As an example, this is a shorter text to observe the differences in text length.");
        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        notes.add("Sed ullamcorper morbi tincidunt ornare. Tellus orci ac auctor augue mauris. Mauris sit amet massa vitae. Risus feugiat in ante metus dictum at tempor. Amet dictum sit amet justo donec enim. Lectus quam id leo in vitae turpis massa. Nunc consequat interdum varius sit amet mattis vulputate enim. Adipiscing commodo elit at imperdiet dui accumsan sit amet nulla. Diam in arcu cursus euismod quis viverra nibh cras pulvinar. Scelerisque purus semper eget duis at tellus at.");
        notes.add("Egestas erat imperdiet sed euismod nisi. Sodales ut etiam sit amet nisl purus in. Tellus orci ac auctor augue. Dui id ornare arcu odio ut sem. Orci dapibus ultrices in iaculis. Et tortor at risus viverra adipiscing at. Eget felis eget nunc lobortis mattis aliquam faucibus purus in.");
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
        private List<String> notes;

        RecyclerAdapter(Context context, List<String> notes) {
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
            String text = notes.get(i);
            viewHolder.note.setText(text);
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
            ClipboardUtil.setPlainText(context, "article_note", notes.get(position));
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        }

        // TODO: To complete with notes API
        void deleteNote(final int position) {
            notes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notes.size());
        }
    }
}
