package com.otmj.otmjapp.API.Auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface SpotifyPlaybackService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @PUT("/v1/me/player/play")
    Call<Void> playSong(
            @Header("Authorization") String token,
            @Field("uris") String[] songsToPlay
    );

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("/v1/me/player/pause")
    Call<Void> pauseSong(@Header("Authorization") String token);
}
