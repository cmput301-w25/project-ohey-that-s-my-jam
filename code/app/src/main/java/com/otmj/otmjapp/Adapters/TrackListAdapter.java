package com.otmj.otmjapp.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.API.Models.Track;

import java.util.ArrayList;

public class TrackListAdapter extends ArrayAdapter<Track> {
    public TrackListAdapter(@NonNull Context context, ArrayList<Track> tracks) {
        super(context, 0, tracks);
    }

    /*@NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(getCount() == 0) {
            return new View(getContext());
        }

        // get the Track object for the current position
        Track track = getItem(position);
        assert track != null;


    }*/
}
