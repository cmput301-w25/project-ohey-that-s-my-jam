package com.otmj.otmjapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;

import java.util.List;
import java.util.Map;

public class MoodEventAddEditDialogFragment extends DialogFragment {
    private EmotionalState selectedEmotionalState = null;
    private SocialSituation selectedSocialSituation;
    private MoodEvent moodEvent;
    private Map<String, SocialSituation> socialSituationMapping;

    // Will be implemented in project part 4
    // private String ImageLink
    // private boolean addLocation

    public static MoodEventAddEditDialogFragment newInstance(MoodEvent moodEvent) {
        Bundle args = new Bundle();
        args.putSerializable("moodEvent", moodEvent);

        MoodEventAddEditDialogFragment fragment = new MoodEventAddEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.create_mood_event, null);

        socialSituationMapping = Map.of(
            getString(R.string.alone_text), SocialSituation.Alone,
            getString(R.string.with_one_person_text), SocialSituation.With_1_Other,
            getString(R.string.with_more_than_two_people_text), SocialSituation.With_2_Others,
            getString(R.string.crowd_text), SocialSituation.With_A_Crowd
        );

        // Initialize everything in the fragment
        TextInputLayout reasonWhyInputLayout = view.findViewById(R.id.reason_why_input_box),
                triggerInputLayout = view.findViewById(R.id.trigger_input_box);
        TextInputEditText reasonWhyInputText = view.findViewById(R.id.reason_why_edit_text),
                triggerInputText = view.findViewById(R.id.trigger_edit_text);
        ChipGroup moodChipGroup = view.findViewById(R.id.emotional_state_chip_group),
                socialSituationChipGroup = view.findViewById(R.id.social_situation_chip_group);

        ImageButton closeFragmentButton = view.findViewById(R.id.ExitCreateMoodEvent);
        Button submitPostButton = view.findViewById(R.id.SubmitPostButton);

        // Retrieve arguments for editing existing event
        String tag = getTag();
        Bundle bundle = getArguments();

        if (tag != null && tag.equals("edit") && bundle != null) {
            moodEvent = (MoodEvent) bundle.getSerializable("moodEvent");
            if (moodEvent != null) {
                setSelectedChip(moodChipGroup, moodEvent.getEmotionalState());
                if (moodEvent.getReason() != null) {
                    reasonWhyInputText.setText(moodEvent.getReason());
                }
                if (moodEvent.getTrigger() != null) {
                    triggerInputText.setText(moodEvent.getTrigger());
                }
                if (moodEvent.getSocialSituation() != null) {
                    setSelectedChip(socialSituationChipGroup, moodEvent.getSocialSituation());
                }
                // setting boolean location and string imageLink will be added in project part 4
            }
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
//                if (textInputEditText.getText().toString().isBlank()) {
//                    textInputLayout.setError("This field cannot be empty");
//                }
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

        // social situation selection (optional)
        socialSituationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                String chipText = chip.getText().toString();

                setSocialSituation(chipText);
            }
        });

        // close button
        closeFragmentButton.setOnClickListener(v -> dismiss());

        // submit post button
        submitPostButton.setOnClickListener(v -> {
            String reason = reasonWhyInputText.getText().toString().trim();
            String trigger = triggerInputText.getText().toString().trim();

            setupMoodEvent(reason, trigger);
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        dialog.setOnShowListener(d -> {
            submitPostButton.setEnabled(false);
            moodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                submitPostButton.setEnabled(!checkedIds.isEmpty());

                if (!checkedIds.isEmpty()) {
                    Chip selectedChip = group.findViewById(checkedIds.get(0));
                    String moodText = selectedChip.getText().toString();

                    setEmotionalState(moodText);
                }
            });
        });

        return dialog;
    }

    private void setupMoodEvent(String reason, String trigger) {
        if (selectedEmotionalState == null) {
            Toast.makeText(getContext(),
                            "Please select an emotional state",
                            Toast.LENGTH_SHORT)
                    .show();
        }

        User user = UserManager.getInstance().getCurrentUser();
        MoodEventController moodEventController =
                new MoodEventController(List.of(user.getID()));

        if (moodEvent != null) {
            moodEvent.setEmotionalState(selectedEmotionalState);
            moodEvent.setReason(reason);
            moodEvent.setTrigger(trigger);
            moodEvent.setSocialSituation(selectedSocialSituation);

            moodEventController.updateMoodEvent(moodEvent);
            Log.d("moodevent", moodEvent.toMap().toString());
        } else {
            moodEventController.addMoodEvent(new MoodEvent(
                    user.getID(),
                    selectedEmotionalState,
                    trigger,
                    selectedSocialSituation,
                    true,
                    reason,
                    null
            ));
        }
    }

    private void setEmotionalState(String emotionalState) {
        selectedEmotionalState = EmotionalState.fromString(emotionalState);
        Log.d("moodevent", selectedEmotionalState.toString());
    }

    private void setSocialSituation(String text) {
        selectedSocialSituation = socialSituationMapping.get(text);
        Log.d("moodevent", selectedSocialSituation.toString());
    }

    private void setSelectedChip(ChipGroup chipGroup, EmotionalState emotionalState) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            String chipText = chip.getText().toString();
            if (EmotionalState.fromString(chipText).color == emotionalState.color) {
                chip.setChecked(true);
                return;
            }
        }
    }

    private void setSelectedChip(ChipGroup chipGroup, SocialSituation socialSituation) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            String chipText = chip.getText().toString();
            if (socialSituationMapping.get(chipText) == socialSituation) {
                chip.setChecked(true);
                return;
            }
        }
    }
}
