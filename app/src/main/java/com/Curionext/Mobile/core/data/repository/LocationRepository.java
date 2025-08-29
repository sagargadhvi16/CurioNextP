package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.local.dao.LocationDao;
import com.curionext.mobile.core.data.local.dao.SafeZoneDao;
import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.data.model.SafeZone;
import com.curionext.mobile.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class LocationRepository {

    private final ApiService apiService;
    private final LocationDao locationDao;
    private final SafeZoneDao safeZoneDao;

    @Inject
    public LocationRepository(ApiService apiService, LocationDao locationDao, SafeZoneDao safeZoneDao) {
        this.apiService = apiService;
        this.locationDao = locationDao;
        this.safeZoneDao = safeZoneDao;
    }

    public Single<LocationData> getCurrentLocation(String childId) {
        return apiService.getCurrentLocation(childId);
    }

    public Single<List<LocationData>> getLocationHistory(String childId, String date) {
        return apiService.getLocationHistory(childId, date);
    }

    public Single<LocationData> updateLocation(String childId, LocationData location) {
        return apiService.updateLocation(childId, location)
                .doOnSuccess(updatedLocation -> {
                    // Cache in local database
                    // locationDao.insertLocation(updatedLocation);
                });
    }

    public Single<List<SafeZone>> getSafeZones(String childId) {
        return apiService.getSafeZones(childId)
                .doOnSuccess(safeZones -> {
                    // Cache in local database
                    // safeZoneDao.insertSafeZones(safeZones);
                });
    }

    public Single<SafeZone> createSafeZone(String childId, SafeZone safeZone) {
        return apiService.createSafeZone(childId, safeZone);
    }

    public Single<SafeZone> updateSafeZone(String childId, String zoneId, SafeZone safeZone) {
        return apiService.updateSafeZone(childId, zoneId, safeZone);
    }

    public Single<Void> deleteSafeZone(String childId, String zoneId) {
        return apiService.deleteSafeZone(childId, zoneId);
    }
}
