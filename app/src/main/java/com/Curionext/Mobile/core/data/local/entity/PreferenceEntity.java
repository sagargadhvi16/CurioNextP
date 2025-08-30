package com.curionext.mobile.core.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
        tableName = "preferences",
        foreignKeys = @ForeignKey(
                entity = ChildEntity.class,
                parentColumns = "child_id",
                childColumns = "child_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("child_id"), @Index("topic"), @Index("sentiment")}
)
public class PreferenceEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "child_id")
    public String childId;

    @ColumnInfo(name = "topic")
    public String topic;

    @ColumnInfo(name = "sentiment")
    public double sentiment; // -1.0 to 1.0 (negative to positive)

    @ColumnInfo(name = "confidence")
    public double confidence; // 0.0 to 1.0

    @ColumnInfo(name = "frequency")
    public int frequency;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "keywords")
    public String keywords; // JSON string array

    @ColumnInfo(name = "last_updated")
    public Date lastUpdated;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "trend")
    public String trend; // "increasing", "decreasing", "stable"

    @ColumnInfo(name = "context")
    public String context; // Additional context about when this preference was identified

    @ColumnInfo(name = "source")
    public String source; // "conversation", "activity", "observation"

    @ColumnInfo(name = "intensity")
    public double intensity; // How strongly the preference was expressed (0.0 to 1.0)

    // Constructors
    public PreferenceEntity() {}

    public PreferenceEntity(@NonNull String id, String childId, String topic, double sentiment, double confidence, String category) {
        this.id = id;
        this.childId = childId;
        this.topic = topic;
        this.sentiment = sentiment;
        this.confidence = confidence;
        this.category = category;
        this.frequency = 1;
        this.lastUpdated = new Date();
        this.createdAt = new Date();
        this.trend = "stable";
        this.source = "conversation";
        this.intensity = Math.abs(sentiment);
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

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }

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

    public boolean isHighConfidence() {
        return confidence >= 0.7;
    }

    public boolean isStrongPreference() {
        return intensity >= 0.7 && confidence >= 0.6;
    }
}