package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Preference {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("topic")
    private String topic;

    @SerializedName("sentiment")
    private double sentiment; // -1.0 to 1.0 (negative to positive)

    @SerializedName("confidence")
    private double confidence; // 0.0 to 1.0

    @SerializedName("frequency")
    private int frequency;

    @SerializedName("category")
    private String category;

    @SerializedName("keywords")
    private String[] keywords;

    @SerializedName("last_updated")
    private Date lastUpdated;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("trend")
    private String trend; // "increasing", "decreasing", "stable"

    // Constructors
    public Preference() {}

    public Preference(String childId, String topic, double sentiment, double confidence, String category) {
        this.childId = childId;
        this.topic = topic;
        this.sentiment = sentiment;
        this.confidence = confidence;
        this.category = category;
        this.frequency = 1;
        this.lastUpdated = new Date();
        this.createdAt = new Date();
        this.trend = "stable";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public double getSentiment() { return sentiment; }
    public void setSentiment(double sentiment) { this.sentiment = sentiment; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String[] getKeywords() { return keywords; }
    public void setKeywords(String[] keywords) { this.keywords = keywords; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    // Helper methods
    public boolean isPositive() {
        return sentiment > 0.1;
    }

    public boolean isNegative() {
        return sentiment < -0.1;
    }

    public boolean isNeutral() {
        return Math.abs(sentiment) <= 0.1;
    }

    public String getSentimentLabel() {
        if (isPositive()) return "Like";
        if (isNegative()) return "Dislike";
        return "Neutral";
    }
}
