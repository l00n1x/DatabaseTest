package com.example.linuxm4573r.databasetest;

import android.provider.BaseColumns;

/**
 * Created by linuxm4573r on 7/18/2015.
 */
public abstract class DatabaseEntry implements BaseColumns {
    public static final String TABLE_NAME = "settings";
    public static final String KEY_TYPE = "TEXT";
    public static final String VALUE_TYPE = "TEXT";
}
