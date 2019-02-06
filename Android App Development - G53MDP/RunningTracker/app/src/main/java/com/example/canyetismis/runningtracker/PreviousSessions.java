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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PreviousSessions extends AppCompatActivity {

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private ContentResolver resolver;
    private String orderBy = " DESC";
    private String param = DBProviderContract.TIME;

    static final int DETAILS_ACTIVITY_CODE = 3;

    ListView listView;
    SimpleCursorAdapter dataAdapter;
    Button backButton, first, last, orderDistance, orderDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_sessions);
        //Gets the content resolver and the database helper
        Context context = PreviousSessions.this;
        resolver = this.getContentResolver();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        //Finds the buttons
        backButton =(Button) findViewById(R.id.backButton);
        first = (Button) findViewById(R.id.first);
        last = (Button) findViewById(R.id.last);
        orderDistance = (Button) findViewById(R.id.orderDistance);
        orderDuration = (Button) findViewById(R.id.orderDuration);

        populate();
    }
    //Method for populating the listview
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
        //query to retrieve the data, sort order is determined by onClick() methods bellow
        final Cursor cursor = resolver.query(DBProviderContract.URI_MAIN, projection,null , null, param + orderBy);
        // Colums that are displayed on the list
        String[] columns = new String[]{
                DBProviderContract.TOTAL_DISTANCE,
                DBProviderContract.TOTAL_DURATION,
                DBProviderContract.DATE
        };
        //Finds the column id's on the list view layout (which is called row layout)
        int[] to = new int[]{
            R.id.totalDist,
            R.id.totalDur,
            R.id.date
        };
        //data adapter to populate the listview
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.row_layout,
                cursor,
                columns,
                to,
    0);
        //finds the listview and populates it
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
        //Sets on click listener on list view items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                //Gets the id of the selected item and sends it to the Details Activity/Starts the Details activivty
                Intent intent = new Intent(PreviousSessions.this,
                        Details.class);
                int id = cursor.getInt(cursor.getColumnIndex(DBProviderContract._ID));
                intent.putExtra("id", id);
                startActivityForResult(intent, DETAILS_ACTIVITY_CODE);
            }
        });
    }
    //Sorts the list from first insertion date to last insertion date
    public void sortFirstClicked(View v){
        orderBy = " ASC";
        param = DBProviderContract.TIME;
        populate();
    }
    //Sorts the list from last insertion dates to first insertion date
    public void sortLastClicked(View v) {
        orderBy = " DESC";
        param = DBProviderContract.TIME;
        populate();
    }
    //Sorts the list by the longest distance to shortest
    public void sortDistanceClicked(View v){
        orderBy = " DESC";
        param = DBProviderContract.TOTAL_DISTANCE;
        populate();
    }
    //Sorts the list by the longest session duration to shortest
    public void sortDurationClicked(View v){
        orderBy = " DESC";
        param = DBProviderContract.TOTAL_DURATION;
        populate();

    }

    //Sends user back to the main activity and destroys the activity
    public void onBackButtonClicked(View v){
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    //Populates the list view again when resumed
    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }
}
