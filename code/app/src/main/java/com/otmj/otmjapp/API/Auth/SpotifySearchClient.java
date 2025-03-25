package com.otmj.otmjapp.API.Auth;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.API.Models.Track;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifySearchClient {
    public interface SpotifySearchClientListener {
        void onSuccess(ArrayList<Track> results);
        void onFailure(Throwable t);
    }

    private static final Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final SpotifySearchService service = retrofit.create(SpotifySearchService.class);

    public static void getSearchResults(String query, SpotifySearchClientListener listener) {
        Call<ArrayList<Track>> result = service.searchTracks(query, "track", 15);
        result.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Track>> call, @NonNull Response<ArrayList<Track>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Track> tracks = response.body();
                    listener.onSuccess(tracks);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Track>> call, @NonNull Throwable t) {
                listener.onFailure(t);
            }
        });
    }
}
