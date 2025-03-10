package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.Models.SocialSituation;
import com.otmj.otmjapp.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class TimelineMoodEventAdapter extends ArrayAdapter<MoodEvent> {

    public TimelineMoodEventAdapter(@NonNull Context context, @NonNull ArrayList<MoodEvent> objects) {
        super(context,0, objects);
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

        TextView usernameText = view.findViewById(R.id.timeline_mood_event_username);
        usernameText.setText(m.getUser().getUsername());

        TextView description = view.findViewById(R.id.timeline_mood_event_desc);
        setMoodEventDescription(description, m);

        long createdTimeMillis = m.getCreatedDate() != null ? m.getCreatedDate().getTime() : System.currentTimeMillis();
        String timeAgo = getTimeAgo(createdTimeMillis);

        TextView timeAgoText = view.findViewById(R.id.timeline_mood_event_date);
        timeAgoText.setText(timeAgo);


        // TODO: Set location

        return view;
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
