package com.example.canyetismis.coursework1app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BrushSize extends AppCompatActivity {
    Button round;
    Button square;
    Button small;
    Button medium;
    Button large;
    Button saveChanges;

    TextView brushShapeDisplay;
    TextView brushSizeDisplay;

    String brushType;
    String brushWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brush_size);

        brushShapeDisplay = (TextView) findViewById(R.id.brushShapeDisplay);
        brushSizeDisplay = (TextView) findViewById(R.id.brushSizeDisplay);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            brushType = bundle.getString("brushType");
            brushWidth = bundle.getString("brushWidth");
        }

    }

    public void onRoundClicked(View v){
        brushType = "ROUND";
        brushShapeDisplay.setText("Round");
    }

    public void onSquareClicked(View v){
        brushType = "SQUARE";
        brushShapeDisplay.setText("Square");
    }

    public void onSmallClicked(View v){
        brushWidth = "10";
        brushSizeDisplay.setText("Small");
    }

    public void onMediumClicked(View v){
        brushWidth = "20";
        brushSizeDisplay.setText("Medium");
    }

    public void onLargeClicked(View v){
        brushWidth = "40";
        brushSizeDisplay.setText("Large");
    }

    public void onSaveChanges(View v){
        Bundle brushTypeBundle = new Bundle();
        brushTypeBundle.putString("brushType", brushType);
        brushTypeBundle.putString("brushWidth", brushWidth);

        Intent intent = new Intent();
        intent.putExtras(brushTypeBundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
