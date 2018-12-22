package com.johnabbottcollege.androidteamproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.johnabbottcollege.androidteamproject.MapRoute.FetchURL;
import com.johnabbottcollege.androidteamproject.MapRoute.TaskLoadedCallback;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;

    Button getDirection;
    private Polyline currentPolyline;

    public static final LatLng place11 = new LatLng(45.40613, -73.9421);
    public static final LatLng place22 = new LatLng(45.403859,  -73.951470);


    public MarkerOptions place1 = new MarkerOptions().position(place11);
    public MarkerOptions place2 = new MarkerOptions().position(place22);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getDirection = findViewById(R.id.btnGetDirection);

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");

                new FetchURL(MapActivity.this).execute(url, "driving");
//                new FetchURL(MapActivity.this).execute(getUrl (place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });
        //27.658143,85.3199503
        //27.667491,85.3208583
//        place1 = new MarkerOptions().position(new LatLng(45.40613, -73.9421)).title("Location 1");
//        place2 = new MarkerOptions().position(new LatLng(45.5043, -73.5496)).title("Location 2");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Markers Added.", Toast.LENGTH_SHORT).show();
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place11)
                .zoom(15)
                .bearing(0)
                .tilt(30)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" +
                parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}

