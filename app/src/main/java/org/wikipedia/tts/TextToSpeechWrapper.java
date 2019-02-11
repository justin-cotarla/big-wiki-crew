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
//        tts.stop();
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


//    private class AsyncBuilder extends AsyncTask<Void, Void, String> {
//
//        private Context context;
//
//        public AsyncBuilder(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            tts = new TextToSpeech(context, (status) -> {
//                tts.setPitch(Prefs.getTTSPitch());
//                tts.setSpeechRate(Prefs.getTTSSpeechRate());
//                doneInit = true;
//            });
//
//            long timeout = 5000;
//            long start = System.currentTimeMillis();
//
//            while (!doneInit) {
//                try {
//                    Thread.sleep(100);
//                    long now = System.currentTimeMillis();
//                    if (now - start >= timeout) {
//                        throw new RuntimeException("Shit happened tts failed to init in 5 seconds.");
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            System.out.println("Initializing text-to-speech engine...");
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            System.out.println("@@@@ DONEEEE @@@@");
//        }
//    }
}
