package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.Models.MoodEvent;
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
        usernameText.setText(m.getPosterUsername());

        // Build description text (incorporates emotional state, social situation and trigger)
        StringBuilder desc = new StringBuilder(m.getEmotionalState().toString());
        if (m.getSocialSituation() != null) {
            desc.append(' ').append(m.getSocialSituation().toLowerCase());
        }
        if (m.getTrigger() != null && m.getTrigger().isBlank()) {
            desc.append("\nbecause of ").append(m.getTrigger());
        }
        String finalDescription = getContext().getString(R.string.mood_event_description,
                desc.toString());

        TextView description = view.findViewById(R.id.timeline_mood_event_desc);
        description.setText(finalDescription);

        // Change date string depending on how far away the event occurred
        String date;
        Duration diff = Duration.between(LocalDateTime.now(), m.getCreatedDate());
        if (diff.toDays() > 14) {
            date = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.CANADA)
                    .format(m.getCreatedDate());
        } else if (diff.toDays() > 7) {
            date = "two weeks ago";
        } else if (diff.toDays() > 1) {
            date = "less than a week ago";
        } else if (diff.toHours() > 1) {
            date = diff.toHours() + " hrs ago";
        } else {
            date = "recently";
        }

        TextView createDate = view.findViewById(R.id.timeline_mood_event_date);
        createDate.setText(date);

        // TODO: Set location

        return view;
    }
}
