package org.wikipedia.language.translation;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TranslationService {
    @POST("language/translate/v2?format=text")
    Observable<TranslationResponse> translate(
            @Query("q") String text,
            @Query("source") String source,
            @Query("target") String target,
            @Query("key") String key
    );

}
