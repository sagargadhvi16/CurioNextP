package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.NotificationEntity;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE child_id = :childId ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsByChild(String childId);

    @Query("SELECT * FROM notifications WHERE child_id = :childId ORDER BY timestamp DESC LIMIT :limit")
    Single<List<NotificationEntity>> getRecentNotificationsByChild(String childId, int limit);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND is_read = 0 ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getUnreadNotificationsByChild(String childId);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND type = :type ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsByType(String childId, String type);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND category = :category ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsByCategory(String childId, String category);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND priority = :priority ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsByPriority(String childId, String priority);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND timestamp >= :sinceDate ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsSince(String childId, Date sinceDate);

    @Query("SELECT * FROM notifications WHERE child_id = :childId AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    Single<List<NotificationEntity>> getNotificationsBetween(String childId, Date startDate, Date endDate);

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    Single<NotificationEntity> getNotificationById(String notificationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotification(NotificationEntity notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotifications(List<NotificationEntity> notifications);

    @Update
    Completable updateNotification(NotificationEntity notification);

    @Delete
    Completable deleteNotification(NotificationEntity notification);

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    Completable deleteNotificationById(String notificationId);

    @Query("DELETE FROM notifications WHERE child_id = :childId")
    Completable deleteAllNotificationsByChild(String childId);

    @Query("DELETE FROM notifications WHERE expires_at < :currentDate")
    Completable deleteExpiredNotifications(Date currentDate);

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    Completable markAsRead(String notificationId);

    @Query("UPDATE notifications SET is_read = 1 WHERE child_id = :childId")
    Completable markAllAsReadByChild(String childId);

    @Query("UPDATE notifications SET is_read = 1 WHERE child_id = :childId AND type = :type")
    Completable markTypeAsReadByChild(String childId, String type);

    @Query("SELECT COUNT(*) FROM notifications WHERE child_id = :childId AND is_read = 0")
    Single<Integer> getUnreadCount(String childId);

    @Query("SELECT COUNT(*) FROM notifications WHERE child_id = :childId AND priority = 'urgent' AND is_read = 0")
    Single<Integer> getUrgentUnreadCount(String childId);

    @Query("SELECT COUNT(*) FROM notifications WHERE child_id = :childId")
    Single<Integer> getTotalNotificationCount(String childId);

    @Query("SELECT DISTINCT type FROM notifications WHERE child_id = :childId ORDER BY type")
    Single<List<String>> getNotificationTypes(String childId);

    @Query("SELECT DISTINCT category FROM notifications WHERE child_id = :childId ORDER BY category")
    Single<List<String>> getNotificationCategories(String childId);

    @Query("DELETE FROM notifications")
    Completable deleteAllNotifications();
}