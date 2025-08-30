package com.curionext.mobile.core.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
        tableName = "interests",
        foreignKeys = @ForeignKey(
                entity = ChildEntity.class,
                parentColumns = "child_id",
                childColumns = "child_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("child_id")}
)
public class InterestEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "child_id")
    public String childId;

    @ColumnInfo(name = "topic")
    public String topic;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "interest_level")
    public double interestLevel;

    @ColumnInfo(name = "frequency")
    public int frequency;

    @ColumnInfo(name = "last_explored")
    public Date lastExplored;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "trend_direction")
    public String trendDirection; // "increasing", "decreasing", "stable"

    @ColumnInfo(name = "keywords")
    public String keywords; // JSON string array

    // Constructors
    public InterestEntity() {}

    public InterestEntity(@NonNull String id, String childId, String topic, String category, double interestLevel) {
        this.id = id;
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

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
}