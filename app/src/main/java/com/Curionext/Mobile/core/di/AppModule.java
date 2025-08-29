package com.curionext.mobile.core.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.curionext.mobile.core.data.repository.ChildRepository;
import com.curionext.mobile.core.data.repository.InterestRepository;
import com.curionext.mobile.core.data.repository.LocationRepository;
import com.curionext.mobile.core.data.repository.NotificationRepository;
import com.curionext.mobile.core.data.repository.PreferenceRepository;
import com.curionext.mobile.core.data.repository.SafetyRepository;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("curionext_prefs", Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    ChildRepository provideChildRepository() {
        return new ChildRepository();
    }

    @Provides
    @Singleton
    InterestRepository provideInterestRepository() {
        return new InterestRepository();
    }

    @Provides
    @Singleton
    LocationRepository provideLocationRepository() {
        return new LocationRepository();
    }

    @Provides
    @Singleton
    NotificationRepository provideNotificationRepository() {
        return new NotificationRepository();
    }

    @Provides
    @Singleton
    PreferenceRepository providePreferenceRepository() {
        return new PreferenceRepository();
    }

    @Provides
    @Singleton
    SafetyRepository provideSafetyRepository() {
        return new SafetyRepository();
    }
}
