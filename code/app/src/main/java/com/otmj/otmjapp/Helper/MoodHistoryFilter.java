package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;

public class MoodHistoryFilter {
    private final Filter filter;

    public MoodHistoryFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }
}
