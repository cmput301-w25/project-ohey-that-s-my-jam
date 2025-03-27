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
import com.otmj.otmjapp.Fragments.AddEditMusicDialogFragment;
import com.otmj.otmjapp.MainActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void findSong(String songTitle, AddEditMusicDialogFragment.SearchResultsCallback searchCallback) {
        Call<ArrayList<Track>> tracksCall = SpotifyAPIClient.getInstance().searchTracks(
                prefsHelper.getAccessToken(),
                songTitle
        );
        Log.d("SpotifyAPIManager", "Access token: " + prefsHelper.getAccessToken());
        Log.d("SpotifyAPIManager", "Request URI: " + tracksCall.request().url());
        Log.d("SpotifyAPIManager", "Auth header: " + tracksCall.request().header("Authorization"));
        Log.d("SpotifyAPIManager", "Searching for song: " + songTitle);
        Log.d("SpotifyAPIManager", "Expiration time: " + prefsHelper.getExpirationDate());
        Log.d("SpotifyAPIManager", "URL: " + tracksCall.request().url().toString());
        tracksCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Track>> call, @NonNull Response<ArrayList<Track>> response) {
                if(response.isSuccessful() && null != response.body()) {
                    Log.d("SpotifyAPIManager", "Response successful: " + response.body());
                    Log.d("SpotifyAPIManager", "Successfully retrieved tracks");

                    ArrayList<Track> tracks = response.body();
                    Log.d("SpotifyAPIManager", "Tracks found: " + tracks.size());

                    searchCallback.onTracksFound(tracks);
                } else {
                    try {
                        Log.d("SpotifyAPIManager", "Error getting tracks: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("SpotifyAPIManager", "IOException: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Track>> call, @NonNull Throwable t) {
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
    public void login() { //TODO: research OAuth flow to better understand process, maybe change method signature for DI
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
            Log.d("SpotifyAPIManager", "Authorization URL: " + authorizationUrl);
            Log.d("SpotifyAPIManager", "Code verifer 1: " + prefsHelper.getCodeVerifier());
            Log.d("SpotifyAPIManager", "Auth flow started: " + MainActivity.authFlowStarted());

            MainActivity.setAuthFlowStarted(true);
            Uri uri = Uri.parse(authorizationUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri); // research what an intent is
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // research what this does

            try {
                Log.d("SpotifyAPIManager", "Activity address: " + activity.toString());
                Log.d("SpotifyAPIManager", "Intent sent: " + intent.toString());

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
        // set query parameters
        Log.d("SpotifyAPIManager", "Code verifer 2: " + prefsHelper.getCodeVerifier());
        SharedPreferencesHelper.getInstance().showAllPreferences();
        SharedPreferencesHelper.getInstance().showAllPreferences();
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
    public void refreshAccessToken() {
        String refreshToken = prefsHelper.getRefreshToken();

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
                            calculateExpirationTime(newToken.getExpiresIn())
                    );

                    Log.d("SpotifyAPIManager", "Successfully refreshed access token");
                } else {
                    Log.d("SpotifyAPIManager", "Null response body");
                    //login(); // force login if token refresh fails
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
    public boolean accessTokenExpired() {DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(prefsHelper.getExpirationDate().equals("undefined")) {
            return false;
        } else {
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime expirationDateTime = LocalDateTime.parse(prefsHelper.getExpirationDate(), formatter);
            LocalDateTime currentDateTime = LocalDateTime.now(zoneId);

            return currentDateTime.isAfter(expirationDateTime);
        }
    }

    /**
     * Formats the expiration time of an access token.
     *
     * @param accessTokenLifetime The lifetime of the access token in seconds.
     *
     * @return  A formatted string representing the expiration time of the access
     */
    private String calculateExpirationTime(long accessTokenLifetime) { //TODO: calculate proper expiration time
        Log.d("SpotifyAPIManager", "Access token lifetime: " + accessTokenLifetime + " seconds");

        return LocalDateTime.now()
                .plusMinutes(accessTokenLifetime / 60)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Generates a code challenge for PKCE (Proof Key for Code Exchange).
     * <p>
     * TODO: explain function in process
     *
     * @return  An encoding of the code verifier.
     */
    private String generateCodeChallenge() {
        String codeVerifier = generateCodeVerifier();
        prefsHelper.saveCodeVerifier(codeVerifier);
        Log.d("SpotifyAPIManager", "Code verifier saved: " + codeVerifier);
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
     * TODO: explain function in process
     *
     * @return  A randomly generated string of 64 characters.
     */
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] codeVerifier = new byte[64];
        secureRandom.nextBytes(codeVerifier); // generate random bytes

        return encodeToString(codeVerifier, URL_SAFE | NO_WRAP | NO_PADDING);
    }
}
