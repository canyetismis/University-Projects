package com.example.canyetismis.coursework3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recipeDB";
    public static final int DATABASE_VERSION = 1;

    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_RATING = "rating";

    public DBHelper(Context context) {
        super(context, "recipeDB", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE myList (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR(128), " +
                "description TEXT(4096), " +
                "rating FLOAT(2,2)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS myList");
        onCreate(db);
    }
}
