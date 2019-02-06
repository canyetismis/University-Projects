package com.example.canyetismis.coursework3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class ViewRecipe extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText recipeTitle;
    EditText recipeDescription;
    RatingBar ratingBar;
    Button saveButton;

    Long id;
    String title;
    String description;
    Float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        recipeTitle = (EditText) findViewById(R.id.recipeTitle);
        recipeDescription = (EditText) findViewById(R.id.recipeDescription);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        saveButton = (Button) findViewById(R.id.saveButton);

        id = getIntent().getLongExtra("id", 1);

        queryDetails();

        recipeTitle.setText(title);
        recipeDescription.setText(description);
        ratingBar.setRating(rating);
    }

    public void queryDetails(){
        String[] projection = new String[]{
                MyProviderContract._ID,
                MyProviderContract.TITLE,
                MyProviderContract.DESCRIPTION,
                MyProviderContract.RATING
        };
        Cursor cursor = getContentResolver().query(MyProviderContract.RECIPE_URI, projection, "_id =" + id, null,null);

        if (cursor.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndex(MyProviderContract.TITLE));
            description = cursor.getString(cursor.getColumnIndex(MyProviderContract.DESCRIPTION));
            rating = cursor.getFloat(cursor.getColumnIndex(MyProviderContract.RATING));
        }
    }

    public void onSaveButtonClicked(View v){
        String titleFinal = recipeTitle.getText().toString();
        String descriptionFinal = recipeDescription.getText().toString();
        Float ratingFinal = ratingBar.getRating();

        ContentValues newValues = new ContentValues();
        newValues.put(MyProviderContract.TITLE, titleFinal);
        newValues.put(MyProviderContract.DESCRIPTION, descriptionFinal);
        newValues.put(MyProviderContract.RATING, ratingFinal);

        getContentResolver().update(MyProviderContract.RECIPE_URI, newValues, "_id =" + id, null);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onDeleteButtonClicked(View v){
        getContentResolver().delete(MyProviderContract.RECIPE_URI, "_id =" + id, null);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
