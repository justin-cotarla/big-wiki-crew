package org.wikipedia.language;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wikipedia.language.translation.TranslateDialog;
import org.wikipedia.language.translation.TranslationClient;
import org.wikipedia.language.translation.TranslationResponse;
import org.wikipedia.page.PageTitle;


import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslateDialogTests {
    private String apiKey = "jehwreh";
    private String selectedText = "mon-text";
    private String sampleLanguage = "EN";
    private String otherLanguage = "FR";
    private TranslateDialog translateDialog;

    @Mock
    private PageTitle pageTitleMock;

    @Mock
    private TranslationClient translationClientMock;

    @Mock
    private ProgressBar progressBarMock;

    @Mock
    private TextView textMock;

    @Mock
    private Observable<TranslationResponse> responseMock;

    static {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @Before
    public void setUp() {
        pageTitleMock = mock(PageTitle.class);
        translationClientMock = mock(TranslationClient.class);
        progressBarMock = mock(ProgressBar.class);
        textMock = mock(TextView.class);
        responseMock = mock(Observable.class);

        translateDialog = TranslateDialog.newInstance(pageTitleMock, selectedText);
    }

    @Test
    public void testTranslateText() {
        translateDialog.onCreate(null, translationClientMock, progressBarMock, textMock, new CompositeDisposable(), selectedText);
        when(translationClientMock.translate(selectedText, sampleLanguage)).thenReturn(responseMock);

        translateDialog.translateText(selectedText, sampleLanguage);
        verify(progressBarMock).setVisibility(View.VISIBLE);
        verify(textMock).setText("");
        verify(translationClientMock).translate(selectedText, sampleLanguage);
    }

    @Test
    public void testSelectLanguageTabEnglish() {
        translateDialog.onCreate(null, translationClientMock, progressBarMock, textMock, new CompositeDisposable(), selectedText);
        when(translationClientMock.translate(selectedText, sampleLanguage)).thenReturn(responseMock);

        translateDialog.onLanguageTabSelected("");
        translateDialog.onLanguageTabSelected(sampleLanguage);
        verify(textMock).setText(selectedText);
    }

    @Test
    public void testSelectLanguageTabNotEnglish() {
        translateDialog.onCreate(null, translationClientMock, progressBarMock, textMock, new CompositeDisposable(), selectedText);
        when(translationClientMock.translate(selectedText, otherLanguage)).thenReturn(responseMock);

        translateDialog.onLanguageTabSelected("");
        translateDialog.onLanguageTabSelected(otherLanguage);
        verify(progressBarMock).setVisibility(View.VISIBLE);
        verify(textMock).setText("");
        verify(translationClientMock).translate(selectedText, otherLanguage);
    }
}
