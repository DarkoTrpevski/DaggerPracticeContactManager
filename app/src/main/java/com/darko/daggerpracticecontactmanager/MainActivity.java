package com.darko.daggerpracticecontactmanager;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.darko.daggerpracticecontactmanager.adapters.ContactsAdapter;
import com.darko.daggerpracticecontactmanager.db.ContactsAppDatabase;
import com.darko.daggerpracticecontactmanager.db.datamodel.Contact;
import com.darko.daggerpracticecontactmanager.utils.AppExecutors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    public static final String DB_NAME = "ContactDB";

    private ContactsAdapter contactsAdapter;
    private List<Contact> contactArrayList = new ArrayList<>();

    @Inject
    public ContactsAppDatabase contactsAppDatabase;

    AppExecutors appExecutors = AppExecutors.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        App.getApp().getContactAppComponent().inject(this);

        bindView();
        addAllContacts();

        FloatingActionButton fabAddOrEditContact = findViewById(R.id.fab);
        fabAddOrEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrEditContacts(false, null, -1);
            }
        });
    }

    private void bindView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_contacts);
        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        contactsAppDatabase = Room.databaseBuilder(this, ContactsAppDatabase.class, DB_NAME).build();
    }

    public void addOrEditContacts(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdate ? "Add New Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            newContact.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
        }

        alertDialogBuilderUserInput.setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton(isUpdate ? "Delete" : "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        if (isUpdate) {
                            deleteContact(contact, position);
                        } else {
                            dialogBox.cancel();
                        }
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(newContact.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter contact name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                if (isUpdate && contact != null) {
                    updateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);
                } else {
                    createContact(newContact.getText().toString(), contactEmail.getText().toString());
                }
            }
        });
    }

    private void addAllContacts() {
        appExecutors.diskIo.execute(new Runnable() {
            @Override
            public void run() {
                contactArrayList.addAll(contactsAppDatabase.getContactDAO().getContacts());
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void deleteContact(Contact contact, int position) {
        contactArrayList.remove(position);
        new DeleteContactAsyncTask().execute(contact);
    }

    private class DeleteContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDatabase.getContactDAO().deleteContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private void updateContact(String name, String email, int position) {
        final Contact contact = contactArrayList.get(position);
        contact.setName(name);
        contact.setEmail(email);
        new UpdateContactAsyncTask().execute(contact);
    }

    private class UpdateContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            contactsAppDatabase.getContactDAO().updateContact(contacts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private void createContact(final String name, final String email) {
        new CreateContactAsyncTask().execute(new Contact(0, name, email));

//        appExecutors.diskIo.execute(new Runnable() {
//            @Override
//            public void run() {
//                long id = contactsAppDatabase.getContactDAO().addContact(new Contact(0, name, email));
//                Contact contact = contactsAppDatabase.getContactDAO().getContact(id);
//                if (contact != null) {
//                    contactArrayList.add(0, contact);
//                }
//            }
//        });
//        AppExecutors.getInstance().mainThread.execute();
    }

    private class CreateContactAsyncTask extends AsyncTask<Contact, Void, Void> {
        @Override
        protected Void doInBackground(Contact... contacts) {
            long id = contactsAppDatabase.getContactDAO().addContact(contacts[0]);
            Contact contact = contactsAppDatabase.getContactDAO().getContact(id);
            if (contact != null) {
                contactArrayList.add(0, contact);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}