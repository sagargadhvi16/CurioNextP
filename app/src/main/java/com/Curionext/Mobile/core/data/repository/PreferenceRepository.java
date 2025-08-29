package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.local.dao.PreferenceDao;
import com.curionext.mobile.core.data.model.Preference;
import com.curionext.mobile.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class PreferenceRepository {

    private final ApiService apiService;
    private final PreferenceDao preferenceDao;

    @Inject
    public PreferenceRepository(ApiService apiService, PreferenceDao preferenceDao) {
        this.apiService = apiService;
        this.preferenceDao = preferenceDao;
    }

    public Single<List<Preference>> getPreferences(String childId) {
        return apiService.getPreferences(childId)
                .doOnSuccess(preferences -> {
                    // Cache in local database
                    // preferenceDao.insertPreferences(preferences);
                });
    }

    public Single<List<Preference>> getPreferenceAnalysis(String childId) {
        return apiService.getPreferenceAnalysis(childId);
    }

    public Single<List<Preference>> getTopLikes(String childId, int limit) {
        return getPreferences(childId)
                .map(preferences -> {
                    return preferences.stream()
                            .filter(Preference::isPositive)
                            .sorted((a, b) -> Double.compare(b.getSentiment(), a.getSentiment()))
                            .limit(limit)
                            .collect(java.util.stream.Collectors.toList());
                });
    }

    public Single<List<Preference>> getTopDislikes(String childId, int limit) {
        return getPreferences(childId)
                .map(preferences -> {
                    return preferences.stream()
                            .filter(Preference::isNegative)
                            .sorted((a, b) -> Double.compare(a.getSentiment(), b.getSentiment()))
                            .limit(limit)
                            .collect(java.util.stream.Collectors.toList());
                });
    }
}
