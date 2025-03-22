package com.otmj.otmjapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Helper.MoodEventsManager;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SimpleLocation;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.Helper.TextValidator;
import android.Manifest;


import java.util.List;
import java.util.Map;




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
    private  Location location;
    private LocationHelper locationHelper;
    private ActivityResultLauncher<String> permissionLauncher;
    private TextView addressTextView;
    private String imageLink;


    // Will be implemented in project part 4
    // private boolean addLocation

    private final ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    long imageSize = ImageHandler.getImageSize(requireContext(), uri);
                    if (imageSize <= 65536) {
                        ImageHandler.uploadToFirebaseStorage(requireContext(), uri, new ImageHandler.UploadCallback() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                Log.d("Image Upload", "Image successfully uploaded: " + imageUrl);
                                imageLink = imageUrl;  // ✅ Set the image link
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("Image Upload", "Failed to upload image: " + e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Image size too big", Toast.LENGTH_SHORT).show();
                    }
                }
            });




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
        Button deletePostButton = view.findViewById(R.id.DeletePostButton);
        ImageView uploadImage = view.findViewById(R.id.add_image_button);
        ImageButton addLocationBottom = view.findViewById(R.id.add_location_button);
        addressTextView = view.findViewById(R.id.textview_address);

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
                if (moodEvent.getLocation() != null) {
                    getCurrentAddress(moodEvent.getLocation().toLocation());
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
            Log.d("ImageLink Check", "Current imageLink: " + imageLink);

            // Directly launch the image picker instead of calling setImageLink()
            galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());

            Log.d("ImageLink Check", "Gallery picker launched");
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

        addLocationBottom.setOnClickListener(v -> {
            addressTextView.setText("Please wait a moment");
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                getCurrentLocation();
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
            MoodEvent temp_moodEvent = new MoodEvent(
                    user.getID(),
                    selectedEmotionalState,
                    selectedSocialSituation,
                    true,
                    reason,
                    imageLink,
                    privacy
            );
            if (location != null){
                SimpleLocation temp_simpleLocation = new SimpleLocation(location.getLatitude(),location.getLongitude());
                temp_moodEvent.setLocation(temp_simpleLocation);
            }
            moodEventsManager.addMoodEvent(temp_moodEvent);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register permission launcher in your Fragment
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, now fetch location.
                        getCurrentLocation();
                    } else {
                        Log.d("Testing", "permission denied");
                        Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                        addressTextView.setText("Permission denied Please Allow\n share location in setting");
                    }
                }
        );
        locationHelper = new LocationHelper(requireActivity());
    }

    public void getCurrentLocation(){
        locationHelper = new LocationHelper(requireActivity());
        Log.d("Testing", "stage initial");
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(Location Currentlocation) {
                location = Currentlocation;
                getCurrentAddress(location);
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("Location", "Lat: " + lat + ", Lon: " + lon);
                Log.d("Testing", "stage 1");
            }

            @Override
            public void onLocationError(String error) {
                Log.e("Location", "Error: " + error);
                Log.d("Testing", "stage 2");
                Toast.makeText(getContext(), "Failed to get Location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCurrentAddress(Location location){
        locationHelper.getAddressFromLocation(location, new LocationHelper.AddressCallback() {
            @Override
            public void onAddressResult(String country, String state, String city) {
                Log.d("Address", "Address: " + city + state + country);
                addressTextView.setText("Location:\n"+ city + ", " + state + ", " + country);
            }

            @Override
            public void onAddressError(String error) {
                Log.e("Address", "Error: " + error);
            }
        });
    }
}