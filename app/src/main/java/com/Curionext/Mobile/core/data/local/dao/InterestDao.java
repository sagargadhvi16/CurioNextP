package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.InterestEntity;

import java.util.List;
import java.util.Date;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface InterestDao {

    @Query("SELECT * FROM interests WHERE child_id = :childId ORDER BY interest_level DESC")
    Single<List<InterestEntity>> getInterestsByChild(String childId);

    @Query("SELECT * FROM interests WHERE child_id = :childId ORDER BY interest_level DESC LIMIT :limit")
    Single<List<InterestEntity>> getTopInterestsByChild(String childId, int limit);

    @Query("SELECT * FROM interests WHERE child_id = :childId AND category = :category ORDER BY interest_level DESC")
    Single<List<InterestEntity>> getInterestsByChildAndCategory(String childId, String category);

    @Query("SELECT * FROM interests WHERE child_id = :childId AND trend_direction = :trendDirection")
    Single<List<InterestEntity>> getInterestsByTrend(String childId, String trendDirection);

    @Query("SELECT * FROM interests WHERE child_id = :childId AND interest_level >= :minLevel ORDER BY interest_level DESC")
    Single<List<InterestEntity>> getInterestsAboveLevel(String childId, double minLevel);

    @Query("SELECT * FROM interests WHERE child_id = :childId AND last_explored >= :sinceDate ORDER BY last_explored DESC")
    Single<List<InterestEntity>> getRecentlyExploredInterests(String childId, Date sinceDate);

    @Query("SELECT DISTINCT category FROM interests WHERE child_id = :childId ORDER BY category")
    Single<List<String>> getInterestCategories(String childId);

    @Query("SELECT * FROM interests WHERE id = :interestId")
    Single<InterestEntity> getInterestById(String interestId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertInterest(InterestEntity interest);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertInterests(List<InterestEntity> interests);

    @Update
    Completable updateInterest(InterestEntity interest);

    @Delete
    Completable deleteInterest(InterestEntity interest);

    @Query("DELETE FROM interests WHERE id = :interestId")
    Completable deleteInterestById(String interestId);

    @Query("DELETE FROM interests WHERE child_id = :childId")
    Completable deleteAllInterestsByChild(String childId);

    @Query("DELETE FROM interests")
    Completable deleteAllInterests();

    @Query("UPDATE interests SET interest_level = :newLevel, last_explored = :exploredDate WHERE id = :interestId")
    Completable updateInterestLevel(String interestId, double newLevel, Date exploredDate);

    @Query("UPDATE interests SET frequency = frequency + 1, last_explored = :exploredDate WHERE id = :interestId")
    Completable incrementInterestFrequency(String interestId, Date exploredDate);

    @Query("UPDATE interests SET trend_direction = :trendDirection WHERE id = :interestId")
    Completable updateInterestTrend(String interestId, String trendDirection);

    @Query("SELECT COUNT(*) FROM interests WHERE child_id = :childId")
    Single<Integer> getInterestCount(String childId);

    @Query("SELECT AVG(interest_level) FROM interests WHERE child_id = :childId")
    Single<Double> getAverageInterestLevel(String childId);

    @Query("SELECT * FROM interests WHERE child_id = :childId AND topic LIKE '%' || :searchTerm || '%' ORDER BY interest_level DESC")
    Single<List<InterestEntity>> searchInterestsByTopic(String childId, String searchTerm);
}