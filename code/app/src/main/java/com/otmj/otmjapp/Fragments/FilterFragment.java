package com.otmj.otmjapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import com.otmj.otmjapp.Helper.FilterOptions;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.R;

import java.util.List;
import java.util.Map;

public class FilterFragment extends DialogFragment {
    public interface FilterListener {
        void filterOptions(FilterOptions filterOptions);
    }

    private final FilterListener listener;
    private FilterOptions filterOptions;
    private final Map<Integer, EmotionalState> idToEmotionalState = Map.of(
            R.id.anger_chip, EmotionalState.Anger,
            R.id.confuse_chip, EmotionalState.Confuse,
            R.id.disgust_chip, EmotionalState.Disgust,
            R.id.fear_chip, EmotionalState.Fear,
            R.id.sad_chip, EmotionalState.Sad,
            R.id.shame_chip, EmotionalState.Shame,
            R.id.happy_chip, EmotionalState.Happy,
            R.id.surprise_chip, EmotionalState.Surprise
    );

    public FilterFragment(FilterOptions filterOptions, FilterListener listener) {
        this.filterOptions = filterOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.filter_fragment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        ChipGroup chipGroup = view.findViewById(R.id.filter_chips);
        TextInputEditText reasonEditText = view.findViewById(R.id.filter_reason_text);

        if (filterOptions != null) {
            setChips(chipGroup);
            reasonEditText.setText(filterOptions.getReasonText());
        } else {
            filterOptions = new FilterOptions();
        }

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filterOptions.setLast7Days(checkedIds.contains(R.id.recent_week_chip));
            filterOptions.resetEmotionalStates();
            if (!checkedIds.isEmpty()) {
                // Handle emotional states
                for (int id : checkedIds) {
                    // Recent week chip is already handled above
                    if (id == R.id.recent_week_chip) {
                        continue;
                    }
                    filterOptions.addEmotionalState(idToEmotionalState.get(id));
                }
            }
        });

        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filterOptions.setReasonText(editable.toString().trim());
            }
        });

        ImageButton closeButton = view.findViewById(R.id.close_fragment_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    private void setChips(ChipGroup chipGroup) {
        List<EmotionalState> selectedEmotionalStates = filterOptions.getEmotionalStates();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            // Handle recent week chip differently
            if (chip.getId() == R.id.recent_week_chip) {
                chip.setChecked(filterOptions.getLast7Days());
            } else {
                // The id of the chip is already in the "idToEmotionalState" map
                EmotionalState correspondingEmotionalState = idToEmotionalState.get(chip.getId());
                // If the "selectedEmotionalStates" contains the emotional state corresponding to the chip
                if (selectedEmotionalStates.contains(correspondingEmotionalState)) {
                    chip.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.filterOptions(filterOptions);
    }
}
