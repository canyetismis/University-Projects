package com.example.canyetismis.runningtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "trackerDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE myList(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "avgSpeed FLOAT, "+
                "maxSpeed FLOAT, "+
                "totalDistance VARCHAR(128)," +
                "totalDuration VARCHAR(128)," +
                "date VARCHAR(128)," +
                "time INTEGER" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS myList");
        onCreate(db);
    }
}


