package com.example.canyetismis.coursework1app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SelectColour extends AppCompatActivity {
    Button red;
    Button green;
    Button blue;
    Button yellow;
    Button black;

    TextView current;

    int colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_colour);

        current = (TextView) findViewById(R.id.current);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            colour = bundle.getInt("colour");
            current.setBackgroundColor(colour);
        }
    }

    public void onRedClicked(View v){
        colour = 0xFFFF0000;
        current.setBackgroundColor(0xFFFF0000);
    }

    public void onGreenClicked(View v){
        colour = 0xFF00FF00;
        current.setBackgroundColor(0xFF00FF00);
    }

    public void onBlueClicked(View v){
        colour = 0xFF0000FF;
        current.setBackgroundColor(0xFF0000FF);
    }

    public void onYellowClicked(View v){
        colour = 0xFFFFFF00;
        current.setBackgroundColor(0xFFFFFF00);
    }

    public void onBlackClicked(View v){
        colour = 0xFF000000;
        current.setBackgroundColor(0xFF000000);
    }

    public void saveChangesClicked(View v){
        Bundle bundle = new Bundle();
        bundle.putInt("colour", colour);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}
