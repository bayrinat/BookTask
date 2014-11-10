package com.example.nefisTask;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AddressBook extends ListActivity {
    public static final String ROW_ID = "row_id";
    OnItemClickListener viewContactListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent viewContact =
                    new Intent(AddressBook.this, ViewContact.class);
            viewContact.putExtra(ROW_ID, id);
            startActivity(viewContact);
        }
    };
    OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return true;
        }
    };
    private CursorAdapter contactAdapter;
    public final static String[] RECORDS =
            new String[]{"name", "birthday", "gender", "phone", "email"};
    public final int[] FIELDS = new int[]{R.id.name, R.id.birthday, R.id.gender,
            R.id.phone, R.id.email};

    // called when the activity is first created
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView contactListView = getListView();
        contactListView.setOnItemClickListener(viewContactListener);
        contactListView.setOnItemLongClickListener(longClickListener);
        contactAdapter = new SimpleCursorAdapter(
                AddressBook.this, R.layout.contact_list_item, null, RECORDS, FIELDS);
        setListAdapter(contactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetContactsTask().execute((Object[]) null);
    }

    @Override
    protected void onStop() {
        Cursor cursor = contactAdapter.getCursor();
        if (cursor != null)
            cursor.deactivate();
        contactAdapter.changeCursor(null);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // create a new Intent to launch the AddEditContact Activity
        Intent addNewContact =
                new Intent(AddressBook.this, AddEditContact.class);
        startActivity(addNewContact); // start Activity
        return super.onOptionsItemSelected(item);
    }

    private class GetContactsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(AddressBook.this);

        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();
            return databaseConnector.getAllContacts();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            contactAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }
}