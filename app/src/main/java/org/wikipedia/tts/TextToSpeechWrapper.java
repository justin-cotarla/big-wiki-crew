package org.wikipedia.tts;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import org.wikipedia.settings.Prefs;

public class TextToSpeechWrapper {
    private TextToSpeech tts;

    public TextToSpeechWrapper() {
    }

    public TextToSpeechWrapper(Context context) {
        tts = new TextToSpeech(context, status -> {

            if (status == TextToSpeech.SUCCESS) {
                tts.setPitch(Prefs.getTTSPitch());
                tts.setSpeechRate(Prefs.getTTSSpeechRate());
            }
            else {
                System.err.println("ERROR: Failed to initialize TTS engine.");
            }
        });
    }

    public TextToSpeech get() {
        return tts;
    }

    public void invalidate() {
        if (tts != null) {
            tts.shutdown();
        }
        tts = null;
    }

    public void speak(String text) {
        speak(text, TextToSpeech.QUEUE_FLUSH);
    }

    public void speakWithUtteranceId(String text, String id) {
        speakWithUtteranceId(text, TextToSpeech.QUEUE_FLUSH, id);
    }

    public void addToQueue(String text) {
        speak(text, TextToSpeech.QUEUE_ADD);
    }

    public void addToQueueWithUtteranceId(String text, String id) {
        speakWithUtteranceId(text, TextToSpeech.QUEUE_ADD, id);
    }


    public void initAndSpeak(Context context, String text) {
        if (tts == null) {
            tts = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setPitch(Prefs.getTTSPitch());
                    tts.setSpeechRate(Prefs.getTTSSpeechRate());
                    speak(text);
                }
                else {
                    System.err.println("ERROR: Failed to initialize TTS engine.");
                }
            });
        }
        else {
            speak(text);
        }
    }

    private void speak(String text, int mode) {
        tts.speak(text, mode, null);
    }

    private void speakWithUtteranceId(String text, int mode, String id) {
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, mode, params, id);
        }
    }
}
