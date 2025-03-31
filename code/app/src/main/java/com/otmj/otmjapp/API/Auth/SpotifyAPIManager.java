package com.otmj.otmjapp.API.Auth;

import static android.util.Base64.NO_PADDING;
import static android.util.Base64.NO_WRAP;
import static android.util.Base64.URL_SAFE;
import static android.util.Base64.encodeToString;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.API.Models.AccessToken;
import com.otmj.otmjapp.API.Models.RefreshToken;
import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.API.Models.TracksResponse;
import com.otmj.otmjapp.Fragments.AddEditMusicDialogFragment;
import com.otmj.otmjapp.MainActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles Spotify authentication, token management, and token refresh.
 */
public class SpotifyAPIManager {
    private final String CLIENT_ID;
    private final String REDIRECT_URI;
    private final String ENCODED_REDIRECT_URI;

    private final SharedPreferencesHelper prefsHelper;
    private final Activity activity;

    private static final String[] SCOPES = {
            "user-read-private",
            "streaming"
    };

    public SpotifyAPIManager(MainActivity activity) {
        this.activity = activity;
        // TODO: find way to get environment variables using a library

        CLIENT_ID = "5d2e6eb636f1462aa365d47ae67aeb9f";
        REDIRECT_URI = "com.otmj.otmjapp://callback";
        ENCODED_REDIRECT_URI = Uri.encode(REDIRECT_URI);

        prefsHelper = SharedPreferencesHelper.getInstance();
    }

    /**
     * Searches for a song using the the provide search query by making a call to Spotify's search
     * endpoint.
     *
     * @param searchQuery    The search query used to find the song.
     * @param searchCallback A callback to handle the list of songs found.
     */
    public void findSong(String searchQuery, AddEditMusicDialogFragment.SearchResultsCallback searchCallback) {
        Call<TracksResponse> tracksCall = SpotifyAPIClient.getInstance().searchTracks(
                prefsHelper.getAccessToken(),
                searchQuery
        );

        tracksCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TracksResponse> call, @NonNull Response<TracksResponse> response) {
                if(response.isSuccessful() && null != response.body()) {
                    TracksResponse tracksResponse = response.body();
                    ArrayList<Track> tracks = tracksResponse.getTracks();

                    searchCallback.onTracksFound(tracks); // TODO: handle case where no tracks are found
                } else {
                    try {
                        Log.d("SpotifyAPIManager", "Error getting tracks: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("SpotifyAPIManager", "IOException: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TracksResponse> call, @NonNull Throwable t) {
                Log.e("SpotifyAPIManager", "Error getting tracks: " + Objects.requireNonNull(t.getMessage()));
            }
        });

    }

    /**
     * Initiates the user authentication process using the provided authorization request.
     * <p>
     * This method sends the authorization request to the Spotify API, triggering the
     * OAuth 2.0 authorization code grant flow with PKCE (Proof Key for Code Exchange).
     */
    public void login() { //TODO: research OAuth flow to better understand process
        if(!MainActivity.authFlowStarted()) {
            String codeChallenge = generateCodeChallenge();
            prefsHelper.saveCodeChallenge(codeChallenge);

            // Construct authorization URL
            String scopesString = String.join("%20", SCOPES);
            String authorizationUrl = String.format(
                    "https://accounts.spotify.com/authorize/" +
                            "?client_id=%s" +
                            "&response_type=code" +
                            "&redirect_uri=%s" +
                            "&code_challenge_method=S256" +
                            "&code_challenge=%s" +
                            "&scope=%s",
                    CLIENT_ID,
                    ENCODED_REDIRECT_URI,
                    prefsHelper.getCodeChallenge(),
                    scopesString
            );

            MainActivity.setAuthFlowStarted(true);
            Uri uri = Uri.parse(authorizationUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                // begin authorization flow in browser
                activity.startActivity(intent);
                prefsHelper.saveRedirectUri(ENCODED_REDIRECT_URI);
                prefsHelper.showAllPreferences();
            } catch (ActivityNotFoundException e) {
                Log.e("SpotifyAPIManager", "No activity found to handle intent", e);
            }
        }
    }

    /**
     * Saves the access token using an authorization code obtained from the authorization response.
     * <p>
     * The access token grants the app access to the the endpoints defined by the authorization request.
     *
     * @param authCode  The code provided by the response of the authorization request.
     */
    public void getAccessToken(String authCode) {
        // make request to api for the access token
        Call<AccessToken> tokenCall = SpotifyAPIClient.getInstance().getAccessToken(
                "authorization_code",
                authCode,
                REDIRECT_URI,
                CLIENT_ID,
                prefsHelper.getCodeVerifier()
        );

        tokenCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if(response.isSuccessful() && null != response.body()) {
                    AccessToken accessToken = response.body();

                    // save tokens and expiration time
                    prefsHelper.saveAccessToken(accessToken.getAccessToken());
                    prefsHelper.setRefreshToken(accessToken.getRefreshToken());
                    prefsHelper.setExpirationTime(
                            calculateExpirationTime(accessToken.getExpiresIn())

                    );
                    prefsHelper.showAllPreferences();

                    Log.d("SpotifyAPIManager", "Successfully retrieved access token");
                } else {
                    try {
                        Log.d("SpotifyAPIManager", "Token exchange failed: " + response.errorBody().string() + ". Code: " + response.code());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Log.e("SpotifyAPIManager", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    /**
     * Refreshes the access token using the refresh token.
     * <p>
     * The refresh token is generated with the access token that is returned when user authentication
     * is successful - it allows the app to obtain a new access token without requiring the user
     * to log in again.
     */
    public void refreshAccessToken() { //TODO: debug here
        String refreshToken = prefsHelper.getRefreshToken();
        Log.d("SpotifyAPIManager", "Refresh token: " + refreshToken);
        Call<RefreshToken> tokenCall = SpotifyAPIClient.getInstance().refreshAccessToken(
                "refresh_token",
                refreshToken,
                CLIENT_ID
        );

        tokenCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RefreshToken> call, @NonNull Response<RefreshToken> response) {
                if(response.isSuccessful() && null != response.body()) {
                    RefreshToken newToken = response.body();

                    // update access token
                    prefsHelper.saveAccessToken(newToken.getAccessToken());
                    prefsHelper.setExpirationTime(
                            calculateExpirationTime(newToken.getExpiresIn()) // date is not being saved
                    );

                    Log.d("SpotifyAPIManager", "Successfully refreshed access token");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RefreshToken> call, @NonNull Throwable t) {
                Log.e("SpotifyAPIManager", "Token refresh failed: " + Objects.requireNonNull(t.getMessage()));
                //login();
            }
        });
    }

    /**
     * Checks if the access token has expired.
     *
     * @return Whether or not the access token has expired.
     */
    public boolean accessTokenExpired() {
        if(prefsHelper.getTokenExpirationTime().equals("undefined")) {
            return false;
        } else {
            LocalDateTime expirationTime = LocalDateTime.parse(prefsHelper.getTokenExpirationTime(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime currentTime = LocalDateTime.now();
            Log.d("SpotifyAPIManager", "Expired: " + currentTime.isAfter(expirationTime));
            return currentTime.isAfter(expirationTime); // there's still an issue with time generation. troubleshoot this
        }
    }

    /**
     * Calculates the expiration time of an access token.
     *
     * @param accessTokenLifetime The lifetime of the access token in milliseconds.
     *
     * @return  The expiration time of the access token.
     */
    private String calculateExpirationTime(long accessTokenLifetime) { //TODO: calculate proper expiration time
        LocalDateTime currentDateTime = LocalDateTime.now();

        Log.d("SpotifyAPIManager", "Current date/time: " + currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        int timeInMinutes = (int) (accessTokenLifetime / 60);
        int delay = 5;
        LocalDateTime expirationDateTime = currentDateTime.plusMinutes(timeInMinutes - delay);

        String expirationString = expirationDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Log.d("SpotifyAPIManager", "Expiration date/time: " + expirationString);
        return expirationString;
    }

    /**
     * Generates a code challenge for PKCE (Proof Key for Code Exchange).
     * <p>
     * The code challenge is sent in the authorization request to the Spotify API, where it is saved.
     * When the request for the access token is sent, we pass in the code verifier that generated the
     * code challenge. Spotify then encodes the code verifier and verifies that it matches the code
     * challenge that was previously sent before it can release the access token.
     *
     * @return  An encoding of the code verifier.
     */
    private String generateCodeChallenge() {
        String codeVerifier = generateCodeVerifier();
        prefsHelper.saveCodeVerifier(codeVerifier);

        // encrypt code verifier
        MessageDigest digest;
        String codeChallenge = "";

        try {
            digest = MessageDigest.getInstance("SHA-256");
            codeChallenge = encodeToString(digest.digest(codeVerifier.getBytes(
                    StandardCharsets.UTF_8)),
                    URL_SAFE | NO_WRAP | NO_PADDING);
        } catch (NoSuchAlgorithmException e) {
            Log.d("SpotifyAPIManager", Objects.requireNonNull(e.getMessage()));
        }

        return codeChallenge;
    }

    /**
     * Generates a random string used for PKCE (Proof Key for Code Exchange).
     * <p>
     * The code verifier is used to generate the code challenge - an encoding of the code verifier.
     * See {@link #generateCodeChallenge()}.
     * @return  A randomly generated string of 64 characters.
     */
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] codeVerifier = new byte[64];
        secureRandom.nextBytes(codeVerifier); // generate a random string of bytes

        return encodeToString(codeVerifier, URL_SAFE | NO_WRAP | NO_PADDING);
    }
}
