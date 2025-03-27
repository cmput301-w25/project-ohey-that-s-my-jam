package com.otmj.otmjapp.API.Auth;

import android.util.Log;

import com.otmj.otmjapp.API.Models.AccessToken;
import com.otmj.otmjapp.API.Models.RefreshToken;
import com.otmj.otmjapp.API.Models.Track;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles communication with the Spotify API.
 */
public class SpotifyAPIClient {
    private final SpotifySearchService searchService;
    private final SpotifyAuthService authService;
    private static final SpotifyAPIClient instance = new SpotifyAPIClient();

    private SpotifyAPIClient() {
        Retrofit retrofitAPI = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit retrofitAuth = new Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // generate proxy objects for API calls
        searchService = retrofitAPI.create(SpotifySearchService.class);
        authService = retrofitAuth.create(SpotifyAuthService.class);
    }

    // TODO: make methods static
    public Call<ArrayList<Track>> searchTracks(
                                       String accessToken,
                                       String query) {
        Log.d("SpotifyAPIClient", "Query: " + query);
        Log.d("SpotifyAPIClient", "Access token: " + accessToken);
        Log.d("SpotifyAPIClient", "Search service: " + searchService.toString());

        return searchService.searchTracks(
                "Bearer " + accessToken,
                "track:" + query,  // putting 'track' before query ensures only tracks are returned
                "track", 15);
    }

    /**
     * {@link SpotifyAuthService#getAccessToken(String, String, String, String, String)}
     */
    public Call<AccessToken> getAccessToken(
                                 String grantType,
                                 String authCode,
                                 String redirectUri,
                                 String clientID,
                                 String codeVerifier
    ) {
        return authService.getAccessToken(grantType, authCode, redirectUri, clientID, codeVerifier);
    }

    /**
     * {@link SpotifyAuthService#refreshAccessToken(String, String, String)}
     */
    public Call<RefreshToken> refreshAccessToken(
                                         String grantType,
                                         String refreshToken,
                                         String clientID) {
        return authService.refreshAccessToken(grantType, refreshToken, clientID);
    }

    public static SpotifyAPIClient getInstance() {
        return instance;
    }
}
