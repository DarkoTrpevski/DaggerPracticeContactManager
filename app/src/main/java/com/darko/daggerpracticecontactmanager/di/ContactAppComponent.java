package com.darko.daggerpracticecontactmanager.di;

import com.darko.daggerpracticecontactmanager.MainActivity;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ContactAppComponent {
    void inject(MainActivity mainActivity);
}