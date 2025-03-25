package com.otmj.otmjapp.API.Auth;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.API.Models.Track;

import java.util.ArrayList;
import java.util.Objects;

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

    private static final SpotifySearchClient instance = new SpotifySearchClient();
    private final SpotifySearchService client;
    private ArrayList<Track> results;

    private SpotifySearchClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client = retrofit.create(SpotifySearchService.class);
    }

    public void getSearchResults(String query, SpotifySearchClientListener listener) {
        Call<ArrayList<Track>> result = client.searchTracks(query, "track", 15);
        result.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Track>> call, @NonNull Response<ArrayList<Track>> response) {
                if(response.isSuccessful()) {
                    ArrayList<Track> tracks = response.body();
                    results.addAll(Objects.requireNonNull(tracks));
                    listener.onSuccess(results);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Track>> call, @NonNull Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public static SpotifySearchClient getInstance() {
        return instance;
    }

    public ArrayList<Track> getResults() {
        return results;
    }

    public void setResults(ArrayList<Track> results) {
        this.results = results;
    }
}
