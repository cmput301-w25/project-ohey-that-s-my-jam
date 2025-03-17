package com.otmj.otmjapp.Helper;

import com.google.firebase.firestore.Filter;
import com.otmj.otmjapp.Models.EmotionalState;

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

    private final Filter filter;
    private final DBSortOption sortOption;

    private MoodHistoryFilter(Filter filter, DBSortOption sortOption) {
        this.filter = filter;
        this.sortOption = sortOption;
    }

    /**
     * AND current filter with new filter to produce a new MoodHistoryFilter
     * @param newFilter New filter to AND with current filter
     * @return          A new MoodHistoryFilter with a compound filter formed by ANDing
     */
    public MoodHistoryFilter addFilter(Filter newFilter) {
        Filter andFilter = Filter.and(this.filter, newFilter);
        return new MoodHistoryFilter(andFilter, sortOption);
    }

    /**
     * OR current filter with new filter to produce a new MoodHistoryFilter
     * @param newFilter New filter to OR with current filter
     * @return          A new MoodHistoryFilter with a compound filter formed by ORing
     */
    public MoodHistoryFilter includeFilter(Filter newFilter) {
        Filter orFilter = Filter.or(this.filter, newFilter);
        return new MoodHistoryFilter(orFilter, sortOption);
    }

    /**
     * Accepts a MoodHistoryFilter object, instead of a filter
     * @see #includeFilter(Filter)
     */
    public MoodHistoryFilter includeFilter(MoodHistoryFilter newFilter) {
        return includeFilter(newFilter.getFilter());
    }

    /**
     * Accepts a MoodHistoryFilter object, instead of a filter
     * @see #addFilter(Filter)
     */
    public MoodHistoryFilter addFilter(MoodHistoryFilter newFilter) {
        return addFilter(newFilter.getFilter());
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

    public static MoodHistoryFilter OnlyEmotionalState(EmotionalState emotionalState) {
        return new MoodHistoryFilter(
                Filter.equalTo(MoodEventFields.emotionalState.name(), emotionalState.name()),
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
