package com.otmj.otmjapp.API.Auth;

import android.util.Log;

import com.otmj.otmjapp.API.Models.AccessToken;
import com.otmj.otmjapp.API.Models.RefreshToken;
import com.otmj.otmjapp.API.Models.TracksResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles communication with the Spotify API.
 */
public class SpotifyAPIClient {
    private final SpotifyPlaybackService playbackService;
    private final SpotifySearchService searchService;
    private final SpotifyAuthService authService;
    private static final SpotifyAPIClient instance = new SpotifyAPIClient();

    private SpotifyAPIClient() {

        OkHttpClient client = new OkHttpClient.Builder() // interceptor to verify the request being sent
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Log.d("SpotifyAPIManager", "Request URL: " + request.url());
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofitAPI = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Retrofit retrofitAuth = new Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // generate proxy objects for API calls
        playbackService = retrofitAPI.create(SpotifyPlaybackService.class);
        searchService = retrofitAPI.create(SpotifySearchService.class);
        authService = retrofitAuth.create(SpotifyAuthService.class);
    }

    public Call<TracksResponse> searchTracks(
                                       String accessToken,
                                       String query) {
        return searchService.searchTracks(
                "Bearer " + accessToken,
                "track:" + query,
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
    //TODO: investigate why app crashes after first sign in
    public Call<Void> playSong(String uri) {
        String accessToken = "Bearer " + SharedPreferencesHelper.getInstance().getTokenExpirationTime();
        String[] songsToPlay = {"spotify:track:" + uri};

        return playbackService.playSong(accessToken, songsToPlay);
    }

    public Call<Void> pauseSong() {
        String token = "Bearer " + SharedPreferencesHelper.getInstance().getAccessToken();

        return playbackService.pauseSong(token);
    }

    public static SpotifyAPIClient getInstance() {
        return instance;
    }
}
