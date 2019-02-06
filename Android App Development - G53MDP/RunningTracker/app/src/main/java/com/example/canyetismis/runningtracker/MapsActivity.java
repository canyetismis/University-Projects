package com.example.canyetismis.runningtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double[] location;
    Receiver receiver = new Receiver();
    Button backButton;
    //Local Broadcat receiver in maps activity
    public class Receiver extends BroadcastReceiver{
        private final String ACTION = "com.example.canyetismis.runningtracker.MY_MAPS_RECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ACTION.equals(intent.getAction())){
                //gets the coordinates from the location listener to display marker on the map
                location = intent.getDoubleArrayExtra("locations");
                //Sets coordinates of the latLgn object
                LatLng latLng = new LatLng(location[0], location[1]);
                //clears the maps
                mMap.clear();
                //Positions the camera on the current location, zooms in and places a marker
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Sets the intent filter to receive broadcast
        IntentFilter filter = new IntentFilter("com.example.canyetismis.runningtracker.MY_MAPS_RECEIVER");
        registerReceiver(receiver, filter);
        backButton = (Button) findViewById(R.id.backButton);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Sets the intent filter to receive broadcast
        IntentFilter filter = new IntentFilter("com.example.canyetismis.runningtracker.MY_MAPS_RECEIVER");
        registerReceiver(receiver, filter);
    }
    //Sends user back to the main activity
    public void onBackClicked(View v){
        Intent newIntent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, newIntent);
        super.onBackPressed();
    }
}
