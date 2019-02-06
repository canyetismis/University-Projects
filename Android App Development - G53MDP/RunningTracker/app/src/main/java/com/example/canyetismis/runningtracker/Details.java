package com.example.canyetismis.runningtracker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Details extends AppCompatActivity {
    private TextView avgSpeed,
            topSpeed,
            totalDistance,
            totalDuration,
            sessionDate;

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private ContentResolver resolver;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Finds the Text Views that displays information about the selected session
        avgSpeed = (TextView) findViewById(R.id.avgSpeed);
        topSpeed = (TextView) findViewById(R.id.topSpeed);
        totalDistance = (TextView) findViewById(R.id.totalDistance);
        totalDuration = (TextView) findViewById(R.id.totalDuration);
        sessionDate = (TextView) findViewById(R.id.sessionDate);
        //Gets the content resolver and the database helper
        Context context = Details.this;
        resolver = this.getContentResolver();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        //Gets the ID of the item that needs to be displayed
        id = getIntent().getIntExtra("id",0);

        populate();
    }
    //Method for populating the TextViews
    private void populate(){
        //Values selected from the database
        String[] projection = new String[]{
                DBProviderContract._ID,
                DBProviderContract.AVG_SPEED,
                DBProviderContract.MAX_SPEED,
                DBProviderContract.TOTAL_DISTANCE,
                DBProviderContract.TOTAL_DURATION,
                DBProviderContract.DATE,
                DBProviderContract.TIME
        };
        //Query to retrieve the selected session
        final Cursor cursor = resolver.query(DBProviderContract.URI_MAIN, projection,DBProviderContract._ID + " = " + id , null, null);
        //Sets details about the sessions
        if(cursor.moveToFirst()){
            avgSpeed.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(DBProviderContract.AVG_SPEED))));
            topSpeed.setText(Float.toString(cursor.getFloat(cursor.getColumnIndex(DBProviderContract.MAX_SPEED))));
            totalDistance.setText(cursor.getString(cursor.getColumnIndex(DBProviderContract.TOTAL_DISTANCE)));
            totalDuration.setText(cursor.getString(cursor.getColumnIndex(DBProviderContract.TOTAL_DURATION)));
            sessionDate.setText(cursor.getString(cursor.getColumnIndex(DBProviderContract.DATE)));
        }
    }
    //Goes back to the Previous Sessions Activity and destroys the current activity
    public void onBackClicked(View v){
        Intent intent = new Intent(this, Details.class);
        setResult(RESULT_OK, intent);
        finish();
    }
    //Deletes the selected item, goes back to the Previous Sessions Activity and destroys the current activity
    public void onDeleteClicked(View v){
        resolver.delete(DBProviderContract.URI_MAIN, DBProviderContract._ID+" = " + id, null);
        Intent intent = new Intent(this, Details.class);
        setResult(RESULT_OK, intent);
        finish();
    }
}
