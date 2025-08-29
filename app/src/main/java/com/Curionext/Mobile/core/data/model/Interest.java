package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Interest {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("topic")
    private String topic;

    @SerializedName("category")
    private String category;

    @SerializedName("interest_level")
    private double interestLevel;

    @SerializedName("frequency")
    private int frequency;

    @SerializedName("last_explored")
    private Date lastExplored;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("trend_direction")
    private String trendDirection; // "increasing", "decreasing", "stable"

    @SerializedName("keywords")
    private String[] keywords;

    // Constructors
    public Interest() {}

    public Interest(String childId, String topic, String category, double interestLevel) {
        this.childId = childId;
        this.topic = topic;
        this.category = category;
        this.interestLevel = interestLevel;
        this.frequency = 1;
        this.lastExplored = new Date();
        this.createdAt = new Date();
        this.trendDirection = "stable";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getInterestLevel() { return interestLevel; }
    public void setInterestLevel(double interestLevel) { this.interestLevel = interestLevel; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public Date getLastExplored() { return lastExplored; }
    public void setLastExplored(Date lastExplored) { this.lastExplored = lastExplored; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getTrendDirection() { return trendDirection; }
    public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }

    public String[] getKeywords() { return keywords; }
    public void setKeywords(String[] keywords) { this.keywords = keywords; }
}
