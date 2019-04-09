package org.wikipedia.saved.notes;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.wikipedia.R;
import org.wikipedia.util.DimenUtil;
import org.wikipedia.util.ResourceUtil;
import org.wikipedia.views.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteListItemView extends ConstraintLayout {
    public interface Callback {
        void onClick(@NonNull Note note);
        void onDelete(@NonNull Note note);
    }

    public enum Description { DETAIL, SUMMARY }

    @BindView(R.id.item_note_title) TextView titleView;
    @BindView(R.id.item_note_article_description) TextView articleDescriptionView;
    @BindView(R.id.item_note_content) TextView contentView;
    @BindView(R.id.item_note_overflow_menu)View overflowButton;

    @BindView(R.id.item_note_thumbnail) SimpleDraweeView thumbnail;

    @Nullable private org.wikipedia.saved.notes.NoteListItemView.Callback callback;
    @Nullable private Note note;

    public NoteListItemView(Context context) {
        super(context);
        init();
    }

    public NoteListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setNote(@NonNull Note note, @NonNull org.wikipedia.saved.notes.NoteListItemView.Description description) {
        this.note = note;

        boolean isDetailView = description == org.wikipedia.saved.notes.NoteListItemView.Description.DETAIL;
        contentView.setMaxLines(isDetailView
                ? Integer.MAX_VALUE
                : getResources().getInteger(R.integer.reading_list_description_summary_view_max_lines));

        updateDetails();
        updateThumbnail();
    }

    public void setCallback(@Nullable org.wikipedia.saved.notes.NoteListItemView.Callback callback) {
        this.callback = callback;
    }

    public void setThumbnailVisible(boolean visible) {
        thumbnail.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setTitleTextAppearance(@StyleRes int id) {
        TextViewCompat.setTextAppearance(titleView, id);
    }

    @OnClick void onClick(View view) {
        if (callback != null && note != null) {
            callback.onClick(note);
        }
    }

    @OnClick(R.id.item_note_overflow_menu) void showOverflowMenu(View anchorView) {
        PopupMenu menu = new PopupMenu(getContext(), anchorView, Gravity.END);
        menu.getMenuInflater().inflate(R.menu.menu_notes_list_item, menu.getMenu());
        menu.setOnMenuItemClickListener(new org.wikipedia.saved.notes.NoteListItemView.OverflowMenuClickListener(note));
        menu.show();
    }

    private void init() {
        inflate(getContext(), R.layout.item_notes, this);
        ButterKnife.bind(this);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        final int topBottomPadding = 16;
        setPadding(0, DimenUtil.roundedDpToPx(topBottomPadding), 0, DimenUtil.roundedDpToPx(topBottomPadding));
        setBackgroundColor(ResourceUtil.getThemedColor(getContext(), R.attr.paper_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setForeground(ContextCompat.getDrawable(getContext(), ResourceUtil.getThemedAttributeId(getContext(), R.attr.selectableItemBackground)));
        }

        setClickable(true);
        clearThumbnail();
    }

    private void updateDetails() {
        if (note == null) {
            return;
        }

        if (TextUtils.isEmpty(note.description())) {
            articleDescriptionView.setVisibility(GONE);
        } else {
            articleDescriptionView.setText(note.description());
        }

        titleView.setText(note.title());
        contentView.setText(note.content());
    }

    private void clearThumbnail() {
        if (thumbnail == null) {
            return;
        }

        ViewUtil.loadImageUrlInto(thumbnail, null);
        thumbnail.getHierarchy().setFailureImage(null);
    }

    private void updateThumbnail() {
        if (note == null) {
            return;
        }

        clearThumbnail();
        loadThumbnail(thumbnail, note.thumbUrl());
    }

    private void loadThumbnail(@NonNull SimpleDraweeView view, @Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            view.getHierarchy().setFailureImage(R.drawable.ic_image_gray_24dp,
                    ScalingUtils.ScaleType.FIT_CENTER);
        } else {
            ViewUtil.loadImageUrlInto(view, url);
        }
    }

    private class OverflowMenuClickListener implements PopupMenu.OnMenuItemClickListener {
        @Nullable private Note note;

        OverflowMenuClickListener(@Nullable Note note) {
            this.note = note;
        }

        @Override public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_notes_list_delete:
                    if (callback != null && note != null) {
                        callback.onDelete(note);
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}

