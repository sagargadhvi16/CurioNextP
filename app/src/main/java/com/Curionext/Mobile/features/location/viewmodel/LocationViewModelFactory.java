package com.curionext.mobile.features.location.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.curionext.mobile.core.data.repository.LocationRepository;
import com.curionext.mobile.core.data.repository.SafetyRepository;

import javax.inject.Inject;

public class LocationViewModelFactory implements ViewModelProvider.Factory {

    private final LocationRepository locationRepository;
    private final SafetyRepository safetyRepository;

    @Inject
    public LocationViewModelFactory(
            LocationRepository locationRepository,
            SafetyRepository safetyRepository
    ) {
        this.locationRepository = locationRepository;
        this.safetyRepository = safetyRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(com.curionext.mobile.features.location.viewmodel.LocationViewModel.class)) {
            return (T) new com.curionext.mobile.features.location.viewmodel.LocationViewModel(
                    locationRepository,
                    safetyRepository
            );
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}