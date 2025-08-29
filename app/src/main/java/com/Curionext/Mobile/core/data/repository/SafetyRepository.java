package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.model.SafeZone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class SafetyRepository {

    private final LocationRepository locationRepository;

    @Inject
    public SafetyRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Single<List<SafeZone>> getActiveSafeZones(String childId) {
        return locationRepository.getSafeZones(childId)
                .map(safeZones -> {
                    return safeZones.stream()
                            .filter(SafeZone::isActive)
                            .collect(java.util.stream.Collectors.toList());
                });
    }

    public Single<Boolean> isLocationInSafeZone(double latitude, double longitude, List<SafeZone> safeZones) {
        return Single.fromCallable(() -> {
            for (SafeZone zone : safeZones) {
                if (zone.isActive() && isWithinRadius(latitude, longitude, zone)) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean isWithinRadius(double lat1, double lon1, SafeZone safeZone) {
        double lat2 = safeZone.getLatitude();
        double lon2 = safeZone.getLongitude();
        int radius = safeZone.getRadius();

        // Calculate distance using Haversine formula
        double R = 6371e3; // Earth radius in meters
        double φ1 = Math.toRadians(lat1);
        double φ2 = Math.toRadians(lat2);
        double Δφ = Math.toRadians(lat2 - lat1);
        double Δλ = Math.toRadians(lon2 - lon1);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance <= radius;
    }
}
