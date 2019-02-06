package com.example.canyetismis.runningtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.style.TtsSpan;
import android.util.Log;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class TrackingService extends Service {

    private TrackingServiceBinder binder = new TrackingServiceBinder();
    private final String CHANNEL_ID = "200";
    int NOTIFICATION_ID = 002;

    LocationManager locationManager;
    MyLocationListener locationListener;

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private ContentResolver resolver;

    private double[] locations = new double[2];

    Location prevLocation = null;
    float totalDist;
    float currentDist;
    String distance;
    float totalDur;
    String duration;
    float speed;
    float avgSpeed;
    float topSpeed;
    float temp;
    int numberOfLocations = 0;
    long time = 0;
    String date;

    float tmp = 0;
    float maxSpeed = 0;


    @Override
    public void onCreate(){
        super.onCreate();

        Context context = TrackingService.this;
        //gets the content resolver and Database Helper
        resolver = this.getContentResolver();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();

        //Initialises the location listener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
        stopGPS();
    }

    //Location listener
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.d("g53mdp", location.getLatitude() + " " + location.getLongitude());
            //Gets the current latitude and longitude and broadcasts it to the Maps Activity
            locations[0] = location.getLatitude();
            locations[1] = location.getLongitude();
            broadcastToMaps();

            //stores the last recorded time for the session
            time = location.getTime();
            //keeps the location count for Average Speed calculation
            numberOfLocations++;
            //Calculates the total distance and total duration
            if(prevLocation == null){
                totalDist = 0;
                totalDur = 0;
            } else {
                //Calculates the total distance travelled
                currentDist = prevLocation.distanceTo(location);
                totalDist += round(currentDist/1000,3);
                //additional statement to counter rounding errors
                totalDist = round(totalDist,3);
                distance = String.valueOf(totalDist) + " kilometers";

                totalDur += (location.getTime() - prevLocation.getTime())/1000;
                duration = convertDuration(Math.round(totalDur));
            }

            //Calculates the Current Speed
            if(prevLocation != null){
                speed = (prevLocation.distanceTo(location))/((location.getTime() - prevLocation.getTime())/1000);
            } else {
                speed = 0;
            }

            //Calculates the Maximum Speed
            topSpeed = setMaximumSpeed(speed);

            //Calculates the Average Speed
            temp += speed;
            avgSpeed = temp/numberOfLocations;
            avgSpeed = round(avgSpeed,1);
            //Updates the previous location
            prevLocation = location;
            broadcastToMain();

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            locationManager = null;
            locationListener = null;
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }
        //Sends a broadcast to Main Activity, containing information about Average Speed, Maximum Speed
        //Total Distance and Total Duration
        public void broadcastToMain(){
            Intent intent = new Intent();
            intent.setAction("com.example.canyetismis.runningtracker.MAIN_ACTIVITY_RECEIVER");
            intent.putExtra("totalDistance", distance);
            intent.putExtra("totalDuration", duration);
            intent.putExtra("topSpeed", topSpeed);
            intent.putExtra("avgSpeed", avgSpeed);
            LocalBroadcastManager.getInstance(TrackingService.this).sendBroadcast(intent);
        }
        //Sends a broadcast to Maps Activity, containing information about latitude and longitude of the
        //Current Location
        public void broadcastToMaps(){
            Intent intent = new Intent();
            intent.setAction("com.example.canyetismis.runningtracker.MY_MAPS_RECEIVER");
            intent.putExtra("locations", locations);
            sendBroadcast(intent);

        }
        //Sets the value for Maximum Speed of the session
        private float setMaximumSpeed(float speed){
            if(tmp == 0){
                tmp = speed;
                maxSpeed = speed;
                //a statement to handle GPS inaccuracies (with the test data it determined the top speed to be 120 km/h)
                if(temp >= 20){
                    temp = 0;
                    maxSpeed = 0;
                }
            } else {
                tmp = speed;
                if(maxSpeed < tmp && tmp < 20){
                    maxSpeed = tmp;
                }
            }
            return round(maxSpeed,1);
        }
        //Rounding Function for float values
        private float round(float number, int decimalPlace) {
            BigDecimal bd = new BigDecimal(number);
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd.floatValue();
        }
        //Converts duration to hour, minute, second format
        private String convertDuration(int totalSecs){
            int hours = totalSecs / 3600;
            int minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            String str = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            return str;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        locationManager.removeUpdates(locationListener);
        return true;
    }
    //Service binder
    public class TrackingServiceBinder extends Binder {
        //Method called at the start of the session
        public void startRunning(){
            TrackingService.this.createNotificationChannel();
            TrackingService.this.startGPS();
        }
        //Method called when the session is terminated
        public void stopRunning(){
            TrackingService.this.stopNotification();
            TrackingService.this.stopGPS();
            // Converts the last recorded epoch time of the session to a String date format
            date = epochToDate(time);
            //Inserts the session information to the database
            insertSession();

        }
        //Method called for creating notifications
        public void createNotificationChannel(){
            TrackingService.this.createNotificationChannel();
        }
        //Inserts the session information to the database
        private void insertSession(){
            ContentValues newValues = new ContentValues();
            newValues.put(DBProviderContract.AVG_SPEED, avgSpeed);
            newValues.put(DBProviderContract.MAX_SPEED, topSpeed);
            newValues.put(DBProviderContract.TOTAL_DISTANCE, distance);
            newValues.put(DBProviderContract.TOTAL_DURATION, duration);
            newValues.put(DBProviderContract.DATE, date);
            newValues.put(DBProviderContract.TIME, time);
            resolver.insert(DBProviderContract.URI_MAIN, newValues);
        }
        //Converts epoch time to date format
        private String epochToDate(long epoch){
            Instant instant = Instant.ofEpochSecond(epoch/1000);
            ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/London"));
            String str = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss").format(date);
            Log.d("epoch", str);
            return str;
        }

    }
    //Creates a notification
    private void createNotificationChannel(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Running Tracker";
            String description = "Recording...";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                    CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Running Tracker")
                    .setContentText("Running...")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            startForeground(NOTIFICATION_ID, mBuilder.build());
        }

    }
    //Stops the notification and the service
    private void stopNotification(){
        stopForeground(true);
        stopSelf();
    }

    //starts the location listener
    private void startGPS(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
    }
    //stops the location listener
    private void stopGPS(){
        locationManager.removeUpdates(locationListener);
    }
}


