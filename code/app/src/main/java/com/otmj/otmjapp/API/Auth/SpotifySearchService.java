package com.otmj.otmjapp.API.Auth;

import com.otmj.otmjapp.API.Models.Track;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 */
public interface SpotifySearchService {
    @GET("/search")
    Call<ArrayList<Track>> searchTracks(
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit
    );
}
