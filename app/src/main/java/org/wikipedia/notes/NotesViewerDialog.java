package org.wikipedia.notes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.wikipedia.R;
import org.wikipedia.activity.FragmentUtil;
import org.wikipedia.page.ExtendedBottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class NotesViewerDialog extends ExtendedBottomSheetDialogFragment {
    // TODO: Fetch notes from notes API wrt article. These are temporary hardcoded examples.
    private List<String> notes = Arrays.asList(
            "As an example, this is a shorter text to observe the differences in text length.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            "Sed ullamcorper morbi tincidunt ornare. Tellus orci ac auctor augue mauris. Mauris sit amet massa vitae. Risus feugiat in ante metus dictum at tempor. Amet dictum sit amet justo donec enim. Lectus quam id leo in vitae turpis massa. Nunc consequat interdum varius sit amet mattis vulputate enim. Adipiscing commodo elit at imperdiet dui accumsan sit amet nulla. Diam in arcu cursus euismod quis viverra nibh cras pulvinar. Scelerisque purus semper eget duis at tellus at.",
            "Egestas erat imperdiet sed euismod nisi. Sodales ut etiam sit amet nisl purus in. Tellus orci ac auctor augue. Dui id ornare arcu odio ut sem. Orci dapibus ultrices in iaculis. Et tortor at risus viverra adipiscing at. Eget felis eget nunc lobortis mattis aliquam faucibus purus in.");

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
            recyclerView.setAdapter(new RecyclerAdapter(this.getContext(), notes));
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            String text = notes.get(i);
            viewHolder.note.setText(text);
            viewHolder.note.setOnLongClickListener(clicked -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("article_note", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView note;

            ViewHolder(View view) {
                super(view);
                note = view.findViewById(R.id.dialog_notes_text);
                note.setMovementMethod(new ScrollingMovementMethod());
            }
        }
    }
}
