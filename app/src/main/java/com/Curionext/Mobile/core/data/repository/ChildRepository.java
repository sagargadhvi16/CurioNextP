package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.local.dao.ChildDao;
import com.curionext.mobile.core.data.model.Child;
import com.curionext.mobile.core.data.model.WeeklySummary;
import com.curionext.mobile.core.network.ApiService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class ChildRepository {

    private final ApiService apiService;
    private final ChildDao childDao;

    @Inject
    public ChildRepository(ApiService apiService, ChildDao childDao) {
        this.apiService = apiService;
        this.childDao = childDao;
    }

    public Single<Child> getChildProfile(String childId) {
        return apiService.getChildProfile(childId)
                .doOnSuccess(child -> {
                    // Cache in local database
                    // childDao.insertChild(child);
                });
    }

    public Single<Child> updateChildProfile(String childId, Child child) {
        return apiService.updateChildProfile(childId, child)
                .doOnSuccess(updatedChild -> {
                    // Update local cache
                    // childDao.updateChild(updatedChild);
                });
    }

    public Single<WeeklySummary> getWeeklySummary(String childId) {
        return apiService.getWeeklySummary(childId);
    }
}
