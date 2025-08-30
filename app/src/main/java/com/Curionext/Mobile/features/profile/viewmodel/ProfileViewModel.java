package com.curionext.mobile.features.profile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.curionext.mobile.core.data.repository.ChildRepository;
import com.curionext.mobile.core.data.repository.InterestRepository;
import com.curionext.mobile.core.util.Constants;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final ChildRepository childRepository;
    private final InterestRepository interestRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Child> childProfile = new MutableLiveData<>();
    private final MutableLiveData<List<Interest>> childInterests = new MutableLiveData<>();
    private final MutableLiveData<WeeklySummary> weeklySummary = new MutableLiveData<>();
    private final MutableLiveData<String> avatarUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSaving = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    private static final String CHILD_ID = "avani_001"; // In real app, get from preferences

    @Inject
    public ProfileViewModel(
            ChildRepository childRepository,
            InterestRepository interestRepository
    ) {
        this.childRepository = childRepository;
        this.interestRepository = interestRepository;

        // Load initial data
        loadChildProfile();
    }

    // Getters for LiveData
    public LiveData<Child> getChildProfile() { return childProfile; }
    public LiveData<List<Interest>> getChildInterests() { return childInterests; }
    public LiveData<WeeklySummary> getWeeklySummary() { return weeklySummary; }
    public LiveData<String> getAvatarUrl() { return avatarUrl; }
    public LiveData<Boolean> getIsEditing() { return isEditing; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsSaving() { return isSaving; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void loadChildProfile() {
        isLoading.setValue(true);

        disposables.add(
                childRepository.getChildProfile(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                child -> {
                                    childProfile.setValue(child);
                                    avatarUrl.setValue(child.getAvatarUrl());
                                    loadChildInterests();
                                    loadWeeklySummary();
                                    isLoading.setValue(false);
                                    statusMessage.setValue("Profile loaded successfully");
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to load child profile: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadChildInterests() {
        disposables.add(
                interestRepository.getTopInterests(CHILD_ID, 5)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                interests -> {
                                    childInterests.setValue(interests);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load interests: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void loadWeeklySummary() {
        disposables.add(
                childRepository.getWeeklySummary(CHILD_ID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                summary -> {
                                    weeklySummary.setValue(summary);
                                },
                                throwable -> {
                                    errorMessage.setValue("Failed to load weekly summary: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void startEditing() {
        isEditing.setValue(true);
        statusMessage.setValue("Edit mode enabled");
    }

    public void cancelEditing() {
        isEditing.setValue(false);
        // Reload original data
        loadChildProfile();
        statusMessage.setValue("Edit mode cancelled");
    }

    public void saveProfile(String name, Integer age, String avatarUrl) {
        // Validation
        if (!isValidProfileData(name, age)) {
            return;
        }

        isSaving.setValue(true);

        Child currentChild = childProfile.getValue();
        if (currentChild != null) {
            // Update child data
            currentChild.setName(name);
            currentChild.setAge(age);
            currentChild.setAvatarUrl(avatarUrl);
            currentChild.setUpdatedAt(new Date());

            disposables.add(
                    childRepository.updateChildProfile(CHILD_ID, currentChild)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    updatedChild -> {
                                        childProfile.setValue(updatedChild);
                                        this.avatarUrl.setValue(updatedChild.getAvatarUrl());
                                        isSaving.setValue(false);
                                        isEditing.setValue(false);
                                        successMessage.setValue("Profile updated successfully");
                                    },
                                    throwable -> {
                                        isSaving.setValue(false);
                                        errorMessage.setValue("Failed to save profile: " + throwable.getMessage());
                                    }
                            )
            );
        }
    }

    public void updateAvatar(String newAvatarUrl) {
        Child currentChild = childProfile.getValue();
        if (currentChild != null) {
            currentChild.setAvatarUrl(newAvatarUrl);
            avatarUrl.setValue(newAvatarUrl);

            // Auto-save avatar change
            saveProfile(currentChild.getName(), currentChild.getAge(), newAvatarUrl);
        }
    }

    public void refreshProfile() {
        loadChildProfile();
    }

    public void deleteChild() {
        // This would be a serious action requiring confirmation
        Child currentChild = childProfile.getValue();
        if (currentChild != null) {
            statusMessage.setValue("Child deletion requires admin approval");
            // In a real app, this would trigger a deletion workflow
        }
    }

    private boolean isValidProfileData(String name, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Child name cannot be empty");
            return false;
        }

        if (name.length() < Constants.MIN_CHILD_NAME_LENGTH || name.length() > Constants.MAX_CHILD_NAME_LENGTH) {
            errorMessage.setValue("Child name must be between " + Constants.MIN_CHILD_NAME_LENGTH +
                    " and " + Constants.MAX_CHILD_NAME_LENGTH + " characters");
            return false;
        }

        if (age == null || age < Constants.MIN_CHILD_AGE || age > Constants.MAX_CHILD_AGE) {
            errorMessage.setValue("Child age must be between " + Constants.MIN_CHILD_AGE +
                    " and " + Constants.MAX_CHILD_AGE + " years");
            return false;
        }

        return true;
    }

    public String getChildAge() {
        Child child = childProfile.getValue();
        return child != null && child.getAge() != null ? child.getAge().toString() : "";
    }

    public String getChildName() {
        Child child = childProfile.getValue();
        return child != null ? child.getName() : "";
    }

    public String getParentId() {
        Child child = childProfile.getValue();
        return child != null ? child.getParentId() : "";
    }

    public String getDeviceId() {
        Child child = childProfile.getValue();
        return child != null ? child.getDeviceId() : "";
    }

    public boolean isChildActive() {
        Child child = childProfile.getValue();
        return child != null && child.isActive();
    }

    public Date getProfileCreatedDate() {
        Child child = childProfile.getValue();
        return child != null ? child.getCreatedAt() : null;
    }

    public Date getProfileLastUpdated() {
        Child child = childProfile.getValue();
        return child != null ? child.getUpdatedAt() : null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}