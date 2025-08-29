package com.curionext.mobile.core.di;

import com.curionext.mobile.features.dashboard.DashboardActivity;
import com.curionext.mobile.features.location.LocationTrackingActivity;
import com.curionext.mobile.features.interests.InterestAnalysisActivity;
import com.curionext.mobile.features.notifications.NotificationActivity;
import com.curionext.mobile.features.preferences.PreferenceAnalysisActivity;
import com.curionext.mobile.features.profile.ChildProfileActivity;
import com.curionext.mobile.features.safety.SafetySettingsActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, DatabaseModule.class})
public interface AppComponent {
    void inject(DashboardActivity activity);
    void inject(LocationTrackingActivity activity);
    void inject(InterestAnalysisActivity activity);
    void inject(NotificationActivity activity);
    void inject(PreferenceAnalysisActivity activity);
    void inject(ChildProfileActivity activity);
    void inject(SafetySettingsActivity activity);
}
