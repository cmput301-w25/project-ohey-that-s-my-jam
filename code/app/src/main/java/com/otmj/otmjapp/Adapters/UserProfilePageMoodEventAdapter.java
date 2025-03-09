package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class UserProfilePageMoodEventAdapter extends ArrayAdapter<MoodEvent> {

    public UserProfilePageMoodEventAdapter(@NonNull Context context, int resource, @NonNull List<MoodEvent> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.my_mood_history_block, parent, false);
        }

        MoodEvent m = getItem(position);
        assert m != null;
        TextView textview_emotionalState = view.findViewById(R.id.textView_emotionalState);
        ImageView image_emoji = view.findViewById(R.id.image_emoji);
        TextView textView_date = view.findViewById(R.id.event_timestamp);
        ImageView imageView_image = view.findViewById(R.id.reason_why_image);
        TextView textView_feeling = view.findViewById(R.id.textview_feeling);
        TextView textView_reason = view.findViewById(R.id.textview_reason);
        TextView textView_trigger = view.findViewById(R.id.textview_trigger);
        TextView textView_socialStatus =view.findViewById(R.id.textview_socialStatus);
        TextView textView_location = view.findViewById(R.id.event_location);
        textview_emotionalState.setText(m.getEmotionalState().getDescription());
        image_emoji.setImageResource(m.getEmotionalState().getEmoji());
        textView_date.setText(m.getCreatedDate().toString());
        textView_feeling.setText("Feeling: " + Objects.toString(m.getEmotionalState().getDescription(), ""));
        textView_reason.setText("Reason: " + Objects.toString(m.getReason(), ""));
        textView_trigger.setText("Trigger: " + Objects.toString(m.getTrigger(), ""));
        textView_socialStatus.setText("Social Situation: " + Objects.toString(m.getSocialSituation().toString(), ""));
        return view;
    }
}
