package com.example.scorely.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** יוצר פעם אחת את לקוחות ה-Retrofit (Singleton). */
public class RetrofitProvider {

    private static RawgApi rawg;
    private static DeezerApi deezer;

    public static synchronized RawgApi rawg() {
        if (rawg == null) {
            rawg = new Retrofit.Builder().baseUrl(RawgApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(RawgApi.class);
        }
        return rawg;
    }

    public static synchronized DeezerApi deezer() {
        if (deezer == null) {
            deezer = new Retrofit.Builder().baseUrl(DeezerApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                    .create(DeezerApi.class);
        }
        return deezer;
    }
}
