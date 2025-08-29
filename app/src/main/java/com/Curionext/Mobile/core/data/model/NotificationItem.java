package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class NotificationItem {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type; // "interest", "milestone", "alert", "summary", "activity", "system"

    @SerializedName("category")
    private String category; // "learning", "safety", "report", "system"

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("priority")
    private String priority; // "low", "medium", "high", "urgent"

    @SerializedName("action_url")
    private String actionUrl;

    @SerializedName("metadata")
    private String metadata; // JSON string for additional data

    // Constructors
    public NotificationItem() {}

    public NotificationItem(String childId, String title, String description, String type, String category) {
        this.childId = childId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.timestamp = new Date();
        this.isRead = false;
        this.priority = "medium";
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
}
