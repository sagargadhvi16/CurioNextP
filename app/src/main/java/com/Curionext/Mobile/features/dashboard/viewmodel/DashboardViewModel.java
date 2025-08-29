package com.curionext.mobile.features.dashboard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.curionext.mobile.core.data.repository.ChildRepository;
import com.curionext.mobile.core.data.repository.InterestRepository;
import com.curionext.mobile.core.data.repository.NotificationRepository;

import java.util.List;

import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DashboardViewModel extends ViewModel {

    private final ChildRepository childRepository;
    private final InterestRepository interestRepository;
    private final NotificationRepository notificationRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Child> childProfile = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> interests = new MutableLiveData<>();
    private final MutableLiveData<List<NotificationItem>> notifications = new MutableLiveData<>();
    private final MutableLiveData<WeeklySummary> weeklySummary = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private static final String CHILD_ID = "avani_001"; // In real app, get from preferences

    @Inject
    public DashboardViewModel(
            ChildRepository childRepository,
            InterestRepository interestRepository,
            NotificationRepository notificationRepository
    ) {
        this.childRepository = childRepository;
        this.interestRepository = interestRepository;
        this.notificationRepository = notificationRepository;
    }

    // Getters for LiveData
    public LiveData<Child> getChildProfile() { return childProfile; }
    public LiveData<List<Interest>> getInterests() { return interests; }
    public LiveData<List<NotificationItem>> getNotifications() { return notifications; }
    public LiveData<WeeklySummary> getWeeklySummary() { return weeklySummary; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadDashboardData() {
        isLoading.setValue(true);

        // Load child profile
        disposables.add(
                childRepository.getChildProfile(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                child -> {
                                    childProfile.setValue(child);
                                    loadInterests();
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load child profile: " + throwable.getMessage());
                                }
                        )
        );
    }

    private void loadInterests() {
        disposables.add(
                interestRepository.getTopInterests(CHILD_ID, 5)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                interestList -> {
                                    interests.setValue(interestList);
                                    loadNotifications();
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load interests: " + throwable.getMessage());
                                    loadNotifications(); // Continue loading other data
                                }
                        )
        );
    }

    private void loadNotifications() {
        disposables.add(
                notificationRepository.getRecentNotifications(CHILD_ID, 5)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                notificationList -> {
                                    notifications.setValue(notificationList);
                                    loadWeeklySummary();
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load notifications: " + throwable.getMessage());
                                    loadWeeklySummary(); // Continue loading other data
                                }
                        )
        );
    }

    private void loadWeeklySummary() {
        disposables.add(
                childRepository.getWeeklySummary(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                summary -> {
                                    weeklySummary.setValue(summary);
                                    isLoading.setValue(false);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load weekly summary: " + throwable.getMessage());
                                    isLoading.setValue(false);
                                }
                        )
        );
    }

    public void refreshDashboardData() {
        loadDashboardData();
    }

    public void markNotificationAsRead(String notificationId) {
        disposables.add(
                notificationRepository.markAsRead(notificationId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Refresh notifications
                                    loadNotifications();
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to mark notification as read: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void navigateToInterestDetails(String interestId) {
        // Implement navigation logic
        // This could trigger a navigation event that the Activity observes
    }

    public void playWeeklySummary(String summaryId) {
        // Implement audio playback logic
        // This could start an audio service or trigger media player
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
