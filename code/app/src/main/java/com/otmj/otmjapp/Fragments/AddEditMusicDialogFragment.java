package com.otmj.otmjapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.API.Auth.SharedPreferencesHelper;
import com.otmj.otmjapp.API.Auth.SpotifyAPIManager;
import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.Adapters.TrackListAdapter;
import com.otmj.otmjapp.MainActivity;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

public class AddEditMusicDialogFragment extends DialogFragment {
    /**
     * An interface that defines what happens when search results are found.
     */
    public interface SearchResultsCallback {
        /**
         * Define what to do when search results are found.
         * @param tracks A list of tracks that are returned based on the search query.
         */
        void onTracksFound(ArrayList<Track> tracks);
    }

    /**
     * An interface that defines what happens when a track is selected from the search results.
     */
    public interface onTrackSelectedListener {
        /**
         * Define what to do when a track is selected from the search results.
         * <p>
         * @param createdMusicEvent The music event created with the selected track.
         */
        void onTrackSelected(MusicEvent createdMusicEvent);
    }

    /**
     * {@link SpotifyAPIManager}
     */
    private SpotifyAPIManager authManager;
    /**
     * {@link onTrackSelectedListener}
     */
    private onTrackSelectedListener onTrackSelectedListener;
    private View view;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = getLayoutInflater().inflate(R.layout.add_music_event, null);

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
                                handleSongSelection(tracks);
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

        ImageView closeButton = view.findViewById(R.id.ExitCreateMoodEvent);
        closeButton.setOnClickListener(v -> dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        return builder.setView(view).create();
    }

    /**
     * Handles the selection of a track from the search results.
     * <P>
     * See {@link #saveMusicEvent(TrackListAdapter)}.
     * @param tracks A list of tracks that the user can select from based on their search query.
     */
    private void handleSongSelection(ArrayList<Track> tracks) {
        ListView trackListView = view.findViewById(R.id.search_results);
        TrackListAdapter trackListAdapter = new TrackListAdapter(requireActivity(), tracks);
        trackListView.setAdapter(trackListAdapter);

        Button saveButton = view.findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {
            saveMusicEvent(trackListAdapter);
        });
    }

    /**
     * Saves the user's current track selection and feeling before returning to the fragment where
     * they add/edit a mood event.
     * <p>
     * @param trackListAdapter The adapter for the track list view.
     */
    private void saveMusicEvent(TrackListAdapter trackListAdapter) {
        Track selectedTrack = trackListAdapter.getLastSelectedTrack();

        TextInputLayout inputLayout = view.findViewById(R.id.input_layout);
        String feelingText = "";

        if (inputLayout.getEditText() != null) {
            feelingText = inputLayout.getEditText().getText().toString();
        }

        boolean noFeeling = feelingText.isEmpty();

        if(null != selectedTrack && !noFeeling) {
            Log.d("AddEditMusicDialogFragment", "Creating music event with track: " + selectedTrack.getTitle());
            createMusicEvent(selectedTrack, feelingText);
            dismiss();
        } else {
            if(null == selectedTrack) {
                Toast.makeText(requireContext(), "Please select a song.", Toast.LENGTH_SHORT).show();
            }

            if(noFeeling) {
                inputLayout.setError("Please describe how the song makes you feel.");
            }
        }
    }

    /**
     * Creates a new MusicEvent with the given track and feeling.
     * <p>
     * @param track     A track that represents the user's mood.
     * @param feeling   How track makes the user feel.
     */
    public void createMusicEvent(Track track, String feeling) {
        MusicEvent musicEvent = new MusicEvent(
                track,
                feeling
        );

        Log.d("AddEditMusicDialogFragment", "Creating music event with track: " + track.getTitle());

        if(onTrackSelectedListener != null) {
            onTrackSelectedListener.onTrackSelected(musicEvent);
        }
    }

    /**
     * Sets a listener for when a track is selected from the search results.
     * <p>
     * @param listener {@link onTrackSelectedListener}
     */
    public void setOnTrackSelectedListener(onTrackSelectedListener listener) {
        this.onTrackSelectedListener = listener;
    }
}
