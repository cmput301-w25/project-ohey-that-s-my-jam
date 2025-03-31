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
import android.widget.ListView;
import android.widget.TextView;

import com.otmj.otmjapp.API.Auth.SpotifyAPIManager;
import com.otmj.otmjapp.Helper.ImageHandler;
import com.otmj.otmjapp.MainActivity;
import com.otmj.otmjapp.Models.MusicEvent;
import com.otmj.otmjapp.R;

import java.util.ArrayList;

public class TimelineMusicEventAdapter extends ArrayAdapter<MusicEvent> {
    private int currentlyPlayingPosition = -1;
    private Activity activity;
    public TimelineMusicEventAdapter(@NonNull Activity activity, @NonNull ArrayList<MusicEvent> musicEvents) {
        super(activity, 0, musicEvents);
        this.activity = activity;
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
        String moodText = "Mood: " + currentMusicEvent.getAssociatedMood();
        mood.setText(moodText);

        TextView feeling = view.findViewById(R.id.feeling);
        String feelingText = currentMusicEvent.getFeeling(); // TODO: format using wrapText method
        feeling.setText(feelingText);

        TextView artist = view.findViewById(R.id.artist);
        String artistText = "Artist(s): " + currentMusicEvent.getTrack().getArtists().get(0).getName(); // TODO: format using wrapText method
        artist.setText(artistText);

        ImageView albumArt = view.findViewById(R.id.albumArt);

        // load image if available
        if(currentMusicEvent.getAlbumArtURL() != null && !currentMusicEvent.getAlbumArtURL().isEmpty()) {
            Log.d("ImageLoader", "Loading image: " + currentMusicEvent.getAlbumArtURL());

            // ensure ImageView is visible
            albumArt.setVisibility(View.VISIBLE);

            // use ImageHandler to load the image
            ImageHandler.loadImage(parent.getContext(), currentMusicEvent.getAlbumArtURL(), albumArt);
        }

        ImageView playButton = view.findViewById(R.id.play_button);
        ImageView pauseButton = view.findViewById(R.id.pause_button);

        if (position == currentlyPlayingPosition) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }

        // toggle play/pause buttons of other music events based on current item play status
        playButton.setOnClickListener(v -> {
            SpotifyAPIManager apiManager = new SpotifyAPIManager((MainActivity) activity);
            apiManager.playSong(currentMusicEvent.getTrack().getID());

            if (currentlyPlayingPosition != -1 && currentlyPlayingPosition != position) {
                // Iterate through visible children to find the previously playing one
                ListView listView = (ListView) parent;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                int lastVisiblePosition = listView.getLastVisiblePosition();

                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    //Log.d("TimelineMusicEvent", musicEvents.get(i - firstVisiblePosition).getTrack().getTitle());
                    if (i == currentlyPlayingPosition) {
                        View previousView = listView.getChildAt(i - firstVisiblePosition);

                        if (previousView != null) {
                            ImageView prevPlayButton = previousView.findViewById(R.id.play_button);
                            ImageView prevPauseButton = previousView.findViewById(R.id.pause_button);

                            if (prevPlayButton != null && prevPauseButton != null) {
                                prevPlayButton.setVisibility(View.VISIBLE);
                                prevPauseButton.setVisibility(View.GONE);
                            }
                        }

                        break;
                    }
                }
            }

            // Update the current item's button.
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            currentlyPlayingPosition = position;
            notifyDataSetChanged();
        });

        pauseButton.setOnClickListener(v -> {
            SpotifyAPIManager apiManager = new SpotifyAPIManager((MainActivity) activity);
            apiManager.pauseSong();

            // reset 'currently playing' status
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
            currentlyPlayingPosition = -1;
            notifyDataSetChanged();
        });

        return view;
    }
}
