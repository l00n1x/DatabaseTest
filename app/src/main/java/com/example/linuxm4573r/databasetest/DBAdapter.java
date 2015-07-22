package com.example.linuxm4573r.databasetest;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by James Seelbach on 7/17/2015.
 * This class is used for doing database work in MainActivity.java
 * using the SQLiteOpenHelper class.
 */
public class DBAdapter extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    //name of database.  This is also a file found in
    // the data path of the app plus /databases/myDatabase.db
    //For my device it is /data/data/com.example.linuxm4573r.databasetest/databases/myDatabase.db
    public static final String DATABASE_NAME = "myDatabase.db";


    //This is the string that will be passed to the SQL engine itself.
    //This will create a table with parameters set in DatabaseEntry.java
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseEntry.TABLE_NAME + "(key "+DatabaseEntry.KEY_TYPE+", value "+DatabaseEntry.VALUE_TYPE+
            ")";

    //This is the string that will be passed to the SQL engine itself.
    //This will delete the table.
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME;

    //Constructor.  Just read up on SQLiteOpenHelper to look into this.
    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    //This is the method that creates the database.
    //NOTE: this will run only ONCE in order to "create" the database.
    //Each time you execute the application, this will not execute
    //unless the database version is higher.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //This runs when the database exists and the version is higher.
    //It simply deletes the old database
    //and creates the new one.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //delete old table and create a new one.
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
