package com.curionext.mobile.core.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.data.model.SafeZone;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtils {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    /**
     * Calculate distance between two geographic points using Haversine formula
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Calculate distance between two LocationData objects
     */
    public static double calculateDistance(LocationData location1, LocationData location2) {
        return calculateDistance(
                location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude()
        );
    }

    /**
     * Check if a location is within a safe zone
     */
    public static boolean isLocationInSafeZone(LocationData location, SafeZone safeZone) {
        if (!safeZone.isActive()) {
            return false;
        }

        double distance = calculateDistance(
                location.getLatitude(), location.getLongitude(),
                safeZone.getLatitude(), safeZone.getLongitude()
        );

        return distance <= safeZone.getRadius();
    }

    /**
     * Check if a location is within any of the provided safe zones
     */
    public static SafeZone findContainingSafeZone(LocationData location, List<SafeZone> safeZones) {
        for (SafeZone safeZone : safeZones) {
            if (isLocationInSafeZone(location, safeZone)) {
                return safeZone;
            }
        }
        return null;
    }

    /**
     * Get human-readable address from coordinates using Geocoder
     */
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Build readable address string
                StringBuilder addressBuilder = new StringBuilder();

                if (address.getFeatureName() != null) {
                    addressBuilder.append(address.getFeatureName()).append(", ");
                }

                if (address.getThoroughfare() != null) {
                    addressBuilder.append(address.getThoroughfare()).append(", ");
                }

                if (address.getSubLocality() != null) {
                    addressBuilder.append(address.getSubLocality()).append(", ");
                }

                if (address.getLocality() != null) {
                    addressBuilder.append(address.getLocality());
                }

                String result = addressBuilder.toString();
                if (result.endsWith(", ")) {
                    result = result.substring(0, result.length() - 2);
                }

                return result.isEmpty() ? "Unknown Location" : result;
            }
        } catch (IOException e) {
            // Geocoding failed
        }

        return "Lat: " + String.format(Locale.getDefault(), "%.6f", latitude) +
                ", Lng: " + String.format(Locale.getDefault(), "%.6f", longitude);
    }

    /**
     * Check if location permissions are granted
     */
    public static boolean hasLocationPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if background location permission is granted (Android 10+)
     */
    public static boolean hasBackgroundLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Convert meters to human-readable distance
     */
    public static String formatDistance(double meters) {
        if (meters < 1000) {
            return String.format(Locale.getDefault(), "%.0f m", meters);
        } else {
            return String.format(Locale.getDefault(), "%.1f km", meters / 1000);
        }
    }

    /**
     * Convert speed from m/s to km/h
     */
    public static double convertSpeedToKmh(float speedMps) {
        return speedMps * 3.6;
    }

    /**
     * Format speed for display
     */
    public static String formatSpeed(float speedMps) {
        double kmh = convertSpeedToKmh(speedMps);
        return String.format(Locale.getDefault(), "%.1f km/h", kmh);
    }

    /**
     * Check if location is valid (not null coordinates)
     */
    public static boolean isValidLocation(LocationData location) {
        return location != null &&
                location.getLatitude() != 0.0 &&
                location.getLongitude() != 0.0 &&
                Math.abs(location.getLatitude()) <= 90 &&
                Math.abs(location.getLongitude()) <= 180;
    }

    /**
     * Check if location has good accuracy
     */
    public static boolean hasGoodAccuracy(LocationData location) {
        return location.getAccuracy() <= 50.0f; // Within 50 meters
    }

    /**
     * Get accuracy description
     */
    public static String getAccuracyDescription(float accuracy) {
        if (accuracy <= 10) return "Excellent";
        if (accuracy <= 25) return "Good";
        if (accuracy <= 50) return "Fair";
        if (accuracy <= 100) return "Poor";
        return "Very Poor";
    }

    /**
     * Calculate bearing between two points
     */
    public static float calculateBearing(double lat1, double lng1, double lat2, double lng2) {
        double dLng = Math.toRadians(lng2 - lng1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double y = Math.sin(dLng) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLng);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (float) ((bearing + 360) % 360);
    }

    /**
     * Get cardinal direction from bearing
     */
    public static String getCardinalDirection(float bearing) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(bearing / 45.0) % 8;
        return directions[index];
    }

    /**
     * Create LocationData from Android Location object
     */
    public static LocationData createLocationData(String childId, Location location) {
        LocationData locationData = new LocationData();
        locationData.setChildId(childId);
        locationData.setLatitude(location.getLatitude());
        locationData.setLongitude(location.getLongitude());
        locationData.setAccuracy(location.getAccuracy());
        locationData.setTimestamp(new java.util.Date(location.getTime()));

        if (location.hasSpeed()) {
            locationData.setSpeed(location.getSpeed());
        }

        if (location.hasAltitude()) {
            locationData.setAltitude(location.getAltitude());
        }

        if (location.hasBearing()) {
            locationData.setBearing(location.getBearing());
        }

        return locationData;
    }

    /**
     * Calculate bounding box for a given center point and radius
     */
    public static double[] calculateBoundingBox(double centerLat, double centerLng, double radiusMeters) {
        double latOffset = radiusMeters / 111000.0; // Rough conversion: 1 degree lat = ~111km
        double lngOffset = radiusMeters / (111000.0 * Math.cos(Math.toRadians(centerLat)));

        return new double[] {
                centerLat - latOffset,  // minLat
                centerLat + latOffset,  // maxLat
                centerLng - lngOffset,  // minLng
                centerLng + lngOffset   // maxLng
        };
    }

    /**
     * Check if device is stationary based on speed threshold
     */
    public static boolean isStationary(LocationData location) {
        return location.getSpeed() < 1.0f; // Less than 1 m/s (3.6 km/h)
    }

    /**
     * Get movement status description
     */
    public static String getMovementStatus(LocationData location) {
        float speed = location.getSpeed();
        if (speed < 1.0f) return "Stationary";
        if (speed < 5.0f) return "Walking";
        if (speed < 15.0f) return "Running/Cycling";
        if (speed < 50.0f) return "Vehicle";
        return "Fast Vehicle";
    }
}