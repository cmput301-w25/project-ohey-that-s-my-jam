package com.otmj.otmjapp.Helper;

import android.util.Log;

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

    private MoodHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
    }

    /**
     * AND current filter with new filter to produce a new MoodHistoryFilter
     * @param newFilter New filter to AND with current filter
     */
    public void addFilter(Filter newFilter) {
        filter = Filter.and(filter, newFilter);
    }

    /**
     * Accepts a MoodHistoryFilter object, instead of a filter
     * @see #addFilter(Filter)
     */
    public void addFilter(MoodHistoryFilter newFilter) {
        addFilter(newFilter.getFilter());
    }

    /**
     * OR current filter with new filter to produce a new MoodHistoryFilter
     * @param newFilter New filter to OR with current filter
     */
    public void includeFilter(Filter newFilter) {
        filter = Filter.or(filter, newFilter);
    }

    /**
     * Accepts a MoodHistoryFilter object, instead of a filter
     * @see #includeFilter(Filter)
     */
    public void includeFilter(MoodHistoryFilter newFilter) {
        includeFilter(newFilter.getFilter());
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

        // Get first date of the week
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        final Date startDate = calendar.getTime();

        // Get last date of the week
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        final Date endDate = calendar.getTime();

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
        return new MoodHistoryFilter(
                Filter.and(
                        Filter.greaterThanOrEqualTo(MoodEventFields.reason.name(), text),
                        Filter.lessThanOrEqualTo(MoodEventFields.reason.name(), text + 'z')),
                null
        );
    }

    public Filter getFilter() {
        return filter;
    }

    public DBSortOption getSortOption() {
        return sortOption;
    }
}
