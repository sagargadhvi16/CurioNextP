package com.curionext.mobile;

import android.app.Application;
import com.curionext.mobile.core.di.AppComponent;
import com.curionext.mobile.core.di.DaggerAppComponent;
import com.curionext.mobile.core.di.AppModule;

public class CurioNextApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
