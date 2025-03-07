package com.otmj.otmjapp;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
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
    
    // private MaterialButton unfollowButton; // will not be implemented yet
    private ImageButton closeButton;

    public static MoodEventDetailsFragment newInstance() {
        return new MoodEventDetailsFragment();
    }

    public void setMoodEventText(TextView textView, MoodEvent event) {
        // Construct the beginning of the sentence using String.format()
        String combined = String.format("Feeling %s 😊 because of %s",
                event.getEmotionalState().getDescription(),
                event.getReason());

        // Handle optional trigger
        String trigger = event.getTrigger();
        if (trigger != null) {
            combined += String.format(" triggered by %s", trigger);
        }

        // Handle optional social situation
        String socialSituation = event.getSocialSituation();
        if (socialSituation != null) {
            switch (socialSituation) {
                case "Alone":
                    combined += String.format(" while %s", socialSituation);
                    break;
                case "Crowd":
                    combined += String.format(" while in a %s", socialSituation);
                    break;
                case "2+":
                    combined += String.format(" with %s others", socialSituation);
                    break;
                default:
                    combined += String.format(" with %s person", socialSituation);
                    break;
            }
        }

        // Convert text to SpannableString for emoji replacement
        SpannableString spannableString = new SpannableString(combined);

        // Get the emoji drawable from the EmotionalState enum
        Drawable emojiDrawable = ContextCompat.getDrawable(textView.getContext(), event.getEmotionalState().emoji);

        if (emojiDrawable != null) {
            emojiDrawable.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight()); // Resize to fit text
            ImageSpan imageSpan = new ImageSpan(emojiDrawable, ImageSpan.ALIGN_BASELINE);

            // Find position AFTER second word ("Feeling [STATE] 😊 ...")
            int emojiPosition = combined.indexOf("😊");

            // Replace the placeholder emoji (😊) with the actual drawable
            spannableString.setSpan(imageSpan, emojiPosition, emojiPosition + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Set the final SpannableString to TextView
        textView.setText(spannableString);
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

                // set all the available text of a mood event
                setMoodEventText(combinedText, event);

                // set location text
                eventLocationText.setText(event.getLocation().toString());

                // load the reason why image (I'm not sure if this is right yet)
                if (event.getImageLink() != null) {
                    Glide.with(requireContext()).load(event.getImageLink()).into(reasonWhyImage);
                }
            }
        });

        // Handle back navigation
        closeButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp(); // Go back in the navigation stack
        });
    }
}