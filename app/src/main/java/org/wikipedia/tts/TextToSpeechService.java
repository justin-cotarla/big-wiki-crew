package org.wikipedia.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.wikipedia.settings.Prefs;

public class TextToSpeechService {
    private static TextToSpeech ttsInstance = null;

    public static void invalidate() {
        ttsInstance = null;
    }

    public static void speak(String text, Context context) {
        if (ttsInstance == null) {
            ttsInstance = new TextToSpeech(context.getApplicationContext(), (status) -> {
                ttsInstance.setPitch(Prefs.getTTSPitch());
                ttsInstance.setSpeechRate(Prefs.getTTSSpeechRate());
                ttsInstance.speak(text, TextToSpeech.QUEUE_ADD, null);
            });
        }
        ttsInstance.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    private TextToSpeechService() {
    }
}
