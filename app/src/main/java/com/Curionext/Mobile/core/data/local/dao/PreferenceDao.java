package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.PreferenceEntity;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface PreferenceDao {

    @Query("SELECT * FROM preferences WHERE child_id = :childId ORDER BY confidence DESC, ABS(sentiment) DESC")
    Single<List<PreferenceEntity>> getPreferencesByChild(String childId);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND sentiment > 0.1 ORDER BY sentiment DESC, confidence DESC LIMIT :limit")
    Single<List<PreferenceEntity>> getTopLikes(String childId, int limit);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND sentiment < -0.1 ORDER BY sentiment ASC, confidence DESC LIMIT :limit")
    Single<List<PreferenceEntity>> getTopDislikes(String childId, int limit);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND category = :category ORDER BY confidence DESC")
    Single<List<PreferenceEntity>> getPreferencesByCategory(String childId, String category);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND trend = :trend ORDER BY last_updated DESC")
    Single<List<PreferenceEntity>> getPreferencesByTrend(String childId, String trend);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND confidence >= :minConfidence ORDER BY sentiment DESC")
    Single<List<PreferenceEntity>> getHighConfidencePreferences(String childId, double minConfidence);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND intensity >= :minIntensity ORDER BY intensity DESC")
    Single<List<PreferenceEntity>> getStrongPreferences(String childId, double minIntensity);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND source = :source ORDER BY last_updated DESC")
    Single<List<PreferenceEntity>> getPreferencesBySource(String childId, String source);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND last_updated >= :sinceDate ORDER BY last_updated DESC")
    Single<List<PreferenceEntity>> getRecentPreferences(String childId, Date sinceDate);

    @Query("SELECT * FROM preferences WHERE id = :preferenceId")
    Single<PreferenceEntity> getPreferenceById(String preferenceId);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND topic = :topic")
    Single<PreferenceEntity> getPreferenceByTopic(String childId, String topic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPreference(PreferenceEntity preference);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPreferences(List<PreferenceEntity> preferences);

    @Update
    Completable updatePreference(PreferenceEntity preference);

    @Delete
    Completable deletePreference(PreferenceEntity preference);

    @Query("DELETE FROM preferences WHERE id = :preferenceId")
    Completable deletePreferenceById(String preferenceId);

    @Query("DELETE FROM preferences WHERE child_id = :childId")
    Completable deleteAllPreferencesByChild(String childId);

    @Query("DELETE FROM preferences")
    Completable deleteAllPreferences();

    @Query("UPDATE preferences SET sentiment = :newSentiment, confidence = :newConfidence, frequency = frequency + 1, last_updated = :updatedDate WHERE id = :preferenceId")
    Completable updatePreferenceSentiment(String preferenceId, double newSentiment, double newConfidence, Date updatedDate);

    @Query("UPDATE preferences SET trend = :trend WHERE id = :preferenceId")
    Completable updatePreferenceTrend(String preferenceId, String trend);

    @Query("UPDATE preferences SET frequency = frequency + 1, last_updated = :updatedDate WHERE id = :preferenceId")
    Completable incrementFrequency(String preferenceId, Date updatedDate);

    @Query("SELECT COUNT(*) FROM preferences WHERE child_id = :childId")
    Single<Integer> getPreferenceCount(String childId);

    @Query("SELECT COUNT(*) FROM preferences WHERE child_id = :childId AND sentiment > 0.1")
    Single<Integer> getLikesCount(String childId);

    @Query("SELECT COUNT(*) FROM preferences WHERE child_id = :childId AND sentiment < -0.1")
    Single<Integer> getDislikesCount(String childId);

    @Query("SELECT AVG(sentiment) FROM preferences WHERE child_id = :childId")
    Single<Double> getAverageSentiment(String childId);

    @Query("SELECT AVG(confidence) FROM preferences WHERE child_id = :childId")
    Single<Double> getAverageConfidence(String childId);

    @Query("SELECT DISTINCT category FROM preferences WHERE child_id = :childId ORDER BY category")
    Single<List<String>> getPreferenceCategories(String childId);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND topic LIKE '%' || :searchTerm || '%' ORDER BY confidence DESC")
    Single<List<PreferenceEntity>> searchPreferencesByTopic(String childId, String searchTerm);

    @Query("SELECT * FROM preferences WHERE child_id = :childId AND keywords LIKE '%' || :keyword || '%' ORDER BY confidence DESC")
    Single<List<PreferenceEntity>> searchPreferencesByKeyword(String childId, String keyword);
}