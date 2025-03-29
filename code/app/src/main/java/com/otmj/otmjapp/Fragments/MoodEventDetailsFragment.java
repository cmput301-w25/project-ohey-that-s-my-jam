package com.otmj.otmjapp.Fragments;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.otmj.otmjapp.Adapters.CommentAdapter;
import com.otmj.otmjapp.Helper.CommentHandler;
import com.otmj.otmjapp.Helper.CustomImageSpan;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Helper.UserManager;
import com.otmj.otmjapp.Models.Comment;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.Models.User;
import com.otmj.otmjapp.R;
import com.otmj.otmjapp.databinding.FragmentMoodEventDetailsBinding;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Displays the details of a MoodEvent (user information, timestamp, etc.)
 * and an optional image associated with the event.
 */
public class MoodEventDetailsFragment extends Fragment {

    private FragmentMoodEventDetailsBinding binding;

    /**
     * Inflates the fragment's layout using ViewBinding.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMoodEventDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after onCreateView().
     * Initializes UI elements and sets up the MoodEvent data in the view.
     *
     * @param view The view returned by onCreateView().
     * @param savedInstanceState The saved state of the fragment if it was previously created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        ImageView profileImage = binding.detailsProfilePicture;
        TextView usernameText = binding.detailsUsername;
        TextView eventTimestampText = binding.detailsEventTimestamp;
        ImageView moodEventImage = binding.detailsMoodImage;

        TextView detailsReasonWhy = binding.detailsReasonWhy;
        TextView eventLocationText = binding.detailsEventLocation;
        TextView detailsEmotionAndSocialSituation = binding.detailsEmotionAndSocialSituation;
        ListView commentsListView = binding.commentsListView;
        Button unfollowButton = binding.detailsUnfollowButton;

        // Get the passed arguments using Safe Args
        MoodEventDetailsFragmentArgs args = MoodEventDetailsFragmentArgs.fromBundle(getArguments());
        MoodEvent moodEvent = args.getMoodEvent();
        assert moodEvent != null;

        User loggedInUser = UserManager.getInstance().getCurrentUser(),
                moodEventUser = moodEvent.getUser();

        if (Objects.equals(loggedInUser, moodEventUser)) {
            unfollowButton.setVisibility(View.GONE);
        } else {
            unfollowButton.setVisibility(View.VISIBLE);
        }

        String profileImageUrl = moodEvent.getUser().getProfilePictureLink(); // Make sure this returns a proper URL

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            ImageHandler.loadCircularImage(requireContext(), profileImageUrl, profileImage);
        } else {
            profileImage.setImageResource(R.drawable.profile_placeholder); // fallback default image
        }

        // Set the username text
        usernameText.setText(moodEventUser.getUsername());

        // Set timestamp text
        eventTimestampText.setText(moodEvent.getCreatedDate().toString());

        // Set all the available text of a mood event
        setMoodEventHeaderText(detailsEmotionAndSocialSituation, moodEvent);

        // Set Reason Why
        if (moodEvent.getReason() != null && !moodEvent.getReason().isEmpty()){
            detailsReasonWhy.setText(moodEvent.getReason());
        } else {
            detailsReasonWhy.setVisibility(View.GONE);
        }

        Log.d("MoodEventDetails", "MoodEvent: " + moodEvent);
        Log.d("MoodEventDetails", "Image Link: " + moodEvent.getImageLink());

        // Load Image if available
        if (moodEvent.getImageLink() != null && !moodEvent.getImageLink().isEmpty()) {
            moodEventImage.setVisibility(View.VISIBLE);

            // Use ImageHandler with correct context
            ImageHandler.loadImage(requireContext(), moodEvent.getImageLink(), moodEventImage);
        } else {
            // Hide ImageView if no image
            moodEventImage.setVisibility(View.GONE);
        }

        // Set location if available
        if (moodEvent.getLocation() != null) {
            eventLocationText.setVisibility(View.VISIBLE);

            LocationHelper locationHelper = new LocationHelper(requireActivity());

            locationHelper.getAddressFromLocation(moodEvent.getLocation().toLocation(), new LocationHelper.AddressCallback() {
                @Override
                public void onAddressResult(String country, String state, String city) {
                    Log.d("Address", "Address: " + city + ", " + state + ", " + country);
                    eventLocationText.setText(city + ", " + state + ", " + country);
                }

                @Override
                public void onAddressError(String error) {
                    Log.e("Address", "Error: " + error);
                    eventLocationText.setText("Location unavailable");
                }
            });

        } else {
            eventLocationText.setVisibility(View.GONE);
        }

        profileImage.setOnClickListener(v -> {
            // Only view profile of other users
            if (!Objects.equals(loggedInUser, moodEventUser)) {
                MoodEventDetailsFragmentDirections.ActionMoodEventDetailsFragmentToUserProfileFragment showUserProfile =
                        MoodEventDetailsFragmentDirections.actionMoodEventDetailsFragmentToUserProfileFragment();
                showUserProfile.setUser(moodEvent.getUser());

                NavHostFragment.findNavController(MoodEventDetailsFragment.this)
                        .navigate(showUserProfile);
            }
        });

        // Initialize the CommentHandler
        CommentHandler commentHandler = new CommentHandler();

        // Get MoodEvent details
        String moodEventId = moodEvent.getID();

        // Initialize ListView and Adapter
        CommentAdapter commentsAdapter = new CommentAdapter(requireContext(), new ArrayList<>());
        commentsListView.setAdapter(commentsAdapter);

        // Retrieve user from UserManager
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser(); // Get logged-in user

        String currentUserId = currentUser.getID();

        // Load comments
        commentsAdapter.clear();
        commentHandler.loadComments(moodEventId, comment -> {
            commentsAdapter.add(comment);
            commentsAdapter.notifyDataSetChanged();
        });

        // Handle sending comments
        ImageButton sendCommentButton = binding.sendCommentButton;
        EditText commentInput = binding.commentInput;

        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Add the comment with the correct user details
                commentHandler.addComment(
                        new Comment(currentUserId, moodEventId, commentText),
                        c -> {
                            commentsAdapter.add(c);
                            commentsAdapter.notifyDataSetChanged();
                        }
                );
                commentInput.setText("");  // Clear input field after sending
            } else {
                Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets the mood event description with appropriate formatting, colors, and an emoji representing the mood.
     *
     * @param textView The TextView to display the mood event description.
     * @param event The MoodEvent object containing details about the event.
     */
    public void setMoodEventHeaderText(TextView textView, MoodEvent event) {
        // Construct the beginning of the sentence
        StringBuilder combined = new StringBuilder(event.getUser().getUsername());
        combined.append(String.format(" feels %s 😊", event.getEmotionalState().getDescription()));

        // Handle optional social situation
        SocialSituation socialSituation = event.getSocialSituation();
        if (socialSituation != null && !socialSituation.toString().trim().isEmpty()) {
            if ("Alone".equalsIgnoreCase(socialSituation.toString().trim())) {
                combined.append(" while ").append(socialSituation.toString().toLowerCase()); // Add "while" for "Alone"
            } else {
                combined.append(" ").append(socialSituation.toString().toLowerCase());
            }
        }

        // Convert text to SpannableString for emoji replacement
        SpannableString spannableString = new SpannableString(combined.toString());

        // Get the emoji drawable from the EmotionalState enum
        Drawable emojiDrawable = ContextCompat.getDrawable(textView.getContext(),
                event.getEmotionalState().emoji);

        if (emojiDrawable != null) {
            int size = (int) (textView.getLineHeight() * 2); // Increase size by 2x
            emojiDrawable.setBounds(0, 0, size, size); // Set new bounds for bigger size
            ImageSpan imageSpan = new CustomImageSpan(emojiDrawable);

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