package com.otmj.otmjapp;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.otmj.otmjapp.Models.MoodEvent;

public class MoodEventDetailsFragment extends Fragment {

    private MoodEventDetailsViewModel mViewModel;
    private TextView usernameText, eventTimestampText, combinedText, eventLocationText;
    private ImageView profileImage, reasonWhyImage, locationIcon;
    private MaterialButton unfollowButton; // will not be implemented yet
    private ImageButton closeButton;


    public static MoodEventDetailsFragment newInstance() {
        return new MoodEventDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        closeButton = view.findViewById(R.id.close_mood_event_details_fragment_button);
        profileImage = view.findViewById(R.id.profile_image);
        usernameText = view.findViewById(R.id.username);
        eventTimestampText = view.findViewById(R.id.event_timestamp);
        reasonWhyImage = view.findViewById(R.id.reason_why_image);
        combinedText = view.findViewById(R.id.combined_reason_why_trigger_and_social_situation_text_view);
        eventLocationText = view.findViewById(R.id.event_location);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(MoodEventDetailsViewModel.class);

        // Get the passed arguments using Safe Args
        MoodEventDetailsFragmentArgs args = MoodEventDetailsFragmentArgs.fromBundle(getArguments());
        MoodEvent moodEvent = args.getMoodEvent();

        if (moodEvent != null) {
            mViewModel.setMoodEvent(moodEvent);
        }

        // Observe MoodEvent and update UI
        mViewModel.getMoodEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {

                // set the username text
                // String moodEventOwnerUsername = event.user.getUsername();
                // usernameText.setText(moodEventOwnerUsername);

                // load profile image
                // Glide.with(requireContext()).load(event.user.getProfilePictureLink()).into(profileImage);

                // set time stamp text
                eventTimestampText.setText(event.getCreatedDate().toString());

                // obtain all of the available strings
                String combined;
                String emotionalStateEventText = "Feeling " + event.getEmotionalState().toString()
                                                    + " " + event.getEmotionalState().emoji;
                String reasonWhyText = " because of" + event.getReason();
                combined = emotionalStateEventText + reasonWhyText;

                String trigger = event.getTrigger();
                // If mood event has an optional trigger
                if (trigger != null) {
                    String triggerText = " triggered by" + trigger;
                    combined += triggerText;
                }

                String socialSituation = event.getSocialSituation();
                // If mood event has an optional social situation
                if (socialSituation != null) {
                    String socialSituationText;
                    if (socialSituation.equals("Alone")) {
                        socialSituationText = " while" + socialSituation;
                    } else if (socialSituation.equals("Crowd")) {
                        socialSituationText = " while in a" + socialSituation;
                    } else {
                        socialSituationText = " with" + socialSituation;
                    }
                    combined += socialSituationText;
                }

                // set all the available text of a mood event
                combinedText.setText(combined);

                // set location text
                eventLocationText.setText(event.getLocation().toString());

                // load the reason why image (I'm not sure if this is right yet)
                Glide.with(requireContext()).load(event.getImageLink()).into(reasonWhyImage);
            }
        });

        // Handle back navigation
        closeButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp(); // Go back in the navigation stack
        });
    }
}