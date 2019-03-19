package org.wikipedia.language;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.wikipedia.language.translation.TranslationClient;
import org.wikipedia.language.translation.TranslationService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TranslationClientTests {
    private String apiKey = "jehwreh";
    private String text = "sampleText";
    private String target = "sampleSource";
    private TranslationClient translationClient;

    @Mock
    private TranslationService translationService;

    @Before
    public void setUp() {
        translationService = mock(TranslationService.class);
        translationClient = new TranslationClient(apiKey, translationService);
    }

    @Test
    public void testTranslateEnglish() {
        translationClient.translate(text, target);
        verify(translationService).translate(text, "EN", target, apiKey);
    }
}
