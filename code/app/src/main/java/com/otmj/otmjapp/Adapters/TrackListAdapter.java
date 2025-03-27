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

import com.otmj.otmjapp.API.Models.Track;
import com.otmj.otmjapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackListAdapter extends ArrayAdapter<Track> {
    public TrackListAdapter(@NonNull Context context, ArrayList<Track> tracks) {
        super(context, 0, tracks);
    }

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
        //TODO: play preview
        Track track = getItem(position);
        assert track != null;

        // get track data
        String title = track.getTitle();
        String artist = track.getArtists().get(0).getName();
        String albumArtUrl = track.getAlbum().getImages().get(0).getURL();

        TextView songTitle = listItemView.findViewById(R.id.song_title);
        songTitle.setText(String.format("%s %s", title, artist));

        ImageView albumArt = listItemView.findViewById(R.id.album_art);
        Picasso.get()
                .load(albumArtUrl)
                .into(albumArt);

        return listItemView;
    }
}
