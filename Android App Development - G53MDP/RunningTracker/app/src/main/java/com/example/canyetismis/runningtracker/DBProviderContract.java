package com.example.canyetismis.runningtracker;

import android.net.Uri;

public class DBProviderContract {
    final static String AUTHORITY = "com.example.canyetismis.runningtracker.DBContentProvider";
    final static Uri URI_MAIN = Uri.parse("content://" + AUTHORITY);

    public static final String TABLE_NAME = "myList";

    public static final String _ID = "_id";
    public static final String AVG_SPEED = "avgSpeed";
    public static final String MAX_SPEED = "maxSpeed";
    public static final String TOTAL_DISTANCE = "totalDistance";
    public static final String TOTAL_DURATION = "totalDuration";
    public static final String DATE = "date";
    public static final String TIME = "time";

}
