package com.otmj.otmjapp.API.Auth;

import com.otmj.otmjapp.API.Models.AccessToken;
import com.otmj.otmjapp.API.Models.RefreshToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Defines the API contract for interacting with the Spotify Authentication Service.
 */
public interface SpotifyAuthService {
    /**
     * Exchanges an authorization code for an access token using the PKCE (Proof Key for Code Exchange)
     * flow.
     *
     * @param grantType    Specifies the type of authorization flow being used
     * @param authCode     Represents user's consent to access their data
     * @param redirectUri  This parameter is used for validation only (there is no actual redirection).
     * @param clientID     The client id of the Spotify Web Application
     * @param codeVerifier A (securely) random string used for PKCE (Proof Key for Code Exchange)
     *                     to verify the authorization request.
     *
     * @return A Call object that represents a request for an access token
     */
     @Headers("Content-Type: application/x-www-form-urlencoded")
     @FormUrlEncoded
     @POST("/api/token")
     Call<AccessToken> getAccessToken(
             @Field("grant_type") String grantType,
             @Field("code") String authCode,
             @Field("redirect_uri") String redirectUri,
             @Field("client_id") String clientID,
             @Field("code_verifier") String codeVerifier
     );

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param grantType    Specifies the type of authorization flow being used
     * @param refreshToken Allows the refresh of an access token
     * @param clientID     The client id of the Spotify Web Application
     *
     * @return A Call object that represents a request for the access token to be refreshed
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("/token")
    Call<RefreshToken> refreshAccessToken(
            @Field("grant_type") String grantType,
            @Field("refresh_token") String refreshToken,
            @Field("client_id") String clientID
    );

}