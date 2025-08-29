package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class SafeZone {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("radius")
    private int radius;

    @SerializedName("color")
    private String color;

    @SerializedName("icon")
    private String icon;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("schedule")
    private String schedule;

    @SerializedName("alerts_enabled")
    private boolean alertsEnabled;

    @SerializedName("created_at")
    private Date createdAt;

    // Constructors
    public SafeZone() {}

    public SafeZone(String childId, String name, String address, double latitude, double longitude, int radius) {
        this.childId = childId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.isActive = true;
        this.alertsEnabled = true;
        this.schedule = "All days, All hours";
        this.createdAt = new Date();
        this.color = "#4CAF50";
        this.icon = "home";
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

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
