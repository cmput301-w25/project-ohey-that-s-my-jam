package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.otmj.otmjapp.Fragments.MoodEventAddEditDialogFragment;
import com.otmj.otmjapp.Models.EmotionalState;
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
        EmotionalState emotionalState = m.getEmotionalState();
        SpannableString spannable = new SpannableString("Feeling: " + emotionalState.toString());
        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), emotionalState.color)),
                9, // Start index (after "Feeling: ")
                spannable.length(), // End index
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textView_feeling.setText(spannable);


        textView_reason.setText("Reason: " + Objects.toString(m.getReason(), ""));
        textView_trigger.setText("Trigger: " + Objects.toString(m.getTrigger(), ""));
        if (m.getSocialSituation() != null) {
            textView_socialStatus.setText("Social Situation: " + m.getSocialSituation().toString());
        }

        ImageButton editButton = view.findViewById(R.id.my_mood_edit_button);
        editButton.setOnClickListener(v -> {
            MoodEventAddEditDialogFragment popup = MoodEventAddEditDialogFragment.newInstance(m);
            popup.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "edit");
        });

        return view;
    }
}
