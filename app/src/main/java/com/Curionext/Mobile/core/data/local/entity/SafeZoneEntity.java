package com.curionext.mobile.core.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
        tableName = "safe_zones",
        foreignKeys = @ForeignKey(
                entity = ChildEntity.class,
                parentColumns = "child_id",
                childColumns = "child_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("child_id")}
)
public class SafeZoneEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "child_id")
    public String childId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "radius")
    public int radius; // in meters

    @ColumnInfo(name = "color")
    public String color;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "is_active")
    public boolean isActive;

    @ColumnInfo(name = "schedule")
    public String schedule; // JSON string for time-based restrictions

    @ColumnInfo(name = "alerts_enabled")
    public boolean alertsEnabled;

    @ColumnInfo(name = "entry_notifications")
    public boolean entryNotifications;

    @ColumnInfo(name = "exit_notifications")
    public boolean exitNotifications;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "updated_at")
    public Date updatedAt;

    @ColumnInfo(name = "last_entered")
    public Date lastEntered;

    @ColumnInfo(name = "last_exited")
    public Date lastExited;

    @ColumnInfo(name = "visit_count")
    public int visitCount;

    // Constructors
    public SafeZoneEntity() {}

    public SafeZoneEntity(@NonNull String id, String childId, String name, String address, double latitude, double longitude, int radius) {
        this.id = id;
        this.childId = childId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isActive = true;
        this.alertsEnabled = true;
        this.entryNotifications = true;
        this.exitNotifications = true;
        this.schedule = "All days, All hours";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.color = "#4CAF50";
        this.icon = "home";
        this.visitCount = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public boolean isAlertsEnabled() { return alertsEnabled; }
    public void setAlertsEnabled(boolean alertsEnabled) { this.alertsEnabled = alertsEnabled; }

    public boolean isEntryNotifications() { return entryNotifications; }
    public void setEntryNotifications(boolean entryNotifications) { this.entryNotifications = entryNotifications; }

    public boolean isExitNotifications() { return exitNotifications; }
    public void setExitNotifications(boolean exitNotifications) { this.exitNotifications = exitNotifications; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getLastEntered() { return lastEntered; }
    public void setLastEntered(Date lastEntered) { this.lastEntered = lastEntered; }

    public Date getLastExited() { return lastExited; }
    public void setLastExited(Date lastExited) { this.lastExited = lastExited; }

    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
}