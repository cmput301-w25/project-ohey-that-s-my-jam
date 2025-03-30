package com.otmj.otmjapp.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

public class TimelineMusicEventAdapter extends ArrayAdapter<MusicEvent> {

    public TimelineMusicEventAdapter(@NonNull Activity activity, @NonNull ArrayList<MusicEvent> objects) {
        super(activity, 0, objects);
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

        if(null != m.getUser()) { //TODO: user is null, even though database stores user
            TextView author = view.findViewById(R.id.author);
            String authorText = "Posted by: " + m.getUser().getUsername();
            author.setText(authorText);
        }

        TextView trackTitle = view.findViewById(R.id.track_title);
        String title = "Title: " + m.getTrack().getTitle();
        trackTitle.setText(title);

        TextView mood = view.findViewById(R.id.mood);
        String moodText = "Mood: " + m.getAssociatedMood();
        mood.setText(moodText);

        TextView feeling = view.findViewById(R.id.feeling);
        String feelingText = m.getFeeling(); // TODO: format using wrapText method
        feeling.setText(feelingText);

        TextView artist = view.findViewById(R.id.artist);
        String artistText = "Artist: " + m.getTrack().getArtists().get(0).getName(); // TODO: format using wrapText method
        artist.setText(artistText);

        ImageView albumArt = view.findViewById(R.id.albumArt);

        // load image if available
        if(m.getAlbumArtURL() != null && !m.getAlbumArtURL().isEmpty()) {
            Log.d("ImageLoader", "Loading image: " + m.getAlbumArtURL());

            // ensure ImageView is visible
            albumArt.setVisibility(View.VISIBLE);

            // use ImageHandler to load the image
            ImageHandler.loadImage(parent.getContext(), m.getAlbumArtURL(), albumArt);
        }

        return view;
    }
}
