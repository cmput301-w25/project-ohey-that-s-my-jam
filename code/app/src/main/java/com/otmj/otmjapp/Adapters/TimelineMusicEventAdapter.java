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
    public TimelineMusicEventAdapter(@NonNull Activity activity, @NonNull ArrayList<MusicEvent> musicEvents) {
        super(activity, 0, musicEvents);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.timeline_music_event, parent, false);
        }

        MusicEvent currentMusicEvent = getItem(position);
        assert currentMusicEvent != null;

        if(null != currentMusicEvent.getUser()) { //TODO: user is null, even though database stores user
            TextView author = view.findViewById(R.id.author);
            String authorText = "Posted by: " + currentMusicEvent.getUser().getUsername();
            author.setText(authorText);
        }

        TextView trackTitle = view.findViewById(R.id.track_title);
        String title = "Title: " + currentMusicEvent.getTrack().getTitle();
        trackTitle.setText(title);

        TextView mood = view.findViewById(R.id.mood);
        String moodText = "Mood: " + currentMusicEvent.getMoodEvent().getEmotionalState().toString();
        mood.setText(moodText);

        TextView feeling = view.findViewById(R.id.feeling);
        String feelingText = currentMusicEvent.getFeeling(); // TODO: format using wrapText method
        feeling.setText(feelingText);

        TextView artist = view.findViewById(R.id.artist);
        String artistText = "Artist(s): " + currentMusicEvent.getTrack().getArtists().get(0).getName(); // TODO: format using wrapText method
        artist.setText(artistText);

        ImageView albumArt = view.findViewById(R.id.albumArt);

        // load image if available
        String imageURL = currentMusicEvent.getTrack().getAlbum().getImages().get(0).getURL();
        if(imageURL != null && !imageURL.isEmpty()) {
            Log.d("ImageLoader", "Loading image: " + imageURL);

            // ensure ImageView is visible
            albumArt.setVisibility(View.VISIBLE);

            // use ImageHandler to load the image
            ImageHandler.loadImage(parent.getContext(), imageURL, albumArt);
        }

        return view;
    }
}
