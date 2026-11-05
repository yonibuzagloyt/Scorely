package com.example.scorely.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RawgApi {
    String BASE_URL = "https://api.rawg.io/api/";

    @GET("games")
    Call<RawgDtos.Response> searchGames(@Query("search") String query,
                                        @Query("key") String apiKey,
                                        @Query("page_size") int pageSize);

    @GET("games/{id}")
    Call<RawgDtos.RawgGame> gameDetails(@Path("id") int id, @Query("key") String apiKey);
}
