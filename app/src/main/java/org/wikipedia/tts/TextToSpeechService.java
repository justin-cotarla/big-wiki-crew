package org.wikipedia.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.wikipedia.settings.Prefs;

public class TextToSpeechService {
    private static TextToSpeech ttsInstance = null;

    public static void invalidate() {
        ttsInstance = null;
    }

    public static void speak(String text, Context context, float pitch, float speechRate) {
        if (ttsInstance == null) {
            ttsInstance = new TextToSpeech(context.getApplicationContext(), (status) -> {
                ttsInstance.setPitch(pitch);
                ttsInstance.setSpeechRate(speechRate);
                ttsInstance.speak(text, TextToSpeech.QUEUE_ADD, null);
            });
        }
        ttsInstance.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public static void speak(String text, Context context) {
        speak(text, context, Prefs.getTTSPitch(), Prefs.getTTSSpeechRate());
    }

    private TextToSpeechService() {
    }
}
