package com.otmj.otmjapp.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TimelineMusicEventAdapter extends ArrayAdapter<MusicEvent> {
    private final Activity activity;

    public TimelineMusicEventAdapter(@NonNull Activity activity, @NonNull ArrayList<MusicEvent> objects) {
        super(activity, 0, objects);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.timeline_music_event, parent, false);
        }

        MusicEvent m = getItem(position);
        assert m != null;

        if(m.getUser() != null) {
            TextView author = view.findViewById(R.id.author);
            String authorText = "Author: " + m.getUser().getUsername();
            author.setText(authorText);
        }

        TextView trackTitle = view.findViewById(R.id.track_title);
        trackTitle.setText(m.getTrack().getTitle());

        TextView mood = view.findViewById(R.id.mood);
        String moodText = "Mood: " ; //TODO: change from emoji to mood in MusicEvent
        mood.setText(moodText);


        TextView feeling = view.findViewById(R.id.feeling);
        String feelingText = "Feeling: " + m.getFeeling(); // format using wrapText method
        feeling.setText(feelingText);

        LocalDateTime createdDate = m.getCreatedDate();
        long createdTimeMillis = createdDate != null ?
                createdDate.toInstant(java.time.ZoneOffset.UTC).toEpochMilli() :
                System.currentTimeMillis();
        String timeAgo = getTimeAgo(createdTimeMillis);

        TextView date = view.findViewById(R.id.date);
        date.setText(timeAgo);

        ImageView albumArt = view.findViewById(R.id.album_art);

        // load image if available
        if(m.getAlbumArtURL() != null && !m.getAlbumArtURL().isEmpty()) {
            Log.d("ImageLoader", "Loading image: " + m.getAlbumArtURL());

            // ensure ImageView is visible
            albumArt.setVisibility(View.VISIBLE);

            // use ImageHandler to load the image
            ImageHandler.loadImage(parent.getContext(), m.getAlbumArtURL(), albumArt);
        } else {
            // hide ImageView if no image
            albumArt.setVisibility(View.GONE);
        }

        return view;
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
