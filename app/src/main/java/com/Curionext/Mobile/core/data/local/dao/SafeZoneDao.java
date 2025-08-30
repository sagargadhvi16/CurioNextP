package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.SafeZoneEntity;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface SafeZoneDao {

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId ORDER BY created_at DESC")
    Single<List<SafeZoneEntity>> getSafeZonesByChild(String childId);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND is_active = 1 ORDER BY name")
    Single<List<SafeZoneEntity>> getActiveSafeZonesByChild(String childId);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND alerts_enabled = 1")
    Single<List<SafeZoneEntity>> getAlertEnabledSafeZones(String childId);

    @Query("SELECT * FROM safe_zones WHERE id = :safeZoneId")
    Single<SafeZoneEntity> getSafeZoneById(String safeZoneId);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND name = :name")
    Single<SafeZoneEntity> getSafeZoneByName(String childId, String name);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    Single<List<SafeZoneEntity>> getSafeZonesInArea(String childId, double minLat, double maxLat, double minLng, double maxLng);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSafeZone(SafeZoneEntity safeZone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSafeZones(List<SafeZoneEntity> safeZones);

    @Update
    Completable updateSafeZone(SafeZoneEntity safeZone);

    @Delete
    Completable deleteSafeZone(SafeZoneEntity safeZone);

    @Query("DELETE FROM safe_zones WHERE id = :safeZoneId")
    Completable deleteSafeZoneById(String safeZoneId);

    @Query("DELETE FROM safe_zones WHERE child_id = :childId")
    Completable deleteAllSafeZonesByChild(String childId);

    @Query("DELETE FROM safe_zones")
    Completable deleteAllSafeZones();

    @Query("UPDATE safe_zones SET is_active = :isActive WHERE id = :safeZoneId")
    Completable updateSafeZoneStatus(String safeZoneId, boolean isActive);

    @Query("UPDATE safe_zones SET alerts_enabled = :alertsEnabled WHERE id = :safeZoneId")
    Completable updateAlertsEnabled(String safeZoneId, boolean alertsEnabled);

    @Query("UPDATE safe_zones SET last_entered = :enteredAt, visit_count = visit_count + 1 WHERE id = :safeZoneId")
    Completable recordEntry(String safeZoneId, Date enteredAt);

    @Query("UPDATE safe_zones SET last_exited = :exitedAt WHERE id = :safeZoneId")
    Completable recordExit(String safeZoneId, Date exitedAt);

    @Query("UPDATE safe_zones SET radius = :newRadius WHERE id = :safeZoneId")
    Completable updateRadius(String safeZoneId, int newRadius);

    @Query("UPDATE safe_zones SET color = :color, icon = :icon WHERE id = :safeZoneId")
    Completable updateAppearance(String safeZoneId, String color, String icon);

    @Query("SELECT COUNT(*) FROM safe_zones WHERE child_id = :childId AND is_active = 1")
    Single<Integer> getActiveSafeZoneCount(String childId);

    @Query("SELECT AVG(radius) FROM safe_zones WHERE child_id = :childId AND is_active = 1")
    Single<Double> getAverageRadius(String childId);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND visit_count > 0 ORDER BY visit_count DESC LIMIT :limit")
    Single<List<SafeZoneEntity>> getMostVisitedSafeZones(String childId, int limit);

    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND last_entered IS NOT NULL ORDER BY last_entered DESC LIMIT :limit")
    Single<List<SafeZoneEntity>> getRecentlyVisitedSafeZones(String childId, int limit);

    /**
     * Check if a location (lat, lng) falls within any active safe zone for a child
     * This is a helper method for location tracking
     */
    @Query("SELECT * FROM safe_zones WHERE child_id = :childId AND is_active = 1")
    Single<List<SafeZoneEntity>> getActiveSafeZonesForLocationCheck(String childId);
}