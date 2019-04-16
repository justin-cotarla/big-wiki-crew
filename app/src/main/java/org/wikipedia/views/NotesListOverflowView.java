package org.wikipedia.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import org.wikipedia.R;
import org.wikipedia.util.DimenUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotesListOverflowView extends FrameLayout {

    public interface Callback {
        void sortByClick();
    }

    @Nullable
    private Callback callback;
    @Nullable private PopupWindow popupWindowHost;

    public NotesListOverflowView(Context context) {
        super(context);
        init();
    }

    public void show(@NonNull View anchorView, @Nullable Callback callback) {
        this.callback = callback;
        popupWindowHost = new PopupWindow(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowHost.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        PopupWindowCompat.setOverlapAnchor(popupWindowHost, true);
        final int compatOffset = 8;
        PopupWindowCompat.showAsDropDown(popupWindowHost, anchorView, 0, Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                ? -DimenUtil.getToolbarHeightPx(anchorView.getContext()) + DimenUtil.roundedDpToPx(compatOffset) : 0, Gravity.END);
    }

    @OnClick({R.id.notes_list_overflow_sort_by})
    void onItemClick(View view) {
        if (popupWindowHost != null) {
            popupWindowHost.dismiss();
            popupWindowHost = null;
        }
        if (callback == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.notes_list_overflow_sort_by:
                callback.sortByClick();
                break;
            default:
                break;
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_notes_list_overflow, this);
        ButterKnife.bind(this);

        CardView cardContainer = findViewById(R.id.notes_list_overflow_card_container);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cardContainer.setPreventCornerOverlap(false);
        }
    }
}
