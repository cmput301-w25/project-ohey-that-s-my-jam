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

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.R;

import java.util.ArrayList;
import java.util.Arrays;
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

                ArrayList<Integer> chips = new ArrayList<>(
                        Arrays.asList(R.id.anger_chip, R.id.confuse_chip, R.id.shame_chip, R.id.surprise_chip,
                                R.id.disgust_chip, R.id.fear_chip,
                                R.id.happy_chip, R.id.sad_chip));

                ArrayList<EmotionalState> emotionalStates = new ArrayList<>(
                        Arrays.asList(EmotionalState.Anger, EmotionalState.Confuse, EmotionalState.Shame,
                                EmotionalState.Surprise, EmotionalState.Disgust, EmotionalState.Fear,
                                EmotionalState.Happy, EmotionalState.Sad)
                );

                for(int i = 0; i < chips.size(); i++) {
                    if(checkedIds.contains(chips.get(i))) {
                        checkedEmotionalStates.add(emotionalStates.get(i));
                    }
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
