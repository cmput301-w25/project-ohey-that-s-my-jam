package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.otmj.otmjapp.API.Auth.SharedPreferencesHelper;
import com.otmj.otmjapp.API.Auth.SpotifyAPIManager;
import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.Adapters.TrackListAdapter;
import com.otmj.otmjapp.MainActivity;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

public class AddEditMusicDialogFragment extends DialogFragment {
    private SpotifyAPIManager authManager;
    public interface SearchResultsCallback {
        void onTracksFound(ArrayList<Track> tracks);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.add_music_event, null);

        SearchView searchView = view.findViewById(R.id.search_song);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        authManager = new SpotifyAPIManager((MainActivity) requireActivity());
                        SharedPreferencesHelper prefsHelper = SharedPreferencesHelper.getInstance();

                        prefsHelper.showAllPreferences();
                        if(prefsHelper.accessTokenExists()) {
                            if(authManager.accessTokenExpired()) {
                                Log.d("AddEditMusicDialogFragment", "Access token expired");
                                authManager.refreshAccessToken();
                            }

                            Log.d("AddEditMusicDialogFragment", "Searching for song: " + s);
                            authManager.findSong(s, tracks -> {
                                ListView trackListView = view.findViewById(R.id.search_results);
                                TrackListAdapter trackListAdapter = new TrackListAdapter(requireActivity(), tracks);
                                trackListView.setAdapter(trackListAdapter);
                            });
                        } else {
                            Log.d("AddEditMusicDialogFragment", "Access token is invalid, logging in...");
                            authManager.login();
                        }

                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                }
        );

        TextInputEditText reasonWhy = view.findViewById(R.id.reason_for_song);

        ImageView closeButton = view.findViewById(R.id.ExitCreateMoodEvent);
        closeButton.setOnClickListener(v -> dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        return dialog;
    }
}

