package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.Privacy;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MusicHistoryFilter {
    // Field names as found in database
    enum MusicEventFields {
        associatedMood,
        createdDate,
        user,
        privacy
    }

    private Filter filter;
    private final DBSortOption sortOption;
    private String queryText;

    private MusicHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
        this.queryText = null;
    }

    /**
     * Specialized constructor for handling text search
     * @param queryText Query to search for
     */
    private MusicHistoryFilter(String queryText) {
        this.filter = null;
        this.sortOption = null;
        this.queryText = queryText.trim().toLowerCase();
    }

    /**
     * AND current filter with new filter to produce a new MusicHistoryFilter
     * @param newFilter New filter to AND with current filter
     */
    public void addFilter(@NonNull Filter newFilter) {
        filter = Filter.and(filter, newFilter);
    }

    /**
     * Accepts a MusicHistoryFilter object, instead of a filter
     * @see #addFilter(Filter)
     */
    public void addFilter(MusicHistoryFilter newFilter) {
        if (newFilter.queryText != null) {
            this.queryText = newFilter.queryText;
        }
        if (newFilter.getFilter() != null) {
            addFilter(newFilter.getFilter());
        }
    }

    /**
     * Get mood events from specified users and sort by most recent first
     * @param userIDs   IDs of users to get mood events from
     * @return          Filter that specifies users and sorts by date
     */
    public static MusicHistoryFilter Default(List<String> userIDs) {
        return new MusicHistoryFilter(
                Filter.inArray(MusicHistoryFilter.MusicEventFields.user.name(), userIDs),
                new DBSortOption(MusicHistoryFilter.MusicEventFields.createdDate.name(), true)
        );
    }

    public static MusicHistoryFilter PublicMoodEvents() {
        return new MusicHistoryFilter(
                Filter.equalTo(MusicHistoryFilter.MusicEventFields.privacy.name(), Privacy.Public),
                null
        );
    }

    /**
     * Get music events from most recent week.
     * @return Filter that specifies users from most recent week
     */
     public static MusicHistoryFilter MostRecentWeek() {
         String createdDate = MusicHistoryFilter.MusicEventFields.createdDate.name();

         // Get today's date
         Calendar calendar = Calendar.getInstance();
         final Date endDate = calendar.getTime();

         // Go back 6 days from now to get starting date
         calendar.add(Calendar.DATE, -6);
         final Date startDate = calendar.getTime();

         return new MusicHistoryFilter(
                 Filter.and(
                         Filter.greaterThanOrEqualTo(createdDate, startDate),
                         Filter.lessThanOrEqualTo(createdDate, endDate)
                 ),
                 null);
     }

     public static MusicHistoryFilter ContainsText(String text) {
         return new MusicHistoryFilter(text);
     }

     public Filter getFilter() {
        return filter;
     }

     public DBSortOption getSortOption() {
        return sortOption;
     }

     public String getQueryText() {
        return queryText;
     }
}
