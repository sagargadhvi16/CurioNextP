package com.curionext.mobile.core.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
        tableName = "notifications",
        foreignKeys = @ForeignKey(
                entity = ChildEntity.class,
                parentColumns = "child_id",
                childColumns = "child_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("child_id"), @Index("timestamp"), @Index("is_read")}
)
public class NotificationEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "child_id")
    public String childId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "type")
    public String type; // "interest", "milestone", "alert", "summary", "activity", "system"

    @ColumnInfo(name = "category")
    public String category; // "learning", "safety", "report", "system"

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "is_read")
    public boolean isRead;

    @ColumnInfo(name = "priority")
    public String priority; // "low", "medium", "high", "urgent"

    @ColumnInfo(name = "action_url")
    public String actionUrl;

    @ColumnInfo(name = "metadata")
    public String metadata; // JSON string for additional data

    @ColumnInfo(name = "expires_at")
    public Date expiresAt;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    // Constructors
    public NotificationEntity() {}

    public NotificationEntity(@NonNull String id, String childId, String title, String description, String type, String category) {
        this.id = id;
        this.childId = childId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.timestamp = new Date();
        this.isRead = false;
        this.priority = "medium";
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}