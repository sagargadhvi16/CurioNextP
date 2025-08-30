package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.LocationEntity;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM locations WHERE child_id = :childId ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsByChild(String childId);

    @Query("SELECT * FROM locations WHERE child_id = :childId ORDER BY timestamp DESC LIMIT 1")
    Single<LocationEntity> getLatestLocationByChild(String childId);

    @Query("SELECT * FROM locations WHERE child_id = :childId ORDER BY timestamp DESC LIMIT :limit")
    Single<List<LocationEntity>> getRecentLocationsByChild(String childId, int limit);

    @Query("SELECT * FROM locations WHERE child_id = :childId AND timestamp >= :sinceDate ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsSince(String childId, Date sinceDate);

    @Query("SELECT * FROM locations WHERE child_id = :childId AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsBetween(String childId, Date startDate, Date endDate);

    @Query("SELECT * FROM locations WHERE child_id = :childId AND is_in_safe_zone = :inSafeZone ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsBySafeZoneStatus(String childId, boolean inSafeZone);

    @Query("SELECT * FROM locations WHERE child_id = :childId AND safe_zone_name = :safeZoneName ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsBySafeZone(String childId, String safeZoneName);

    @Query("SELECT * FROM locations WHERE id = :locationId")
    Single<LocationEntity> getLocationById(String locationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertLocation(LocationEntity location);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertLocations(List<LocationEntity> locations);

    @Update
    Completable updateLocation(LocationEntity location);

    @Delete
    Completable deleteLocation(LocationEntity location);

    @Query("DELETE FROM locations WHERE id = :locationId")
    Completable deleteLocationById(String locationId);

    @Query("DELETE FROM locations WHERE child_id = :childId")
    Completable deleteAllLocationsByChild(String childId);

    @Query("DELETE FROM locations WHERE timestamp < :beforeDate")
    Completable deleteOldLocations(Date beforeDate);

    @Query("DELETE FROM locations")
    Completable deleteAllLocations();

    @Query("SELECT COUNT(*) FROM locations WHERE child_id = :childId")
    Single<Integer> getLocationCount(String childId);

    @Query("SELECT COUNT(*) FROM locations WHERE child_id = :childId AND timestamp >= :sinceDate")
    Single<Integer> getLocationCountSince(String childId, Date sinceDate);

    @Query("SELECT COUNT(*) FROM locations WHERE child_id = :childId AND is_in_safe_zone = 1")
    Single<Integer> getSafeZoneLocationCount(String childId);

    @Query("SELECT AVG(accuracy) FROM locations WHERE child_id = :childId AND timestamp >= :sinceDate")
    Single<Double> getAverageAccuracy(String childId, Date sinceDate);

    @Query("SELECT AVG(speed) FROM locations WHERE child_id = :childId AND timestamp >= :sinceDate AND speed > 0")
    Single<Double> getAverageSpeed(String childId, Date sinceDate);

    @Query("SELECT DISTINCT safe_zone_name FROM locations WHERE child_id = :childId AND safe_zone_name IS NOT NULL ORDER BY safe_zone_name")
    Single<List<String>> getVisitedSafeZones(String childId);

    @Query("SELECT * FROM locations WHERE child_id = :childId AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng ORDER BY timestamp DESC")
    Single<List<LocationEntity>> getLocationsInBounds(String childId, double minLat, double maxLat, double minLng, double maxLng);

    @Query("UPDATE locations SET is_in_safe_zone = :inSafeZone, safe_zone_name = :safeZoneName WHERE id = :locationId")
    Completable updateSafeZoneStatus(String locationId, boolean inSafeZone, String safeZoneName);
}