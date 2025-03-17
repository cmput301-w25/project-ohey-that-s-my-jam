package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.R;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends DialogFragment {

    public interface FilterListener {
        void filterOptions(boolean last7Days,
                           String text,
                           List<EmotionalState> emotionalStates);
    }

    private final FilterListener listener;

    // Filter options
    private final ArrayList<EmotionalState> checkedEmotionalStates = new ArrayList<>();
    private boolean onlyLast7Days = false;
    private String reasonText = "";

    public FilterFragment(FilterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.filter_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        ChipGroup chipGroup = view.findViewById(R.id.filter_chips);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            checkedEmotionalStates.clear();
            if (!checkedIds.isEmpty()) {
                onlyLast7Days = checkedIds.contains(R.id.recent_week_chip);
                if (checkedIds.contains(R.id.anger_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Anger);
                }
                if (checkedIds.contains(R.id.confuse_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Confuse);
                }
                if (checkedIds.contains(R.id.shame_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Shame);
                }
                if (checkedIds.contains(R.id.surprise_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Surprise);
                }
                if (checkedIds.contains(R.id.disgust_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Disgust);
                }
                if (checkedIds.contains(R.id.fear_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Fear);
                }
                if (checkedIds.contains(R.id.happy_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Happy);
                }
                if (checkedIds.contains(R.id.sad_chip)) {
                    checkedEmotionalStates.add(EmotionalState.Sad);
                }
            }
        });

        TextInputEditText reasonEditText = view.findViewById(R.id.filter_reason_text);
        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                reasonText = editable.toString().trim();
            }
        });

        ImageButton closeButton = view.findViewById(R.id.close_fragment_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        listener.filterOptions(onlyLast7Days, reasonText, checkedEmotionalStates);
    }
}
