package org.wikipedia.tts;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

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
            } else {
                System.err.println("ERROR: Failed to initialize TTS engine.");
            }
        });
    }

    public TextToSpeech get() {
        return tts;
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
                    speakWithUtteranceListener(text);
                } else {
                    System.err.println("ERROR: Failed to initialize TTS engine.");
                }
            });
        } else {
            speakWithUtteranceListener(text);
        }
    }

    public void speakWithUtteranceListener(String text) {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // nothing
            }

            @Override
            public void onDone(String utteranceId) {
                tts.shutdown();
            }

            @Override
            public void onError(String utteranceId) {
                System.err.println("ERROR: Something went wrong during the TTS process.");
            }
        });
        speakWithUtteranceId(text, "ttsWrapper");
    }

    private void speak(String text, int mode) {
        tts.speak(text, mode, null);
    }

    private void speakWithUtteranceId(String text, int mode, String id) {
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id);

        // todo: other versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, mode, params, id);
        }
    }
}
