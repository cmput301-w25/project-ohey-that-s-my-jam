package com.otmj.otmjapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.otmj.otmjapp.Helper.CommentHandler;
import com.otmj.otmjapp.Helper.CustomImageSpan;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.R;

import java.util.ArrayList;


public class TimelineMoodEventAdapter extends ArrayAdapter<MoodEvent> {
private final Activity activity;
public TimelineMoodEventAdapter(@NonNull Activity activity, @NonNull ArrayList<MoodEvent> objects) {
    super(activity, 0, objects);
    this.activity = activity;
}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.timeline_mood_event, parent, false);
        }

        MoodEvent m = getItem(position);
        assert m != null;

        if (m.getUser() != null) {
            TextView usernameText = view.findViewById(R.id.timeline_mood_event_username);
            setMoodEventHeaderText(usernameText, m);

            ImageView profilePic = view.findViewById(R.id.timeline_mood_event_profile_pic);

            String profileImageUrl = m.getUser().getProfilePictureLink(); // Make sure this returns a proper URL

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                ImageHandler.loadCircularImage(getContext(), profileImageUrl, profilePic);
            } else {
                profilePic.setImageResource(R.drawable.profile_placeholder); // fallback default image
            }
        }

        TextView description = view.findViewById(R.id.timeline_mood_event_desc);
        if (m.getReason() != null && !m.getReason().isEmpty()) {
            description.setVisibility(View.VISIBLE);
            description.setText(m.getReason());
        } else {
            description.setVisibility(View.GONE);
        }

        long createdTimeMillis = m.getCreatedDate() != null ? m.getCreatedDate().getTime() : System.currentTimeMillis();
        String timeAgo = getTimeAgo(createdTimeMillis);

        TextView timeAgoText = view.findViewById(R.id.timeline_mood_event_date);
        timeAgoText.setText(timeAgo);

        ImageView moodEventImage = view.findViewById(R.id.mood_image);

        // Load Image if available
        if (m.getImageLink() != null && !m.getImageLink().isEmpty()) {
            Log.d("ImageLoader", "Loading image: " + m.getImageLink());

            // Ensure ImageView is visible
            moodEventImage.setVisibility(View.VISIBLE);

            // Use ImageHandler to load the image
            ImageHandler.loadImage(parent.getContext(), m.getImageLink(), moodEventImage);
        } else {
            // Hide ImageView if no image
            moodEventImage.setVisibility(View.GONE);
        }

        TextView locationText = view.findViewById(R.id.timeline_mood_event_location);

        if (m.getLocation() != null) {
            locationText.setVisibility(View.VISIBLE);

            LocationHelper locationHelper = new LocationHelper(activity);

            locationHelper.getAddressFromLocation(m.getLocation().toLocation(), new LocationHelper.AddressCallback() {
                @Override
                public void onAddressResult(String country, String state, String city) {
                    Log.d("Address", "Address: " + city + ", " + state + ", " + country);
                    locationText.setText(city + ", " + state + ", " + country);
                }

                @Override
                public void onAddressError(String error) {
                    Log.e("Address", "Error: " + error);
                    locationText.setText("Location unavailable");
                }
            });

        } else {
            locationText.setVisibility(View.GONE);
        }


        TextView commentCountText = view.findViewById(R.id.timeline_comment_count);
        commentCountText.setText(String.valueOf(0));

        CommentHandler commentHandler = new CommentHandler();
        commentHandler.getCommentCount(m.getID(), new CommentHandler.CommentCountsCallback() {
            @Override
            public void result(int count) {
                commentCountText.setText(String.valueOf(count));
            }
        });

        return view;
    }

    public void setMoodEventHeaderText(TextView textView, MoodEvent event) {
        String username = "username";
        if (event.getUser() != null) {
            username = event.getUser().getUsername();
        }

        // Construct the beginning of the sentence
        StringBuilder combined = new StringBuilder(username);
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


    public String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < DateUtils.MINUTE_IN_MILLIS) {
            long sec = diff / 1000;
            return sec + (sec == 1 ? " sec ago" : " secs ago");
        } else if (diff < DateUtils.HOUR_IN_MILLIS) {
            long min = diff / DateUtils.MINUTE_IN_MILLIS;
            return min + (min == 1 ? " min ago" : " mins ago");
        } else if (diff < DateUtils.DAY_IN_MILLIS) {
            long hr = diff / DateUtils.HOUR_IN_MILLIS;
            return hr + (hr == 1 ? " hr ago" : " hrs ago");
        } else if (diff < 2 * DateUtils.DAY_IN_MILLIS) {
            return "Yesterday";
        } else {
            long days = diff / DateUtils.DAY_IN_MILLIS;
            return days + " days ago";
        }
    }



}
