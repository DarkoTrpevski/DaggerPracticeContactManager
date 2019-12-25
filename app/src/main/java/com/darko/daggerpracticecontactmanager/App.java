package com.darko.daggerpracticecontactmanager;

import android.app.Application;

import com.darko.daggerpracticecontactmanager.di.ApplicationModule;
import com.darko.daggerpracticecontactmanager.di.ContactAppComponent;
import com.darko.daggerpracticecontactmanager.di.DaggerContactAppComponent;

public class App extends Application {

    private static App app;
    private ContactAppComponent contactAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        contactAppComponent = DaggerContactAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static App getApp() {
        return app;
    }
    public ContactAppComponent getContactAppComponent() {
        return contactAppComponent;
    }
}