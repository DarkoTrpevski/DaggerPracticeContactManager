package com.darko.daggerpracticecontactmanager.di;

import android.app.Application;

import androidx.room.Room;

import com.darko.daggerpracticecontactmanager.db.ContactsAppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class RoomModule {
    @Provides
    @Singleton
    ContactsAppDatabase provideContactsAppDatabase(Application application) {
        return Room.databaseBuilder(application, ContactsAppDatabase.class, "ContactDB").build();
    }
}