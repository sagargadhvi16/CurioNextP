package com.curionext.mobile.core.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Date;

@Entity(
        tableName = "locations",
        foreignKeys = @ForeignKey(
                entity = ChildEntity.class,
                parentColumns = "child_id",
                childColumns = "child_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("child_id"), @Index("timestamp")}
)
public class LocationEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "child_id")
    public String childId;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "accuracy")
    public float accuracy;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "timestamp")
    public Date timestamp;

    @ColumnInfo(name = "is_in_safe_zone")
    public boolean isInSafeZone;

    @ColumnInfo(name = "safe_zone_name")
    public String safeZoneName;

    @ColumnInfo(name = "battery_level")
    public int batteryLevel;

    @ColumnInfo(name = "speed")
    public float speed; // km/h

    @ColumnInfo(name = "altitude")
    public double altitude; // meters

    @ColumnInfo(name = "bearing")
    public float bearing; // degrees

    // Constructors
    public LocationEntity() {}

    public LocationEntity(@NonNull String id, String childId, double latitude, double longitude, float accuracy) {
        this.id = id;
        this.childId = childId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.timestamp = new Date();
        this.isInSafeZone = false;
        this.batteryLevel = 100;
        this.speed = 0f;
        this.altitude = 0d;
        this.bearing = 0f;
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

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public double getAltitude() { return altitude; }
    public void setAltitude(double altitude) { this.altitude = altitude; }

    public float getBearing() { return bearing; }
    public void setBearing(float bearing) { this.bearing = bearing; }
}