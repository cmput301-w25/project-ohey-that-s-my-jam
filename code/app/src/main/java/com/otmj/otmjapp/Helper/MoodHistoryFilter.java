package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MoodHistoryFilter {
    // Field names as found in database
    enum MoodEventFields {
        createdDate
    }

    private final Filter filter;
    private final DBSortOption sortOption;

    public MoodHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
    }

    public MoodHistoryFilter addFilter(Filter newFilter) {
        Filter andFilter = Filter.and(this.filter, newFilter);
        return new MoodHistoryFilter(andFilter, sortOption);
    }

    /**
     * Get mood events from specified users and sort by most recent first
     * @param userIDs   IDs of users to get mood events from
     * @return          Filter that specifies users and sorts by date
     */
    public static MoodHistoryFilter Default(List<String> userIDs) {
        return new MoodHistoryFilter(
            Filter.inArray("userID", userIDs),
            new DBSortOption(MoodEventFields.createdDate.name(), true)
        );
    }

    /**
     * Get mood events from most recent week. Uses default as base
     * @param userIDs   IDs of users to get mood events from
     * @return          Filter that specifies users from most recent weeks, sorts by date
     */
    public static MoodHistoryFilter MostRecentWeek(List<String> userIDs) {
        String createdDate = MoodEventFields.createdDate.name();

        MoodHistoryFilter base = Default(userIDs);

        // Get first date of the week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        final Date startDate = calendar.getTime();

        // Get last date of the week
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        final Date endDate = calendar.getTime();

        return base
                .addFilter(Filter.greaterThanOrEqualTo(createdDate, startDate))
                .addFilter(Filter.lessThanOrEqualTo(createdDate, endDate));
    }

    public Filter getFilter() {
        return filter;
    }

    public DBSortOption getSortOption() {
        return sortOption;
    }
}
