package org.wikipedia.language.translation;

import android.content.Context;

import org.wikipedia.R;

import org.wikipedia.json.GsonUtil;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslationClient {
    public enum Language {
        EN,
        FR,
        ES,
        HI,
        EL,
        AR,
        PT,
        RO,
        RU,
        JA,
        DE,
        IT
    }

    TranslationService service;
    String apiKey;

    public TranslationClient(Context applicationContext) {
        apiKey = applicationContext.getString(R.string.google_translation_api_key);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://translation.googleapis.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.getDefaultGson()))
                .build();

        service = retrofit.create(TranslationService.class);
    }

    public TranslationClient(String apiKey, TranslationService service) {
        this.apiKey = apiKey;
        this.service = service;
    }

    public Observable<TranslationResponse> translate(String text, String target, String source) {
        return service.translate(text, source, target, apiKey);
    }

    public Observable<TranslationResponse> translate(String text, String target) {
        return translate(text, target, Language.EN.name());
    }
}
