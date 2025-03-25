package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.API.Auth.SpotifySearchClient;
import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.R;

import java.util.ArrayList;
import java.util.Objects;

public class AddEditMusicDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.add_music, null);

        SearchView searchView = view.findViewById(R.id.find_song_input_box);
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    String query = searchView.getQuery().toString();
                    SpotifySearchClient.getSearchResults(query, new SpotifySearchClient.SpotifySearchClientListener() {
                        @Override
                        public void onSuccess(ArrayList<Track> results) {
                            Log.d("AddEditMusicDialogFragment", results.toString());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("AddEditMusicDialogFragment", Objects.requireNonNull(t.getMessage()));
                        }
                    });

                    return true;
                }

                return false;
            }
        });
        TextInputLayout reasonWhy = view.findViewById(R.id.vertical_layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        return dialog;
    }
}
