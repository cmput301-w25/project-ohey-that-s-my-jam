package com.otmj.otmjapp.Helper;

import com.otmj.otmjapp.Models.EmotionalState;

import java.util.ArrayList;
import java.util.List;

/**
 *  A Class that helps manage filters for mood history searches.
 */
public class FilterOptions {
    private boolean last7Days = false;
    private String reasonText = "";
    private final List<EmotionalState> emotionalStates = new ArrayList<>();

    public FilterOptions() {}

    /**
     * Filters for mood events in the last 7 days.
     */
    public boolean getLast7Days() {
        return last7Days;
    }

    public void setLast7Days(boolean last7Days) {
        this.last7Days = last7Days;
    }

    /**
     * Filters for text within a Reason of a mood event.
     */
    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    /**
     * Provides the list of all possible emotional states.
     * @return a list of emotional states.
     */
    public final List<EmotionalState> getEmotionalStates() {
        return emotionalStates;
    }

    public void resetEmotionalStates() {
        emotionalStates.clear();
    }

    /**
     * Filters for mood events with a certain emotional state.
     */
    public void addEmotionalState(EmotionalState emotionalState) {
        if (!emotionalStates.contains(emotionalState)) {
            emotionalStates.add(emotionalState);
        }
    }

    /**
     * Builds a custom mood history filter based on the selected filter options.
     *
     * @return A `MoodHistoryFilter` object.
     */
    public MoodHistoryFilter buildFilter(List<String> userIDs) {
        MoodHistoryFilter moodHistoryFilter = MoodHistoryFilter.Default(userIDs);
        if (last7Days) {
            moodHistoryFilter.addFilter(MoodHistoryFilter.MostRecentWeek());
        }
        if (!emotionalStates.isEmpty()) {
            moodHistoryFilter.addFilter(MoodHistoryFilter.OnlyEmotionalStates(emotionalStates));
        }
        if (!reasonText.isBlank()) {
            moodHistoryFilter.addFilter(MoodHistoryFilter.ContainsText(reasonText));
        }

        return moodHistoryFilter;
    }
}
