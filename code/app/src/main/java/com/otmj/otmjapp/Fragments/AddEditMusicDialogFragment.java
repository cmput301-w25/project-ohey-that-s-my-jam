package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.R;

public class AddEditMusicDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.add_music, null);

        TextInputLayout searchQuery = view.findViewById(R.id.find_song_input_box);
        TextInputLayout reasonWhy = view.findViewById(R.id.reason_why_song);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        return dialog;
    }
}
