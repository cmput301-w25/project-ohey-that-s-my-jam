package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MoodHistoryFilter {
    private final Filter filter;
    private final DBSortOption sortOption;

    public MoodHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
    }

    /**
     * Get mood events from specified users and sort by most recent first
     * @param userIDs   IDs of users to get mood events from
     * @return          Filter that specifies users and sorts by date
     */
    public static MoodHistoryFilter Default(List<String> userIDs) {
        return new MoodHistoryFilter(
            Filter.inArray("userID", userIDs),
            new DBSortOption("createdDate", true)
        );
    }

    public Filter getFilter() {
        return filter;
    }

    public DBSortOption getSortOption() {
        return sortOption;
    }
}
