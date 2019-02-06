package com.example.canyetismis.coursework3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    static final int ADD_RECIPE_CODE = 1;
    static final int VIEW_RECIPE_CODE = 2;

    DBHelper dbHelper;
    SQLiteDatabase db;
    SimpleCursorAdapter dataAdapter;
    ListView listView;
    String orderBy = MyProviderContract.TITLE + " ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        populate();
    }

    public void onAddRecipeClicked(View v){
        Intent intent = new Intent(MainActivity.this,
                AddRecipe.class);
        startActivityForResult(intent, ADD_RECIPE_CODE);
    }

    public void onTitleClicked(View v){
        orderBy = MyProviderContract.TITLE + " ASC";
        populate();
    }

    public void onRatingClicked(View v){
        orderBy = MyProviderContract.RATING + " DESC";
        populate();
    }

    public void populate(){
        String[] projection = new String[]{
                MyProviderContract._ID,
                MyProviderContract.TITLE,
                MyProviderContract.RATING
        };

        final Cursor cursor = getContentResolver().query(MyProviderContract.RECIPE_URI, projection, null, null, orderBy);

        String[] columns = new String[] {
                dbHelper.KEY_TITLE,
                dbHelper.KEY_RATING
        };

        int[] to = new int[] {
                R.id.recipeTitle,
                R.id.rating,
        };

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.row_layout,
                cursor,
                columns,
                to,
                0);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Intent intent = new Intent(MainActivity.this,
                        ViewRecipe.class);
                Long id = cursor.getLong(cursor.getColumnIndex(MyProviderContract._ID));
                intent.putExtra("id", id);
                startActivityForResult(intent, VIEW_RECIPE_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populate();
    }

}
