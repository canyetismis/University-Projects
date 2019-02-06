package com.example.canyetismis.coursework3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class AddRecipe extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;

    EditText recipeTitle;
    EditText recipeDescription;
    RatingBar ratingBar;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        recipeTitle = (EditText) findViewById(R.id.recipeTitle);
        recipeDescription = (EditText) findViewById(R.id.recipeDescription);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        saveButton = (Button) findViewById(R.id.saveButton);
    }

    public void onSaveButtonClicked(View v){
        String title = recipeTitle.getText().toString();
        String description = recipeDescription.getText().toString();
        Float rating = ratingBar.getRating();

        ContentValues newValues = new ContentValues();
        newValues.put(MyProviderContract.TITLE, title);
        newValues.put(MyProviderContract.DESCRIPTION, description);
        newValues.put(MyProviderContract.RATING, rating);

        getContentResolver().insert(MyProviderContract.RECIPE_URI, newValues);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

    }
}
