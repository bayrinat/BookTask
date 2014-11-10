package com.example.nefisTask;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewContact extends Activity {
    private long rowID;
    private TextView nameTextView;
    private TextView birthdayTextView;
    private TextView genderTextView;
    private TextView phoneTextView;
    private TextView emailTextView;

    // called when the Activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        birthdayTextView = (TextView) findViewById(R.id.birthdayTextView);
        genderTextView = (TextView) findViewById(R.id.genderTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);

        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(AddressBook.ROW_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadContactTask().execute(rowID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.editItem:
                Intent addEditContact =
                        new Intent(this, AddEditContact.class);
                addEditContact.putExtra(AddressBook.ROW_ID, rowID);
                addEditContact.putExtra("name", nameTextView.getText());
                addEditContact.putExtra("birthday", birthdayTextView.getText());
                addEditContact.putExtra("gender", genderTextView.getText());
                addEditContact.putExtra("phone", phoneTextView.getText());
                addEditContact.putExtra("email", emailTextView.getText());
                startActivity(addEditContact); // start the Activity

                return true;
            case R.id.deleteItem:
                deleteContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteContact() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ViewContact.this);

        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);

        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(ViewContact.this);

                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>() {
                                    @Override
                                    protected Object doInBackground(Long... params) {
                                        databaseConnector.deleteContact(params[0]);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result) {
                                        finish();
                                    }
                                };
                        deleteTask.execute(rowID);
                    } // end method onClick
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }

    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(ViewContact.this);

        @Override
        protected Cursor doInBackground(Long... params) {
            databaseConnector.open();

            return databaseConnector.getOneContact(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);

            result.moveToFirst();

            int nameIndex = result.getColumnIndex("name");
            int birthdayIndex = result.getColumnIndex("birthday");
            int genderIndex = result.getColumnIndex("gender");
            int phoneIndex = result.getColumnIndex("phone");
            int emailIndex = result.getColumnIndex("email");

            nameTextView.setText(result.getString(nameIndex));
            birthdayTextView.setText(result.getString(birthdayIndex));
            genderTextView.setText(result.getString(genderIndex));
            phoneTextView.setText(result.getString(phoneIndex));
            emailTextView.setText(result.getString(emailIndex));

            result.close();
            databaseConnector.close();
        }
    }
}
