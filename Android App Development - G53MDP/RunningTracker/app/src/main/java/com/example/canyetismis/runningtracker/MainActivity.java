package com.example.canyetismis.runningtracker;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final int MAPS_ACTIVITY_CODE = 1;
    static final int SESSIONS_ACTIVITY_CODE = 2;
    private final String ACTION = "com.example.canyetismis.runningtracker.MAIN_ACTIVITY_RECEIVER";

    Button startRunning,
            stopRunning,
            showLocation,
            showRecords;
    TextView avgSpeedCurrent,
            topSpeedCurrent,
            totalDistanceCurrent,
            totalDurationCurrent;

    TrackingService.TrackingServiceBinder binder = null;
    private Intent serviceIntent;

    float avgSpeed;
    float topSpeed;
    float totalDist;
    float totalDur;
    String duration;
    String distance;

    Receiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("receiverBroadcast", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finds the buttons
        startRunning = (Button) findViewById(R.id.startRunning);
        stopRunning = (Button) findViewById(R.id.stopRunning);
        showLocation = (Button) findViewById(R.id.showLocation);
        showRecords = (Button) findViewById(R.id.showRecords);
        //Finds the TextViews that displays information about the current session
        avgSpeedCurrent = (TextView) findViewById(R.id.avgSpeedCurrent);
        topSpeedCurrent = (TextView) findViewById(R.id.topSpeedCurrent);
        totalDistanceCurrent = (TextView) findViewById(R.id.totalDistanceCurrent);
        totalDurationCurrent = (TextView) findViewById(R.id.totalDurationCurrent);
        //sets an intent filter for the broadcast receiver
        IntentFilter filter = new IntentFilter(ACTION);
        receiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        //Binds to the service class
        serviceIntent = new Intent(this,TrackingService.class);
        this.startService(serviceIntent);
        this.bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
        //Asks permission to user to use location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    //Service connection to the Tracking Service
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TrackingService.TrackingServiceBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };
    //Broadcast receiver that listens to the location listener on the service
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION.equals(intent.getAction())) {
                Log.d("receiverBroadcast", "in");

                //Sets the value for average speed
                avgSpeed = intent.getFloatExtra("avgSpeed", 0);

                //sets the value for Maximum Speed
                topSpeed = intent.getFloatExtra("topSpeed", 0);

                //sets the value for Total Distance
                distance = intent.getStringExtra("totalDistance");
                if(distance == null){
                    distance = "0.000 kilometers";
                }

                //sets the value for Session Duration:
                duration = intent.getStringExtra("totalDuration");
                if(duration == null){
                    duration = "00:00:00";
                }
                updateText();
            }
        }
    };
    //Updates the TextViews when called and displays information about the current session
    private void updateText(){
        Log.d("updateText", "in");
        avgSpeedCurrent.setText("Average Speed: " + avgSpeed + "m/s");
        topSpeedCurrent.setText("Maximum Speed: " + topSpeed + "m/s");
        totalDistanceCurrent.setText("Total Distance: " + distance);
        totalDurationCurrent.setText("Time Elapsed: " + duration);
    }
    //Start Button
    public void onStartClicked(View v){
        startRunning.setEnabled(false);
        stopRunning.setEnabled(true);
        //Creates notification and starts location listener
        binder.startRunning();
    }
    //Stop Button
    public void onStopClicked(View v){
        startRunning.setEnabled(true);
        stopRunning.setEnabled(false);
        //Stops the notification and location listener
        binder.stopRunning();
        //resets the local values that stores the information about current session
        avgSpeed = 0;
        topSpeed = 0;
        totalDist = 0;
        totalDur = 0;
        //resets the TextViews at the end of the session
        avgSpeedCurrent.setText("Average Speed: 0.0 m/s");
        topSpeedCurrent.setText("Maximum Speed: 0.0 m/s");
        totalDistanceCurrent.setText("Total Distance: 0.000 kilometers");
        totalDurationCurrent.setText("Time Elapsed: 00:00:00");
    }
    //Display Location Button
    public void onShowLocationClicked(View v){
        Intent newIntent = new Intent(this,MapsActivity.class);
        startActivityForResult(newIntent, MAPS_ACTIVITY_CODE);
    }
    //Show Previous Sessions Button
    public void onShowRecordsClicked(View v){
        Intent newIntent = new Intent(this,PreviousSessions.class);
        startActivityForResult(newIntent, SESSIONS_ACTIVITY_CODE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Unbinds from the service and the broadcat receiver
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        binder.createNotificationChannel();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
