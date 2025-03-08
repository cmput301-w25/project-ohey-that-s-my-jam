package com.otmj.otmjapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.Controllers.MoodEventController;
import com.otmj.otmjapp.Models.MoodEvent;

import java.util.HashMap;
import java.util.Map;

public class MoodEventAddEditDialogFragment extends DialogFragment {
    private TextInputEditText reasonWhyInputText, triggerInputText;
    private TextInputLayout reasonWhyInputLayout, triggerInputLayout;
    private TextView selectMoodStatus;
    private ChipGroup moodChipGroup, socialSituationChipGroup;
    private int selectedEmotionalState;
    private String selectedSocialSituation;

    private MoodEventController moodEventController;

    private MoodEvent moodEvent;

    // Will be implemented in project part 4
    // private String ImageLink
    // private boolean addLocation

    // Mapping of moods to color resource IDs in colors.xml
    private final Map<String, Integer> moodColorMap = new HashMap<String, Integer>() {{
        put("ANGER", R.color.anger);
        put("FEAR", R.color.fear);
        put("SURPRISE", R.color.surprise);
        put("SHAME", R.color.shame);
        put("HAPPY", R.color.happy);
        put("DISGUST", R.color.disgust);
        put("SAD", R.color.sad);
        put("CONFUSE", R.color.confuse);
    }};

    public static MoodEventAddEditDialogFragment newInstance(MoodEvent moodEvent) {
        Bundle args = new Bundle();
        args.putSerializable("Mood Event", moodEvent);

        MoodEventAddEditDialogFragment fragment = new MoodEventAddEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.create_mood_event, null);

        // Initialize everything in the fragment
        reasonWhyInputLayout = view.findViewById(R.id.reason_why_input_box);
        reasonWhyInputText = view.findViewById(R.id.reason_why_edit_text);
        triggerInputLayout = view.findViewById(R.id.trigger_input_box);
        triggerInputText = view.findViewById(R.id.trigger_edit_text);
        moodChipGroup = view.findViewById(R.id.emotional_state_chip_group);
        socialSituationChipGroup = view.findViewById(R.id.social_situation_chip_group);
        selectMoodStatus = view.findViewById(R.id.selected_chip_text);
        ImageButton closeFragmentButton = view.findViewById(R.id.ExitCreateMoodEvent);
        Button submitPostButton = view.findViewById(R.id.SubmitPostButton);

        // Retrieve arguments for editing existing event
        String tag = getTag();
        Bundle bundle = getArguments();
        if (tag != null && tag.equals("Mood Event Details") && bundle != null) {
            moodEvent = (MoodEvent) bundle.getSerializable("Mood Event");

            if (moodEvent != null) {
                selectedEmotionalState = moodEvent.getEmotionalState().color;
                setSelectedMoodChip(selectedEmotionalState);
                reasonWhyInputText.setText(moodEvent.getReason());
                if (moodEvent.getTrigger() != null) {
                    triggerInputText.setText(moodEvent.getTrigger());
                }
                if (moodEvent.getSocialSituation() != null) {
                    setSelectedSocialSituationChip(moodEvent.getSocialSituation());
                }
                // setting boolean location and
                // string imageLink will be added in project part 4
            }
        } else {
            moodEvent = null; // new mood event
        }

        // Input validation for "Reason Why"
        reasonWhyInputText.addTextChangedListener(new TextValidator(reasonWhyInputText, reasonWhyInputLayout) {
            @Override
            public void validateWhileTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                String input = textInputEditText.getText().toString().trim();
                if (input.length() > 20) {
                    textInputLayout.setError("Max 20 characters");
                } else if (input.split("\\s+").length > 3) {
                    textInputLayout.setError("Max 3 words");
                } else {
                    textInputLayout.setError(null);
                }
            }
            @Override
            public void validateAfterTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                if (textInputEditText.getText().toString().trim().isEmpty()) {
                    textInputLayout.setError("Reason Why Cannot be empty");
                }
            }
        });

        // Input validation for "Trigger"
        triggerInputText.addTextChangedListener(new TextValidator(triggerInputText, triggerInputLayout) {
            @Override
            public void validateWhileTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                if (textInputEditText.getText().toString().contains(" ")) {
                    textInputLayout.setError("Max 1 word");
                } else {
                    textInputLayout.setError(null);
                }
            }
            @Override
            public void validateAfterTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                // Not needed as social trigger is optional
            }
        });

        moodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                moodChipGroup.setBackgroundColor(Color.TRANSPARENT);
                int selectedChipId = moodChipGroup.getCheckedChipId();

                Chip selectedChip = group.findViewById(selectedChipId);
                if (selectedChip != null) {
                    String moodText = selectedChip.getText().toString();
                    selectMoodStatus.setText(String.format("✔ %s", moodText));
                    selectMoodStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.chip_selected));
                }
            } else {
                String moodSelectionRequired = "Please select a mood";
                selectMoodStatus.setText(moodSelectionRequired);
                selectMoodStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.chip_selection_required));
            }
        });

        // social situation selection (optional)
        socialSituationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedSocialSituation = checkedIds.isEmpty() ? null :
                    ((Chip) view.findViewById(socialSituationChipGroup.getCheckedChipId()))
                                                .getText().toString();
        });

        // close button
        closeFragmentButton.setOnClickListener(v -> dismiss());

        // submit post button
        submitPostButton.setOnClickListener(v -> {
            if (!validInput()) return;
            String reasonWhy = reasonWhyInputText.getText().toString().trim();
            String trigger;
            if (triggerInputText.getText() != null) {
                trigger = triggerInputText.getText().toString().trim();
            } else {
                trigger = null;
            }

            if (moodEvent != null) {
                submitPostButton.setText(R.string.edit_button_text);
                moodEvent.setEmotionalState(selectedEmotionalState);
                moodEvent.setReason(reasonWhy);
                if (trigger != null) {
                    moodEvent.setTrigger(trigger);
                }
                if (selectedSocialSituation != null) {
                    moodEvent.setSocialSituation(selectedSocialSituation);
                }
                // moodEventController.updateMoodEvent(moodEvent);
                // I don't think editing/updating a mood event is implemented yet in the controller
            } else {
                // I haven't figured out the logic to find the userID yet
                // includeLocation is set to false by default
                // imageLink is set to null by default
                MoodEvent newMoodEvent = new MoodEvent("", selectedEmotionalState,
                                                        trigger, selectedSocialSituation,
                                                        false, reasonWhy, null);
                moodEventController.addMoodEvent(newMoodEvent);
            }

            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        dialog.show();

        return dialog;
    }

    private boolean validInput() {
        if (moodChipGroup.getCheckedChipIds().isEmpty()) {
            moodChipGroup.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.error_background));
            String moodSelectionRequired = "Please select a mood";
            selectMoodStatus.setText(moodSelectionRequired);
            selectMoodStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.chip_selection_required));
            return false;
        }
        if (reasonWhyInputText.getText().toString().trim().isEmpty()) {
            reasonWhyInputLayout.setError("Reason cannot be empty");
            return false;
        }
        return true;
    }

    private void setSelectedMoodChip(int color) {
        for (int i = 0; i < moodChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) moodChipGroup.getChildAt(i);
            if (getColorFromMoodText(getContext(), chip.getText().toString()) == color) {
                chip.setChecked(true);
                return;
            }
        }
    }

    private int getColorFromMoodText(Context context, String moodText) {
        return moodColorMap.getOrDefault(moodText, R.color.black);
    }

    private void setSelectedSocialSituationChip(String socialSituation) {
        for (int i = 0; i < socialSituationChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) socialSituationChipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(socialSituation)) {
                chip.setChecked(true);
                break;
            }
        }
    }
}

