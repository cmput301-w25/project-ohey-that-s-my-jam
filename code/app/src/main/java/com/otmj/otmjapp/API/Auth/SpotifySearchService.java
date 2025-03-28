package com.otmj.otmjapp.API.Auth;

import com.otmj.otmjapp.API.Models.TracksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 *
 */
public interface SpotifySearchService {
    /**
     * Search for a track.
     *
     * @param accessToken The access token required for authorization.
     * @param query       The search query string.
     * @return A Call object that represents a request for the search results.
     */
    @GET("/v1/search")
    Call<TracksResponse> searchTracks(
            @Header("Authorization") String accessToken,
            @Query("q") String query,
            @Query("type") String type,
            @Query("limit") int limit
    );
}
