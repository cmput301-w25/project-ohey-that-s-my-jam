package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.TextValidator;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;

import java.util.List;
import java.util.Map;

public class FilterFragment extends DialogFragment {

//    public interface FilterListener {
//        void
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.filter_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        Button closeButton = view.findViewById(R.id.close_fragment_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        ChipGroup chipGroup = view.findViewById(R.id.filter_chips);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {

            }
        });

        return dialog;
    }
}
