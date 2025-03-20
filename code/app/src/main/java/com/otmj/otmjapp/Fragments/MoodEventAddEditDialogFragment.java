package com.otmj.otmjapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.Helper.TextValidator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.type.MediaType;


/**
 * A dialog fragment for adding or editing a MoodEvent.
 * This fragment allows users to select an emotional state, provide a reason, specify a trigger,
 * and optionally select a social situation. If an existing MoodEvent is passed, it allows editing
 * the event details.
 */
public class MoodEventAddEditDialogFragment extends DialogFragment {
    private EmotionalState selectedEmotionalState;
    private SocialSituation selectedSocialSituation;
    private MoodEvent moodEvent;
    private MoodEvent.Privacy privacy;
    private Map<String, SocialSituation> socialSituationMapping;

    private String imageLink;

    // Will be implemented in project part 4
    // private boolean addLocation

    /**
     * Creates a new instance of the dialog fragment with a given MoodEvent.
     *
     * @param moodEvent The MoodEvent to be edited.
     * @return A new instance of MoodEventAddEditDialogFragment.
     */
    public static MoodEventAddEditDialogFragment newInstance(MoodEvent moodEvent) {
        Bundle args = new Bundle();
        args.putSerializable("moodEvent", moodEvent);

        MoodEventAddEditDialogFragment fragment = new MoodEventAddEditDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the dialog is created, setting up the UI elements and initializing event handlers.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The created Dialog.
     */
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

        // Initialize UI components
        TextInputLayout reasonWhyInputLayout = view.findViewById(R.id.reason_why_input_box);
        TextInputEditText reasonWhyInputText = view.findViewById(R.id.reason_why_edit_text);
        ChipGroup moodChipGroup = view.findViewById(R.id.emotional_state_chip_group),
                socialSituationChipGroup = view.findViewById(R.id.social_situation_chip_group);

        ImageButton closeFragmentButton = view.findViewById(R.id.ExitCreateMoodEvent);
        Button submitPostButton = view.findViewById(R.id.SubmitPostButton);
        ImageButton deletePostButton = view.findViewById(R.id.DeletePostButton);
        ImageView uploadImage = view.findViewById(R.id.add_image_button);

        // using SwitchCompat makes the app crash when the 'addMoodEvent' button is clicked
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch privacySwitch = view.findViewById(R.id.privacy_switch);
        // Hide delete button by default
        deletePostButton.setVisibility(View.GONE);

        // Retrieve arguments for editing existing event
        String tag = getTag();
        Bundle bundle = getArguments();

        if (tag != null && tag.equals("edit") && bundle != null) {
            moodEvent = (MoodEvent) bundle.getSerializable("moodEvent");
            if (moodEvent != null) {
                selectedEmotionalState = moodEvent.getEmotionalState();
                selectedSocialSituation = moodEvent.getSocialSituation();
                privacy = moodEvent.getPrivacy();

                submitPostButton.setText(R.string.edit_button_text);
                deletePostButton.setVisibility(View.VISIBLE); // Show delete button in edit mode

                setSelectedChip(moodChipGroup, moodEvent.getEmotionalState());
                if (moodEvent.getReason() != null) {
                    reasonWhyInputText.setText(moodEvent.getReason());
                }
                if (moodEvent.getSocialSituation() != null) {
                    setSelectedChip(socialSituationChipGroup, moodEvent.getSocialSituation());
                }
                if (moodEvent.getImageLink() != null) {
                    imageLink = moodEvent.getImageLink();
                }

                privacySwitch.setChecked(privacy == MoodEvent.Privacy.Public);
                // setting boolean location and string imageLink will be added in project part 4
            }
        }

        // Input validation for "Reason Why"
        reasonWhyInputText.addTextChangedListener(new TextValidator(reasonWhyInputText, reasonWhyInputLayout) {
            @Override
            public void validateWhileTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                String input = textInputEditText.getText().toString().trim();
                if (input.length() > 200) {
                    textInputLayout.setError("Max 200 characters");
                } else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void validateAfterTyping(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
                // not needed
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

        uploadImage.setOnClickListener(v -> {
            if (imageLink == null) {
                setImageLink();
            } else {
                imageLink = updateImageLink(imageLink);
            }
        });

        // close button
        closeFragmentButton.setOnClickListener(v -> dismiss());

        // privacy toggle
        privacySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            privacy = isChecked ? MoodEvent.Privacy.Public : MoodEvent.Privacy.Private;
        });

        // submit post button
        submitPostButton.setOnClickListener(v -> {
            String reason = reasonWhyInputText.getText().toString().trim();

            if (moodChipGroup.getCheckedChipId() != View.NO_ID
                    && reason.length() <= 200) {
                setupMoodEvent(reason);
                dismiss();
            } else if (moodChipGroup.getCheckedChipId() == View.NO_ID) {
                    Toast.makeText(getContext(),
                                "Please select an emotional state",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Delete button (Only in Edit Mode)
        deletePostButton.setOnClickListener(v -> {
            if (moodEvent != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Mood Event")
                        .setMessage("Are you sure you want to delete this mood event?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            MoodEventsManager moodEventsManager =
                                    new MoodEventsManager(List.of(UserManager.getInstance().getCurrentUser().getID()));

                            moodEventsManager.deleteMoodEvent(moodEvent);
                            dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        AlertDialog dialog = builder.setView(view).create();

        dialog.setOnShowListener(d -> {
            moodChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip selectedChip = group.findViewById(checkedIds.get(0));
                    String moodText = selectedChip.getText().toString();

                    setEmotionalState(moodText);
                }
            });
        });

        return dialog;
    }

    private void setupMoodEvent(String reason) {

        User user = UserManager.getInstance().getCurrentUser();
        MoodEventsManager moodEventsManager =
                new MoodEventsManager(List.of(user.getID()));
        Log.d("MoodEvent privacy", "setupMoodEvent:" + privacy);
        if (moodEvent != null) {
            moodEvent.setEmotionalState(selectedEmotionalState);
            moodEvent.setReason(reason);
            moodEvent.setSocialSituation(selectedSocialSituation);
            moodEvent.setPrivacy(privacy);
            moodEvent.setImageLink(imageLink);
            moodEventsManager.updateMoodEvent(moodEvent);
        } else {
            if(privacy == null) { // privacy is null by default. If user doesn't toggle switch, set it to private
                privacy = MoodEvent.Privacy.Private;
            }

            moodEventsManager.addMoodEvent(new MoodEvent(
                    user.getID(),
                    selectedEmotionalState,
                    selectedSocialSituation,
                    true,
                    reason,
                    imageLink,
                    privacy
            ));
        }
    }

    /**
     * Sets the selected emotional state based on user selection.
     *
     * @param emotionalState The emotional state as a string.
     */
    private void setEmotionalState(String emotionalState) {
        selectedEmotionalState = EmotionalState.fromString(emotionalState);
    }

    /**
     * Sets the selected social situation based on user selection.
     *
     * @param text The social situation as a string.
     */
    private void setSocialSituation(String text) {
        selectedSocialSituation = socialSituationMapping.get(text);
    }

    /**
     * Marks the correct chip in a ChipGroup based on the selected emotional state.
     *
     * @param chipGroup The ChipGroup containing emotion chips.
     * @param emotionalState The selected emotional state.
     */
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

    /**
     * Marks the correct chip in a ChipGroup based on the selected social situation.
     *
     * @param chipGroup The ChipGroup containing social situation chips.
     * @param socialSituation The selected social situation.
     */
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

    /**
     * Opens an image picker, allows the user to select an image, and uploads it to Firebase Storage.
     * Once uploaded, the image's download URL is stored in the `imageLink` variable.
     */
    private void setImageLink() {
        TedImagePicker.with(getContext())
                .title("Select an Image")
                .mediaType(MediaType.IMAGE)
                .showCameraTile(false)
                .max(1, "You can only select 1 image.")
                .startMultiImage(uriList -> {
                    if (!uriList.isEmpty()) {
                        Uri uri = uriList.get(0);
                        long imageSize = ImageHandler.getFileSize(requireContext(), uri);
                        if (imageSize <= 65536) {
                            ImageHandler.uploadImage(requireContext(), uri, new ImageHandler.UploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    imageLink = imageUrl; // Set the image URL for Firestore
                                    Log.d("Image Upload", "Image successfully uploaded: " + imageLink);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("Image Upload", "Failed to upload image: " + e.getMessage());
                                }
                            });
                        }
                        // TODO: add a toast message that image size is too big
                    }
                });
    }


    /**
     * Opens an image picker to allow the user to replace or remove an existing image in their mood event.
     * New image is uploaded to Firebase Storage.
     * Old image are deleted from Firebase Storage.
     * @param imageLink The current image URL stored in Firebase Storage.
     * @return The new image URL after upload, or `null` if no image was selected.
     * **Behavior:**
     * - If the user selects a new image, the old image is deleted, and the new URL is returned.
     * - If the upload fails, the old image remains unchanged.
     * - If the user deselected all images, detaching image from their post, `null` is returned.
     */
    private String updateImageLink(String imageLink) {
        Uri preselectedImage = Uri.parse(imageLink);
        List<Uri> updatedImage = new ArrayList<>();

        // Use a final array to hold the updated image link
        final String[] updatedImageLink = {imageLink};

        TedImagePicker.with(getContext())
                .title("Select an Image")
                .mediaType(MediaType.IMAGE)
                .showCameraTile(false)
                .max(1, "You can only select 1 image.")
                .selectedUri(Collections.singletonList(preselectedImage))
                .startMultiImage(uriList -> {
                    if (!uriList.isEmpty()) {
                        Uri selectedUri = uriList.get(0);
                        long imageSize = ImageHandler.getFileSize(requireContext(), selectedUri);
                        if (imageSize <= 65536) {
                            updatedImage.add(selectedUri);

                            // Upload the new image to Firebase
                            ImageHandler.uploadImage(requireContext(), selectedUri, new ImageHandler.UploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    // Update the array value instead of a final String variable
                                    updatedImageLink[0] = imageUrl;
                                    Log.d("Image Update", "Updated image URL: " + updatedImageLink[0]);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("Image Update", "Failed to update image: " + e.getMessage());
                                }
                            });
                        }
                    }
                    // TODO: add a toast message that image size is too big
                });

        // Delete the old image if a new one was uploaded successfully
        if (!updatedImageLink[0].equals(imageLink)) {
            ImageHandler.deleteImage(imageLink);
        }

        return updatedImage.isEmpty() ? null : updatedImageLink[0];
    }


}