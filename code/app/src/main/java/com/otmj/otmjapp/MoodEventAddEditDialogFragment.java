package com.otmj.otmjapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import com.otmj.otmjapp.Controllers.MoodEventController;
import com.otmj.otmjapp.Models.MoodEvent;

import java.util.HashMap;
import java.util.Map;

public class MoodEventAddEditDialogFragment extends DialogFragment {
    private TextInputEditText reasonWhyInputText, triggerInputText;
    private TextInputLayout reasonWhyInputLayout, triggerInputLayout;
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
        ImageButton closeFragmentButton = view.findViewById(R.id.ExitCreateMoodEvent);
        Button submitPostButton = view.findViewById(R.id.SubmitPostButton);

        // Retrieve arguments for editing existing event
        String tag = getTag();
        Bundle bundle = getArguments();
        if (tag != null && tag.equals("Mood Event Details") && bundle != null) {
            moodEvent = (MoodEvent) bundle.getSerializable("Mood Event");

            if (moodEvent != null) {
                selectedEmotionalState = moodEvent.getEmotionalState().color;
                setSelectedChip(selectedEmotionalState);
                reasonWhyInputText.setText(moodEvent.getReason());
                if (moodEvent.getTrigger() != null) {
                    triggerInputText.setText(moodEvent.getTrigger());
                }
                if (moodEvent.getSocialSituation() != null) {
                    setSelectedSocialSituationChip(moodEvent.getSocialSituation());
                }
                // setting boolean location and string imageLink will be added in project part 4
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

        // mood chip selection
        moodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int selectedChipId = checkedIds.get(0);
                Chip selectedChip = view.findViewById(selectedChipId);
                if (selectedChip != null) {
                    String moodText = selectedChip.getText().toString();
                    selectedEmotionalState = getColorFromMoodText(getContext(), moodText);
                }
            }
        });

        // social situation selection (optional)
        socialSituationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedSocialSituation = checkedIds.isEmpty() ? null :
                    ((Chip) view.findViewById(checkedIds.get(0))).getText().toString();
        });

        // close button
        closeFragmentButton.setOnClickListener(v -> dismiss());

        // submit post button
        submitPostButton.setOnClickListener(v -> {
            if (!validInput()) return;

            String reasonWhy = reasonWhyInputText.getText().toString().trim();
            String trigger = triggerInputText.getText().toString().trim();

            if (moodEvent != null) {
                moodEvent.setEmotionalState(selectedEmotionalState);
                moodEvent.setReason(reasonWhy);
                moodEvent.setTrigger(trigger);
                moodEvent.setSocialSituation(selectedSocialSituation);
                // moodEventController.updateMoodEvent(moodEvent);
                // I don't think editing/updating a mood event is implemented yet in the controller
            } else {
                // I haven't figured out the logic to find the userID yet
                // includeLocation is set to false by default
                // imageLink is set to null by default
                MoodEvent newMoodEvent = new MoodEvent("", selectedEmotionalState, trigger, selectedSocialSituation, false, reasonWhy, null);
                moodEventController.addMoodEvent(newMoodEvent);
            }

            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        dialog.setOnShowListener(d -> {
            submitPostButton.setEnabled(false);
            moodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                submitPostButton.setEnabled(!checkedIds.isEmpty());
            });
        });

        return dialog;
    }

    private boolean validInput() {
        if (moodChipGroup.getCheckedChipIds().isEmpty()) {
            Toast.makeText(getContext(), "Please select an emotional state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (reasonWhyInputText.getText().toString().trim().isEmpty()) {
            reasonWhyInputText.setError("Reason cannot be empty");
            return false;
        }
        return true;
    }

    private void setSelectedChip(int color) {
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

