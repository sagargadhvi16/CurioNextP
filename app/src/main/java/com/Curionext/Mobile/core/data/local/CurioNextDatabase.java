package com.curionext.mobile.core.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.curionext.mobile.core.data.local.dao.ChildDao;
import com.curionext.mobile.core.data.local.dao.InterestDao;
import com.curionext.mobile.core.data.local.dao.LocationDao;
import com.curionext.mobile.core.data.local.dao.NotificationDao;
import com.curionext.mobile.core.data.local.dao.PreferenceDao;
import com.curionext.mobile.core.data.local.dao.SafeZoneDao;
import com.curionext.mobile.core.data.local.entity.ChildEntity;
import com.curionext.mobile.core.data.local.entity.InterestEntity;
import com.curionext.mobile.core.data.local.entity.LocationEntity;
import com.curionext.mobile.core.data.local.entity.NotificationEntity;
import com.curionext.mobile.core.data.local.entity.PreferenceEntity;
import com.curionext.mobile.core.data.local.entity.SafeZoneEntity;
import com.curionext.mobile.core.data.local.converter.DateConverter;

@Database(
        entities = {
                ChildEntity.class,
                InterestEntity.class,
                LocationEntity.class,
                NotificationEntity.class,
                PreferenceEntity.class,
                SafeZoneEntity.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class CurioNextDatabase extends RoomDatabase {
    public abstract ChildDao childDao();
    public abstract InterestDao interestDao();
    public abstract LocationDao locationDao();
    public abstract NotificationDao notificationDao();
    public abstract PreferenceDao preferenceDao();
    public abstract SafeZoneDao safeZoneDao();
}
