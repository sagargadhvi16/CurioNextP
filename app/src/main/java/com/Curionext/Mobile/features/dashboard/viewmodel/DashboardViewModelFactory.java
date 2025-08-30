package com.curionext.mobile.features.dashboard.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.curionext.mobile.core.data.repository.ChildRepository;
import com.curionext.mobile.core.data.repository.InterestRepository;
import com.curionext.mobile.core.data.repository.NotificationRepository;

import javax.inject.Inject;

public class DashboardViewModelFactory implements ViewModelProvider.Factory {

    private final ChildRepository childRepository;
    private final InterestRepository interestRepository;
    private final NotificationRepository notificationRepository;

    @Inject
    public DashboardViewModelFactory(
            ChildRepository childRepository,
            InterestRepository interestRepository,
            NotificationRepository notificationRepository
    ) {
        this.childRepository = childRepository;
        this.interestRepository = interestRepository;
        this.notificationRepository = notificationRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(childRepository, interestRepository, notificationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}