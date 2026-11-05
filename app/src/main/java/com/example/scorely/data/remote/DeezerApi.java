package com.example.scorely.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeezerApi {
    String BASE_URL = "https://api.deezer.com/";

    @GET("search/album")
    Call<DeezerDtos.SearchResponse> searchAlbums(@Query("q") String query);

    @GET("album/{id}")
    Call<DeezerDtos.DeezerAlbum> albumDetails(@Path("id") long id);
}
