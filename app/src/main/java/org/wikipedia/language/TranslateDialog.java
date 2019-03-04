package org.wikipedia.language;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wikipedia.R;
import org.wikipedia.WikipediaApp;
import org.wikipedia.analytics.WiktionaryDialogFunnel;
import org.wikipedia.page.ExtendedBottomSheetDialogFragment;
import org.wikipedia.page.PageTitle;
import org.wikipedia.wiktionary.WiktionaryDialog;

import io.reactivex.disposables.CompositeDisposable;

import static org.wikipedia.util.L10nUtil.setConditionalLayoutDirection;

public class TranslateDialog extends ExtendedBottomSheetDialogFragment {
    public interface Callback {
        void translateShowDialogForTerm(@NonNull String term);
    }

    private static final String TITLE = "title";
    private static final String SELECTED_TEXT = "selected_text";

    private PageTitle pageTitle;
    private String selectedText;
    private String translatedText;
    private CompositeDisposable disposables = new CompositeDisposable();
    private View rootView;

    public static TranslateDialog newInstance(@NonNull PageTitle title, @NonNull String selectedText) {
        TranslateDialog dialog = new TranslateDialog();
        Bundle args = new Bundle();
        args.putParcelable(TITLE, title);
        args.putString(SELECTED_TEXT, selectedText);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTitle = getArguments().getParcelable(TITLE);
        selectedText = getArguments().getString(SELECTED_TEXT);
        translatedText = selectedText;
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_translate, container);

        TextView titleText = rootView.findViewById(R.id.translate_dialog_title);
        titleText.setText("Translation");

        TextView translationText = rootView.findViewById(R.id.translate_translated_text);
        translationText.setText(translatedText);

        setConditionalLayoutDirection(rootView, pageTitle.getWikiSite().languageCode());

        return rootView;
    }
}
