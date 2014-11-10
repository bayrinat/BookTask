package com.example.nefisTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseConnector {
    private static final String DATABASE_NAME = "Contacts";
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context) {
        databaseOpenHelper =
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null)
            database.close();
    }

    public void insertContact(String name, String birthday, String gender,
                              String phone, String email) {
        ContentValues newContact = new ContentValues();
        newContact.put("name", name);
        newContact.put("birthday", birthday);
        newContact.put("gender", gender);
        newContact.put("phone", phone);
        newContact.put("email", email);
        open();
        database.insert("contacts", null, newContact);
        close();
    }

    public void updateContact(long id, String name, String birthday, String gender,
                              String phone, String email) {
        ContentValues editContact = new ContentValues();
        editContact.put("name", name);
        editContact.put("birthday", birthday);
        editContact.put("gender", gender);
        editContact.put("phone", phone);
        editContact.put("email", email);
        open();
        database.update("contacts", editContact, "_id=" + id, null);
        close();
    }

    public Cursor getAllContacts() {
        return database.query("contacts", new String[]{"_id", "name", "birthday", "gender",
                "phone", "email"}, null, null, null, null, "name");
    }

    public Cursor getOneContact(long id) {
        return database.query(
                "contacts", null, "_id=" + id, null, null, null, null);
    }

    public void deleteContact(long id) {
        open();
        database.delete("contacts", "_id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {
        public DatabaseOpenHelper(Context context, String name,
                                  CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE contacts" +
                    "(_id integer primary key autoincrement," +
                    "name TEXT, birthday TEXT, gender TEXT, phone TEXT, email TEXT);";
            String insertQuery = "INSERT INTO contacts VALUES(" +
                    "0,'Иванов И.И.', '01.01.1999', 'Мужской', '+79059127525', 'ivan@ya.ru');";
            db.execSQL(createQuery);
            db.execSQL(insertQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // nothing to do
        }
    }
}

