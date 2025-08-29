package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class WeeklySummary {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("week_start")
    private Date weekStart;

    @SerializedName("week_end")
    private Date weekEnd;

    @SerializedName("summary_text")
    private String summaryText;

    @SerializedName("audio_url")
    private String audioUrl;

    @SerializedName("duration_seconds")
    private int durationSeconds;

    @SerializedName("topics_explored")
    private List<String> topicsExplored;

    @SerializedName("new_interests")
    private List<String> newInterests;

    @SerializedName("conversation_starters")
    private List<String> conversationStarters;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("is_listened")
    private boolean isListened;

    // Constructors
    public WeeklySummary() {}

    public WeeklySummary(String childId, Date weekStart, Date weekEnd, String summaryText) {
        this.childId = childId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.summaryText = summaryText;
        this.createdAt = new Date();
        this.isListened = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public Date getWeekStart() { return weekStart; }
    public void setWeekStart(Date weekStart) { this.weekStart = weekStart; }

    public Date getWeekEnd() { return weekEnd; }
    public void setWeekEnd(Date weekEnd) { this.weekEnd = weekEnd; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public List<String> getTopicsExplored() { return topicsExplored; }
    public void setTopicsExplored(List<String> topicsExplored) { this.topicsExplored = topicsExplored; }

    public List<String> getNewInterests() { return newInterests; }
    public void setNewInterests(List<String> newInterests) { this.newInterests = newInterests; }

    public List<String> getConversationStarters() { return conversationStarters; }
    public void setConversationStarters(List<String> conversationStarters) { this.conversationStarters = conversationStarters; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isListened() { return isListened; }
    public void setListened(boolean listened) { isListened = listened; }
}
