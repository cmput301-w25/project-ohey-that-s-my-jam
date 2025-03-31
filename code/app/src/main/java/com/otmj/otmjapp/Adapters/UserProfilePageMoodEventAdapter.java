package com.otmj.otmjapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.otmj.otmjapp.Fragments.MoodEventAddEditDialogFragment;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Helper.LocationHelper;
import com.otmj.otmjapp.Models.EmotionalState;
import com.otmj.otmjapp.Models.MoodEvent;
import com.otmj.otmjapp.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class UserProfilePageMoodEventAdapter extends ArrayAdapter<MoodEvent> {
    private boolean blurText = false;
    private Activity activity;

    private boolean isCurrentUserProfile = false;

    public void setIsCurrentUserProfile(boolean isCurrentUserProfile) {
        this.isCurrentUserProfile = isCurrentUserProfile;
        notifyDataSetChanged();
    }


    public void setBlurText(boolean blur) {
        this.blurText = blur;
        notifyDataSetChanged();
    }

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
        textView_date.setText(m.getCreatedDate().toString());


        EmotionalState emotionalState = m.getEmotionalState();
        String emotion = emotionalState.toString();
        String fullText;
        if (m.getSocialSituation() != null) {
            String social = m.getSocialSituation().toString().toLowerCase();
            if ("alone".equalsIgnoreCase(social)){
                fullText = emotion + " while " + social;
            }
            else {
                fullText = emotion + " " + social;
            }
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

        if (blurText) {
            textView_reason.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            textView_reason.getPaint().setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            textView_date.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            textView_date.getPaint().setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            textview_emotionalState.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            textview_emotionalState.getPaint().setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            textView_location.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            textView_location.getPaint().setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        } else {
            textView_reason.setLayerType(View.LAYER_TYPE_NONE, null);
            textView_reason.getPaint().setMaskFilter(null);

            textView_date.setLayerType(View.LAYER_TYPE_NONE, null);
            textView_date.getPaint().setMaskFilter(null);

            textview_emotionalState.setLayerType(View.LAYER_TYPE_NONE, null);
            textview_emotionalState.getPaint().setMaskFilter(null);

            textView_location.setLayerType(View.LAYER_TYPE_NONE, null);
            textView_location.getPaint().setMaskFilter(null);
        }

        if (blurText) {
            // Blur mood image
            if (m.getImageLink() != null) {
                moodEventImage.setVisibility(View.VISIBLE);
                ImageHandler.loadBlurredImage(getContext(), m.getImageLink(), moodEventImage, 25); // 25 is the blur radius
            } else {
                moodEventImage.setVisibility(View.GONE);
            }

            // Blur emoji too
            Glide.with(getContext())
                    .load(m.getEmotionalState().getEmoji())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(20, 3)))
                    .into(image_emoji);
        } else {
            // Load regular image
            if (m.getImageLink() != null) {
                moodEventImage.setVisibility(View.VISIBLE);
                ImageHandler.loadImage(getContext(), m.getImageLink(), moodEventImage);
            } else {
                moodEventImage.setVisibility(View.GONE);
            }

            image_emoji.setImageResource(m.getEmotionalState().getEmoji());
        }

        ImageButton editButton = view.findViewById(R.id.my_mood_edit_button);

        if (isCurrentUserProfile) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setFocusable(false); // This is necessary for the list item to be clickable
            editButton.setOnClickListener(v -> {
                MoodEventAddEditDialogFragment popup = MoodEventAddEditDialogFragment.newInstance(m);
                popup.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "edit");
            });
        } else {
            editButton.setVisibility(View.GONE);
        }

        return view;
    }
}
