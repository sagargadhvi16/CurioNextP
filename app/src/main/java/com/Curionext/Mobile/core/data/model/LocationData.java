package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class LocationData {
    @SerializedName("id")
    private String id;

    @SerializedName("child_id")
    private String childId;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("accuracy")
    private float accuracy;

    @SerializedName("address")
    private String address;

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("is_in_safe_zone")
    private boolean isInSafeZone;

    @SerializedName("safe_zone_name")
    private String safeZoneName;

    @SerializedName("battery_level")
    private int batteryLevel;

    // Constructors
    public LocationData() {}

    public LocationData(String childId, double latitude, double longitude, float accuracy) {
        this.childId = childId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.timestamp = new Date();
        this.isInSafeZone = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChildId() { return childId; }
    public void setChildId(String childId) { this.childId = childId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = accuracy; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isInSafeZone() { return isInSafeZone; }
    public void setInSafeZone(boolean inSafeZone) { isInSafeZone = inSafeZone; }

    public String getSafeZoneName() { return safeZoneName; }
    public void setSafeZoneName(String safeZoneName) { this.safeZoneName = safeZoneName; }

    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel; }
}
