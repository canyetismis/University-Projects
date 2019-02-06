package com.example.canyetismis.coursework3;

import android.net.Uri;

public class MyProviderContract {
    public static final String AUTHORITY = "com.example.canyetismis.coursework3.MyContentProvider";

    public static final Uri RECIPE_URI = Uri.parse("content://"+AUTHORITY+"/myList");
    public static final Uri SINGLE_RECIPE_URI = Uri.parse("content://"+AUTHORITY+"/myList/#");

    public static final String TABLE_NAME = "myList";

    public static final String _ID = "_id";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String RATING = "rating";
}
