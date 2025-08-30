package com.curionext.mobile.core.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.curionext.mobile.core.data.local.entity.ChildEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface ChildDao {

    @Query("SELECT * FROM children WHERE child_id = :childId")
    Single<ChildEntity> getChildById(String childId);

    @Query("SELECT * FROM children WHERE parent_id = :parentId AND is_active = 1")
    Single<List<ChildEntity>> getActiveChildrenByParent(String parentId);

    @Query("SELECT * FROM children WHERE device_id = :deviceId")
    Single<ChildEntity> getChildByDeviceId(String deviceId);

    @Query("SELECT * FROM children WHERE is_active = 1")
    Single<List<ChildEntity>> getAllActiveChildren();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertChild(ChildEntity child);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertChildren(List<ChildEntity> children);

    @Update
    Completable updateChild(ChildEntity child);

    @Delete
    Completable deleteChild(ChildEntity child);

    @Query("UPDATE children SET is_active = 0 WHERE child_id = :childId")
    Completable deactivateChild(String childId);

    @Query("UPDATE children SET device_id = :deviceId WHERE child_id = :childId")
    Completable updateDeviceId(String childId, String deviceId);

    @Query("UPDATE children SET avatar_url = :avatarUrl WHERE child_id = :childId")
    Completable updateAvatarUrl(String childId, String avatarUrl);

    @Query("DELETE FROM children WHERE child_id = :childId")
    Completable deleteChildById(String childId);

    @Query("DELETE FROM children")
    Completable deleteAllChildren();

    @Query("SELECT COUNT(*) FROM children WHERE parent_id = :parentId AND is_active = 1")
    Single<Integer> getActiveChildrenCount(String parentId);

    @Query("SELECT EXISTS(SELECT 1 FROM children WHERE child_id = :childId AND parent_id = :parentId)")
    Single<Boolean> childBelongsToParent(String childId, String parentId);
}