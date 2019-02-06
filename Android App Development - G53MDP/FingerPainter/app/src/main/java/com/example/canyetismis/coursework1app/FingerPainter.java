package com.example.canyetismis.coursework1app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class FingerPainter extends AppCompatActivity {
    FingerPainterView fpv;
    Button selectColour;
    Button brushSize;

    static final int SELECT_COLOUR_CODE = 1;
    static final int SELECT_BRUSH_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_painter);


        fpv = findViewById(R.id.myFingerPainterView); //Finds viewer at the start

        fpv.load(getIntent().getData());
        selectColour = (Button) findViewById(R.id.selectColour);
        brushSize = (Button) findViewById(R.id.brushSize);
        String currentColour = Integer.toString(fpv.getColour());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_COLOUR_CODE){
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                int i = bundle.getInt("colour");
                fpv.setColour(i);
            }
        }
        if (requestCode == SELECT_BRUSH_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String brushType = bundle.getString("brushType");
                String brushWidth = bundle.getString("brushWidth");
                fpv.setBrush(Paint.Cap.valueOf(brushType));
                fpv.setBrushWidth(Integer.valueOf(brushWidth));
            }
        }
    }

    public void openSelectColour(View v){
        final Button selectColour = (Button) findViewById(R.id.selectColour);

        Bundle bundle = new Bundle();
        bundle.putInt("colour", fpv.getColour());

        Intent intent = new Intent(FingerPainter.this,
                SelectColour.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, SELECT_COLOUR_CODE);
    }

    public void openBrushSize(View v){
        final Button brushSize = (Button) findViewById(R.id.brushSize);

        String brushType = fpv.getBrush().name();
        String brushWidth = Integer.toString(fpv.getBrushWidth());

        Bundle brushTypeBundle = new Bundle();
        brushTypeBundle.putString("brushType", brushType);
        brushTypeBundle.putString("brushWidth", brushWidth);

        Intent intent = new Intent(FingerPainter.this,
                BrushSize.class);
        intent.putExtras(brushTypeBundle);
        startActivityForResult(intent, SELECT_BRUSH_CODE);
    }
}
