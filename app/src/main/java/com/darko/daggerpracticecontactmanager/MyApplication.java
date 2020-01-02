package com.darko.daggerpracticecontactmanager;

import android.app.Application;

import com.darko.daggerpracticecontactmanager.di.ApplicationModule;
import com.darko.daggerpracticecontactmanager.di.ContactAppComponent;
import com.darko.daggerpracticecontactmanager.di.DaggerContactAppComponent;

public class MyApplication extends Application {

    private static MyApplication myApplication;
    private ContactAppComponent contactAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        contactAppComponent = DaggerContactAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static MyApplication getMyApplication() {
        return myApplication;
    }

    public ContactAppComponent getContactAppComponent() {
        return contactAppComponent;
    }
}