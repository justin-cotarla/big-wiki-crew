package org.wikipedia.language.translation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.wikipedia.R;
import org.wikipedia.page.ExtendedBottomSheetDialogFragment;
import org.wikipedia.page.PageTitle;
import org.wikipedia.views.LanguageScrollView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static org.wikipedia.util.L10nUtil.setConditionalLayoutDirection;

public class TranslateDialog extends ExtendedBottomSheetDialogFragment implements LanguageScrollView.Callback{
    public interface Callback {
        void translateShowDialogForTerm(@NonNull String term);
    }

    private static final String TITLE = "title";
    private static final String SELECTED_TEXT = "selected_text";

    private ProgressBar progressBar;
    private CompositeDisposable disposables = new CompositeDisposable();
    private View rootView;
    private TranslationClient translationClient;

    private boolean initialized = false;

    private PageTitle pageTitle;
    private String selectedText;

    @BindView(R.id.lang_scroll) LanguageScrollView languageScrollView;
    @BindView(R.id.language_scroll_container) View languageScrollContainer;
    @BindView(R.id.more_languages) TextView moreButton;
    @BindView(R.id.translate_translated_text) TextView translationText;
    private Unbinder unbinder;

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
        translationClient = new TranslationClient(getContext());
        pageTitle = getArguments().getParcelable(TITLE);
        selectedText = getArguments().getString(SELECTED_TEXT);
   }

   public void onCreate(Bundle savedInstanceState, TranslationClient translationClient, ProgressBar progressBar, TextView translationText, CompositeDisposable disposables, String selectedText) {
        super.onCreate(savedInstanceState);
        this.translationClient = translationClient;
        this.pageTitle = getArguments().getParcelable(TITLE);
        this.selectedText = selectedText;
        this.progressBar = progressBar;
        this.translationText = translationText;
        this.disposables = disposables;
   }

    @Override
    public void onDestroy() {
        disposables.clear();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_translate, container);
        unbinder = ButterKnife.bind(this, rootView);
        progressBar = rootView.findViewById(R.id.dialog_translate_progress);

        ArrayList<TranslationClient.Language> langList = new ArrayList<>(Arrays.asList(TranslationClient.Language.values()));
        ArrayList<String> langNames = new ArrayList<>();
        for (TranslationClient.Language lang : langList) {
            langNames.add(lang.name());
        }

        setConditionalLayoutDirection(rootView, pageTitle.getWikiSite().languageCode());
        languageScrollContainer.setVisibility(View.VISIBLE);
        moreButton.setVisibility(View.GONE);
        languageScrollView.setUpLanguageScrollTabData(langNames, this, 0);
        translationText.setText(selectedText);

        return rootView;
    }

    public void translateText(String text, String language) {
        progressBar.setVisibility(View.VISIBLE);
        translationText.setText("");
        disposables.add(translationClient.translate(text, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translationResponse -> {
                    progressBar.setVisibility(View.GONE);
                    String translated = translationResponse.getData().getTranslations().get(0).getTranslatedText();
                    translationText.setText(translated);
                }));
    }

    @Override
    public void onLanguageTabSelected(String selectedLanguageCode) {
        if (!initialized) {
            initialized = true;
            return;
        }

        // Original text
        if (selectedLanguageCode.equals(TranslationClient.Language.EN.name())) {
            translationText.setText(selectedText);
            return;
        }

        translateText(selectedText, selectedLanguageCode);
    }

    @Override
    public void onLanguageButtonClicked() {
        // Stub
    }
}
