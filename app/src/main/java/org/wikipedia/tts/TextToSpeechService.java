package org.wikipedia.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import org.wikipedia.settings.Prefs;

public final class TextToSpeechService {
    private static TextToSpeech TTS_INSTANCE = null;

    public static void invalidate() {
        TTS_INSTANCE = null;
    }

    public static void speak(String text, Context context, float pitch, float speechRate) {
        if (TTS_INSTANCE == null) {
            TTS_INSTANCE = new TextToSpeech(context.getApplicationContext(), (status) -> {
                TTS_INSTANCE.setPitch(pitch);
                TTS_INSTANCE.setSpeechRate(speechRate);
                TTS_INSTANCE.speak(text, TextToSpeech.QUEUE_ADD, null);
            });
        }
        TTS_INSTANCE.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public static void speak(String text, Context context) {
        speak(text, context, Prefs.getTTSPitch(), Prefs.getTTSSpeechRate());
    }

    private TextToSpeechService() {
    }
}
