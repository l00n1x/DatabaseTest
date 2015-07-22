package com.example.linuxm4573r.databasetest;

import android.provider.BaseColumns;

/**
 * Created by James Seelbach on 7/18/2015.
 */
    //This is just used to keep the structure of the table we're using in SQL
    //In a convenient location.  It is also similar to the example on the
    //Android Developer site.

public abstract class DatabaseEntry implements BaseColumns {
    public static final String TABLE_NAME = "settings";
    public static final String KEY_TYPE = "TEXT";
    public static final String VALUE_TYPE = "TEXT";
}
