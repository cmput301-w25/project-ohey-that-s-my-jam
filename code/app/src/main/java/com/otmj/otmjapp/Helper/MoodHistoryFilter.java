package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.EmotionalState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Represents a filter for mood history data
 * and can be used to define criteria for filtering mood events.<br>
 * Use static method `Default` as base for building filters, e.g.<br>
 * `MoodHistoryFilter.Default(userIDs).addFilter(...)`
 */
public class MoodHistoryFilter {
    // Field names as found in database
    enum MoodEventFields {
        createdDate,
        userID,
        emotionalState,
        reason
    }

    private Filter filter;
    private final DBSortOption sortOption;
    private String queryText;

    private MoodHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
        this.queryText = null;
    }

    /**
     * Specialized constructor for handling text search
     * @param queryText Query to search for
     */
    private MoodHistoryFilter(String queryText) {
        this.filter = null;
        this.sortOption = null;
        this.queryText = queryText.trim().toLowerCase();
    }

    /**
     * AND current filter with new filter to produce a new MoodHistoryFilter
     * @param newFilter New filter to AND with current filter
     */
    public void addFilter(@NonNull Filter newFilter) {
        filter = Filter.and(filter, newFilter);
    }

    /**
     * Accepts a MoodHistoryFilter object, instead of a filter
     * @see #addFilter(Filter)
     */
    public void addFilter(MoodHistoryFilter newFilter) {
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
    public static MoodHistoryFilter Default(List<String> userIDs) {
        return new MoodHistoryFilter(
            Filter.inArray(MoodEventFields.userID.name(), userIDs),
            new DBSortOption(MoodEventFields.createdDate.name(), true)
        );
    }

    /**
     * Get mood events from most recent week.
     * @return Filter that specifies users from most recent week
     */
    public static MoodHistoryFilter MostRecentWeek() {
        String createdDate = MoodEventFields.createdDate.name();

        // Get today's date
        Calendar calendar = Calendar.getInstance();
        final Date endDate = calendar.getTime();

        // Go back 6 days from now to get starting date
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        final Date startDate = calendar.getTime();

        return new MoodHistoryFilter(
                Filter.and(
                    Filter.greaterThanOrEqualTo(createdDate, startDate),
                    Filter.lessThanOrEqualTo(createdDate, endDate)),
                null);
    }

    /**
     * Get mood events that have an emotional state included in the list
     * @param emotionalStates   List of desired emotional states
     * @return                  Filter that specifies emotional states
     */
    public static MoodHistoryFilter OnlyEmotionalStates(List<EmotionalState> emotionalStates) {
        // Get names of emotional states
        ArrayList<String> emotionalStateNames = new ArrayList<>();
        for (EmotionalState e : emotionalStates) {
            emotionalStateNames.add(e.name());
        }

        return new MoodHistoryFilter(
                Filter.inArray(MoodEventFields.emotionalState.name(), emotionalStateNames),
                null);
    }

    public static MoodHistoryFilter ContainsText(String text) {
        return new MoodHistoryFilter(text);
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
