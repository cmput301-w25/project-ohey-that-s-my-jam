package com.otmj.otmjapp.Fragments;

import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.databinding.FragmentMoodEventDetailsBinding;

public class MoodEventDetailsFragment extends Fragment {

    private FragmentMoodEventDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMoodEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        ImageView profileImage = binding.detailsProfilePicture;
        TextView usernameText = binding.detailsUsername;
        TextView eventTimestampText = binding.detailsEventTimestamp;
        ImageView moodEventImage = binding.detailsMoodImage;
        TextView eventDescription = binding.detailsEventDescription;
        TextView eventLocationText = binding.detailsEventLocation;

        // Get the passed arguments using Safe Args
        MoodEventDetailsFragmentArgs args = MoodEventDetailsFragmentArgs.fromBundle(getArguments());
        MoodEvent moodEvent = args.getMoodEvent();
        assert moodEvent != null;

        // set the username text
         usernameText.setText(moodEvent.getUser().getUsername());

        // load profile image
//        if (moodEvent.getUser().getProfilePictureLink() != null) {
            // Glide.with(requireContext()).load(event.user.getProfilePictureLink()).into(profileImage);
//        }

        // set time stamp text
        eventTimestampText.setText(moodEvent.getCreatedDate().toString());

        // set all the available text of a mood event
        setMoodEventDescription(eventDescription, moodEvent);

        // set location text
//        eventLocationText.setText(moodEvent.getLocation().toString());

        // load the reason why image (I'm not sure if this is right yet)
        if (moodEvent.getImageLink() != null) {
            Glide.with(requireContext()).load(moodEvent.getImageLink()).into(moodEventImage);
        }
    }

    public void setMoodEventDescription(TextView textView, MoodEvent event) {
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
        SocialSituation socialSituation = event.getSocialSituation();
        if (socialSituation != null) {
            combined += " " + socialSituation.toString();
        }

        // Convert text to SpannableString for emoji replacement
        SpannableString spannableString = new SpannableString(combined);
        // Get the emoji drawable from the EmotionalState enum
        Drawable emojiDrawable = ContextCompat.getDrawable(textView.getContext(),
                event.getEmotionalState().emoji);

        if (emojiDrawable != null) {
            emojiDrawable.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight()); // Resize to fit text
            ImageSpan imageSpan = new ImageSpan(emojiDrawable, ImageSpan.ALIGN_BASELINE);

            // Find position AFTER second word ("Feeling [STATE] 😊 ...")
            int emojiPosition = combined.indexOf("😊");

            // Replace the placeholder emoji (😊) with the actual drawable
            spannableString.setSpan(imageSpan, emojiPosition, emojiPosition + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Get color dynamically from EmotionalState
        int emotionColor = ContextCompat.getColor(textView.getContext(), event.getEmotionalState().color);

        // Find the position of the emotion word (e.g., "Fear")
        int emotionStart = combined.indexOf(event.getEmotionalState().getDescription());
        if (emotionStart != -1) {
            int emotionEnd = emotionStart + event.getEmotionalState().getDescription().length();
            spannableString.setSpan(new ForegroundColorSpan(emotionColor), emotionStart, emotionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Set the final SpannableString to TextView
        textView.setText(spannableString);
    }
}