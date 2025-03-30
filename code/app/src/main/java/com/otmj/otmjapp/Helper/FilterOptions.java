package com.otmj.otmjapp.Helper;

import com.otmj.otmjapp.Models.EmotionalState;

import java.util.ArrayList;
import java.util.List;

public class FilterOptions {
    private boolean last7Days = false;
    private String reasonText = "";
    private final List<EmotionalState> emotionalStates = new ArrayList<>();

    public FilterOptions() {}

    public boolean getLast7Days() {
        return last7Days;
    }

    public void setLast7Days(boolean last7Days) {
        this.last7Days = last7Days;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public final List<EmotionalState> getEmotionalStates() {
        return emotionalStates;
    }

    public void resetEmotionalStates() {
        emotionalStates.clear();
    }

    public void addEmotionalState(EmotionalState emotionalState) {
        if (!emotionalStates.contains(emotionalState)) {
            emotionalStates.add(emotionalState);
        }
    }

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
