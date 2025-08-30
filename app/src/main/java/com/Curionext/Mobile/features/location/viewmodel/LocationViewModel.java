package com.curionext.mobile.features.location.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curionext.mobile.core.data.model.LocationData;
import com.curionext.mobile.core.data.model.SafeZone;
import com.curionext.mobile.core.data.repository.LocationRepository;
import com.curionext.mobile.core.data.repository.SafetyRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class LocationViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final SafetyRepository safetyRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<LocationData> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<List<SafeZone>> safeZones = new MutableLiveData<>();
    private final MutableLiveData<List<LocationData>> locationHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTrackingEnabled = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> locationStatus = new MutableLiveData<>();

    private static final String CHILD_ID = "avani_001"; // In real app, get from preferences

    @Inject
    public LocationViewModel(
            LocationRepository locationRepository,
            SafetyRepository safetyRepository
    ) {
        this.locationRepository = locationRepository;
        this.safetyRepository = safetyRepository;

        // Load initial data
        loadCurrentLocation();
        loadSafeZones();
    }

    // Getters for LiveData
    public LiveData<LocationData> getCurrentLocation() { return currentLocation; }
    public LiveData<List<SafeZone>> getSafeZones() { return safeZones; }
    public LiveData<List<LocationData>> getLocationHistory() { return locationHistory; }
    public LiveData<Boolean> getIsTrackingEnabled() { return isTrackingEnabled; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getLocationStatus() { return locationStatus; }

    public void startLocationTracking() {
        isTrackingEnabled.setValue(true);
        loadCurrentLocation();
        locationStatus.setValue("Location tracking started");
    }

    public void stopLocationTracking() {
        isTrackingEnabled.setValue(false);
        locationStatus.setValue("Location tracking stopped");
    }

    public void setTrackingEnabled(boolean enabled) {
        if (enabled) {
            startLocationTracking();
        } else {
            stopLocationTracking();
        }
    }

    public void loadCurrentLocation() {
        isLoading.setValue(true);

        disposables.add(
                locationRepository.getCurrentLocation(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                location -> {
                                    currentLocation.setValue(location);
                                    isLoading.setValue(false);
                                    updateLocationStatus(location);
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load current location: " + throwable.getMessage());
                                    locationStatus.setValue("Unable to get current location");
                                }
                        )
        );
    }

    public void refreshCurrentLocation() {
        loadCurrentLocation();
    }

    public void loadSafeZones() {
        disposables.add(
                safetyRepository.getActiveSafeZones(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                zones -> {
                                    safeZones.setValue(zones);
                                    // Check if current location is in safe zone
                                    LocationData current = currentLocation.getValue();
                                    if (current != null) {
                                        checkSafeZoneStatus(current, zones);
                                    }
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load safe zones: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadLocationHistory() {
        // Get location history for today
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        disposables.add(
                locationRepository.getLocationHistory(CHILD_ID, todayDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                history -> {
                                    locationHistory.setValue(history);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load location history: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void createSafeZone(SafeZone safeZone) {
        isLoading.setValue(true);

        disposables.add(
                locationRepository.createSafeZone(CHILD_ID, safeZone)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                createdZone -> {
                                    isLoading.setValue(false);
                                    loadSafeZones(); // Refresh the list
                                    locationStatus.setValue("Safe zone '" + createdZone.getName() + "' created");
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to create safe zone: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void editSafeZone(String safeZoneId) {
        // This would typically trigger navigation to an edit screen
        locationStatus.setValue("Editing safe zone: " + safeZoneId);
    }

    public void toggleSafeZone(String safeZoneId, boolean enabled) {
        List<SafeZone> currentZones = safeZones.getValue();
        if (currentZones != null) {
            for (SafeZone zone : currentZones) {
                if (zone.getId().equals(safeZoneId)) {
                    zone.setActive(enabled);

                    disposables.add(
                            locationRepository.updateSafeZone(CHILD_ID, safeZoneId, zone)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            updatedZone -> {
                                                loadSafeZones(); // Refresh the list
                                                locationStatus.setValue("Safe zone " + (enabled ? "enabled" : "disabled"));
                                            },
                                            throwable -> {
                                                errorMessage.setValue("Failed to update safe zone: " + throwable.getMessage());
                                            }
                                    )
                    );
                    break;
                }
            }
        }
    }

    public void deleteSafeZone(String safeZoneId) {
        disposables.add(
                locationRepository.deleteSafeZone(CHILD_ID, safeZoneId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    loadSafeZones(); // Refresh the list
                                    locationStatus.setValue("Safe zone deleted");
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to delete safe zone: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void showLocationOnMap(LocationData location) {
        // This would typically open a map view centered on the location
        locationStatus.setValue("Showing location on map: " + location.getAddress());
    }

    private void updateLocationStatus(LocationData location) {
        if (location.isInSafeZone()) {
            locationStatus.setValue("In " + location.getSafeZoneName() + " safe zone");
        } else {
            locationStatus.setValue("Outside safe zone");
        }
    }

    private void checkSafeZoneStatus(LocationData location, List<SafeZone> zones) {
        disposables.add(
                safetyRepository.isLocationInSafeZone(
                                location.getLatitude(),
                                location.getLongitude(),
                                zones
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isInSafeZone -> {
                                    location.setInSafeZone(isInSafeZone);
                                    if (isInSafeZone) {
                                        // Find which safe zone
                                        for (SafeZone zone : zones) {
                                            if (isLocationInZone(location, zone)) {
                                                location.setSafeZoneName(zone.getName());
                                                break;
                                            }
                                        }
                                    } else {
                                        location.setSafeZoneName(null);
                                    }
                                    currentLocation.setValue(location);
                                    updateLocationStatus(location);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to check safe zone status: " + throwable.getMessage());
                                }
                        )
        );
    }

    private boolean isLocationInZone(LocationData location, SafeZone zone) {
        double distance = calculateDistance(
                location.getLatitude(), location.getLongitude(),
                zone.getLatitude(), zone.getLongitude()
        );
        return distance <= zone.getRadius();
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}