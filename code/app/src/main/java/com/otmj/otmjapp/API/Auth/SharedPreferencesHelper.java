package com.otmj.otmjapp.API.Auth;

import android.content.SharedPreferences;
import android.util.Log;

import com.otmj.otmjapp.MainActivity;

import java.util.Map;
import java.util.Objects;

/**
 * Stores and retrieves the access token and its expiration time.
 */
public class SharedPreferencesHelper {
    private final String ACCESS_TOKEN_KEY = "access_token";
    private final String REFRESH_TOKEN_KEY = "refresh_token";
    private final String EXPIRATION_TIME_KEY = "expiration_time";
    private final String CODE_VERIFIER_KEY = "code_verifier";
    private final String CODE_CHALLENGE_KEY = "code_challenge";
    private final String EXPIRATION_DATE_KEY = "expiration_date";
    private final String REDIRECT_URI_KEY = "redirect_uri";
    private static SharedPreferencesHelper instance;
    private final SharedPreferences preferences;

    private SharedPreferencesHelper() {
        instance = this;
        preferences = MainActivity.getSharedPrefs();
    }

    public String getAccessToken() {

        return preferences.getString(ACCESS_TOKEN_KEY, "undefined");
    }

    public void saveAccessToken(String accessToken) {
        preferences.edit()
                .putString(ACCESS_TOKEN_KEY, accessToken)
                .apply();
    }

    public boolean accessTokenExists() {
        return !preferences.getString(ACCESS_TOKEN_KEY, "undefined").equals("undefined");
    }

    public void saveRedirectUri(String redirectUri) {
        preferences.edit()
                .putString(REDIRECT_URI_KEY, redirectUri)
                .apply();
    }

    public String getCodeChallenge() {
        return preferences.getString(CODE_CHALLENGE_KEY, "undefined");
    }

    public void saveCodeChallenge(String codeChallenge) {
        preferences.edit()
                .putString(CODE_CHALLENGE_KEY, codeChallenge)
                .apply();
    }

    public String getCodeVerifier() {
        return preferences.getString(CODE_VERIFIER_KEY, "undefined");
    }

    public void saveCodeVerifier(String codeVerifier) {
        preferences.edit()
                .putString(CODE_VERIFIER_KEY, codeVerifier)
                .apply();
    }

    public String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN_KEY, "undefined");
    }

    public void setRefreshToken(String refreshToken) {
        preferences.edit()
                .putString(REFRESH_TOKEN_KEY, refreshToken)
                .apply();
    }

    public String getExpirationDate() {
        return preferences.getString(EXPIRATION_DATE_KEY, "undefined");
    }

    public long getTokenExpirationTime() {
        return preferences.getLong(EXPIRATION_TIME_KEY, 0);
    }

    public void setExpirationTime(String expirationTime) {
        preferences.edit()
                .putString(EXPIRATION_TIME_KEY, expirationTime)
                .apply();
    }

    public void showAllPreferences() {
        Map<String, ?> allEntries = preferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferencesHelper", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    public void clear() {
        preferences.edit()
                .clear()
                .apply();
    }

    public static SharedPreferencesHelper getInstance() {
        return Objects.requireNonNullElseGet(instance, SharedPreferencesHelper::new);
    }
}
