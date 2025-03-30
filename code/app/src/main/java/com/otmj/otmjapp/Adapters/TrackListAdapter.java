package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackListAdapter extends ArrayAdapter<Track> {
    public TrackListAdapter(@NonNull Context context, ArrayList<Track> tracks) {
        super(context, 0, tracks);
        this.tracks = tracks;
    }

    private int selectedPosition = -1;
    private final ArrayList<Track> tracks;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(getCount() == 0) {
            return new View(getContext());
        }

        View listItemView = convertView;

        if(null == listItemView) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.track_block, parent, false);
        }

        Track track = getItem(position);

        assert track != null;
        //setSelectedTrack(track);
        // get track data
        String title = track.getTitle();
        String artist = track.getArtists().get(0).getName();
        String albumArtUrl = track.getAlbum().getImages().get(0).getURL();

        TextView songTitle = listItemView.findViewById(R.id.song_title);
        String titleText = "Title: " + title;
        titleText = wrapTextAfter20Chars(titleText);
        songTitle.setText(titleText);

        TextView songArtist = listItemView.findViewById(R.id.song_artist);
        String artistText = "Artist: " + artist;
        artistText = wrapTextAfter20Chars(artistText);
        songArtist.setText(artistText);

        ImageView albumArt = listItemView.findViewById(R.id.album_art);
        Picasso.get()
                .load(albumArtUrl)
                .into(albumArt);

        // Change the background color if the item is selected
        if (position == selectedPosition) {
            listItemView.setBackgroundColor(Color.parseColor("#D2D6D2"));
        } else {
            listItemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Handle item click and update selection
        listItemView.setOnClickListener(v -> {
            selectedPosition = position;

            notifyDataSetChanged();
        });

        return listItemView;
    }

    /**
     * Wraps text after 20 characters.
     * <p>
     * @param input The input string to be wrapped.
     * @return The wrapped string.
     */
    private String wrapTextAfter20Chars(String input) {
        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();
        int currentLineLength = 0;

        for (String word : words) {
            if (currentLineLength + word.length() > 35) {
                // Move to new line
                result.append("\n").append(word).append(" ");
                currentLineLength = word.length() + 1;
            } else {
                // Add to current line
                result.append(word).append(" ");
                currentLineLength += word.length() + 1;
            }
        }

        return result.toString().trim();
    }

    public Track getLastSelectedTrack() {
        if(selectedPosition >= 0) {
            return tracks.get(selectedPosition);
        } else {
            return null;
        }
    }
}
