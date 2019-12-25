package com.darko.daggerpracticecontactmanager.db;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.darko.daggerpracticecontactmanager.db.datamodel.Contact;
@Database(entities = {Contact.class},version = 1, exportSchema = false)
public abstract class ContactsAppDatabase extends RoomDatabase {
    public abstract ContactDao getContactDAO();
}