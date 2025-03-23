package com.otmj.otmjapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class UserProfilePageMoodEventAdapter extends ArrayAdapter<MoodEvent> {
    private Activity activity;

    public UserProfilePageMoodEventAdapter(@NonNull Context context, int resource, @NonNull List<MoodEvent> objects, @NonNull Activity activity) {
        super(context, resource, objects);
        this.activity = activity;
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
        ImageView moodEventImage = view.findViewById(R.id.reason_why_image);
        TextView textView_reason = view.findViewById(R.id.textview_reason);
        TextView textView_location = view.findViewById(R.id.event_location);
        //
        if (m.getLocation() != null) {
            textView_location.setVisibility(View.VISIBLE);
            LocationHelper locationHelper = new LocationHelper(activity);
            locationHelper.getAddressFromLocation(m.getLocation().toLocation(), new LocationHelper.AddressCallback(){
                @Override
                public void onAddressResult(String country, String state, String city) {
                    Log.d("Address", "Address: " + city + state + country);
                    textView_location.setText(city + ", " + state + ", " + country);
                }
                @Override
                public void onAddressError(String error) {
                    Log.e("Address", "Error: " + error);
                }
            });
        } else {
            textView_location.setVisibility(View.GONE);
        }
        textview_emotionalState.setText(m.getEmotionalState().getDescription());
        image_emoji.setImageResource(m.getEmotionalState().getEmoji());
        textView_date.setText(m.getCreatedDate().toString());


        EmotionalState emotionalState = m.getEmotionalState();
        String emotion = emotionalState.toString();
        String fullText;
        if (m.getSocialSituation() != null) {
            String social = m.getSocialSituation().toString().toLowerCase();
            fullText = emotion + " " + social;
        } else {
            fullText = emotion;
        }

        SpannableString spannable = new SpannableString(fullText);

        // Apply color span to the emotion part only
        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), emotionalState.color)),
                0,
                emotion.length(), // just style the emotion part
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textview_emotionalState.setText(spannable);
        if (m.getReason() != null && !m.getReason().isEmpty()) {
            textView_reason.setVisibility(View.VISIBLE);
            textView_reason.setText(m.getReason());
        } else {
            textView_reason.setVisibility(View.GONE);
        }

        if (m.getImageLink() != null) {
            moodEventImage.setVisibility(View.VISIBLE);
            ImageHandler.loadImage(getContext(), m.getImageLink(), moodEventImage);
        } else {
            moodEventImage.setVisibility(View.GONE);
        }

        ImageButton editButton = view.findViewById(R.id.my_mood_edit_button);
        editButton.setOnClickListener(v -> {
            MoodEventAddEditDialogFragment popup = MoodEventAddEditDialogFragment.newInstance(m);
            popup.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "edit");
        });

        return view;
    }
}
