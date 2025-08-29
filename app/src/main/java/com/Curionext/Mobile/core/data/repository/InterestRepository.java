package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.local.dao.InterestDao;
import com.curionext.mobile.core.data.model.Interest;
import com.curionext.mobile.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class InterestRepository {

    private final ApiService apiService;
    private final InterestDao interestDao;

    @Inject
    public InterestRepository(ApiService apiService, InterestDao interestDao) {
        this.apiService = apiService;
        this.interestDao = interestDao;
    }

    public Single<List<Interest>> getChildInterests(String childId) {
        return apiService.getChildInterests(childId)
                .doOnSuccess(interests -> {
                    // Cache in local database
                    // interestDao.insertInterests(interests);
                });
    }

    public Single<List<Interest>> getTopInterests(String childId, int limit) {
        return apiService.getChildInterests(childId)
                .map(interests -> {
                    // Sort by interest level and return top items
                    interests.sort((a, b) -> Double.compare(b.getInterestLevel(), a.getInterestLevel()));
                    return interests.subList(0, Math.min(limit, interests.size()));
                });
    }

    public Single<List<Interest>> getInterestTrends(String childId, String period) {
        return apiService.getInterestTrends(childId, period);
    }
}
