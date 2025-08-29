package com.curionext.mobile.core.data.repository;

import com.curionext.mobile.core.data.local.dao.NotificationDao;
import com.curionext.mobile.core.data.model.NotificationItem;
import com.curionext.mobile.core.network.ApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;

@Singleton
public class NotificationRepository {

    private final ApiService apiService;
    private final NotificationDao notificationDao;

    @Inject
    public NotificationRepository(ApiService apiService, NotificationDao notificationDao) {
        this.apiService = apiService;
        this.notificationDao = notificationDao;
    }

    public Single<List<NotificationItem>> getNotifications(String childId) {
        return apiService.getNotifications(childId)
                .doOnSuccess(notifications -> {
                    // Cache in local database
                    // notificationDao.insertNotifications(notifications);
                });
    }

    public Single<List<NotificationItem>> getRecentNotifications(String childId, int limit) {
        return apiService.getNotifications(childId)
                .map(notifications -> {
                    // Sort by timestamp and return recent items
                    notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                    return notifications.subList(0, Math.min(limit, notifications.size()));
                });
    }

    public Single<Void> markAsRead(String notificationId) {
        return apiService.markNotificationAsRead(notificationId);
    }
}
