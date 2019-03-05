package org.wikipedia.language.translation;

import android.content.Context;
import android.util.Log;

import org.wikipedia.R;

import org.wikipedia.json.GsonUtil;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslationClient {
    public enum Language {
        ENGLISH("en"),
        FRENCH ("fr"),
        CHINESE("zh-CN"),
        SPANISH("es"),
        HINDI("hi"),
        GREEK("el"),
        ARABIC("ar"),
        PORTUGUESE("pt"),
        ROMANIAN("ro"),
        RUSSIAN("ru"),
        JAPANESE("ja"),
        GERMAN("de"),
        ITALIAN("it");

        private String code;

        Language(String code) {
            this.code = code;
        }
    }

    TranslationService service;
    String apiKey;

    public TranslationClient(Context applicationContext) {
        apiKey = applicationContext.getString(R.string.google_translation_api_key);
        Log.i("TRANSLATION", apiKey);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://translation.googleapis.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.getDefaultGson()))
                .build();

        service = retrofit.create(TranslationService.class);
    }

    public Observable<TranslationResponse> translate(String text, Language target, Language source) {
        return service.translate(text, source.code, target.code, apiKey);
    }

    public Observable<TranslationResponse> translate(String text, Language target) {
        return translate(text, target, Language.ENGLISH);
    }
}
