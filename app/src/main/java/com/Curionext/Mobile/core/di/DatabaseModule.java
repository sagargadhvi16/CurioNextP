package com.curionext.mobile.core.di;

import android.content.Context;
import androidx.room.Room;

import com.curionext.mobile.core.data.local.CurioNextDatabase;
import com.curionext.mobile.core.data.local.dao.ChildDao;
import com.curionext.mobile.core.data.local.dao.InterestDao;
import com.curionext.mobile.core.data.local.dao.LocationDao;
import com.curionext.mobile.core.data.local.dao.NotificationDao;
import com.curionext.mobile.core.data.local.dao.PreferenceDao;
import com.curionext.mobile.core.data.local.dao.SafeZoneDao;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    CurioNextDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                CurioNextDatabase.class,
                "curionext_database"
        ).build();
    }

    @Provides
    ChildDao provideChildDao(CurioNextDatabase database) {
        return database.childDao();
    }

    @Provides
    InterestDao provideInterestDao(CurioNextDatabase database) {
        return database.interestDao();
    }

    @Provides
    LocationDao provideLocationDao(CurioNextDatabase database) {
        return database.locationDao();
    }

    @Provides
    NotificationDao provideNotificationDao(CurioNextDatabase database) {
        return database.notificationDao();
    }

    @Provides
    PreferenceDao providePreferenceDao(CurioNextDatabase database) {
        return database.preferenceDao();
    }

    @Provides
    SafeZoneDao provideSafeZoneDao(CurioNextDatabase database) {
        return database.safeZoneDao();
    }
}
