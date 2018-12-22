package com.johnabbottcollege.androidteamproject;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import static com.johnabbottcollege.androidteamproject.R.id.save_parking;
import static com.johnabbottcollege.androidteamproject.R.id.save_restaurant;
import static com.johnabbottcollege.androidteamproject.R.id.save_shopping;


public class PlacesActivity extends AppCompatActivity implements OnMapReadyCallback  {
    private static final String TAG = "PlacesActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 13;
    private GoogleMap mMap;

    LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    private Marker marker;
    Button btnPacking, btnRestaurant, btnShopping;
    private MyLatLngAddress myLatLngAddress = new MyLatLngAddress();

    DatabaseReference databaseLocations;
    DatabaseReference databaseParkingAddress;
    DatabaseReference databaseRestaurantAddress;
    DatabaseReference databaseShoppingAddress;
    DatabaseReference databaseLatLngAddress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
             getSupportFragmentManager().getFragment(savedInstanceState, "ParkingFragment");
        }

        Intent intent = getIntent();

        databaseLocations = FirebaseDatabase.getInstance().getReference("Locations")
                .child(intent.getStringExtra(MainActivity.USER_ID));
        databaseParkingAddress = FirebaseDatabase.getInstance().getReference("Parking")
                .child(intent.getStringExtra(MainActivity.USER_ID));
        databaseRestaurantAddress = FirebaseDatabase.getInstance().getReference("Restaurant")
                .child(intent.getStringExtra(MainActivity.USER_ID));
        databaseShoppingAddress = FirebaseDatabase.getInstance().getReference("Shopping")
                .child(intent.getStringExtra(MainActivity.USER_ID));
        databaseLatLngAddress = FirebaseDatabase.getInstance().getReference("LatLngAddress")
                .child(intent.getStringExtra(MainActivity.USER_ID));


        getlocationPermission();
        btnPacking=findViewById(R.id.btn_parking);
        btnRestaurant = findViewById(R.id.btn_restaurant);
        btnShopping = findViewById(R.id.btn_shopping);

        btnPacking.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                float lat, lng;


                lat = (float) myLatLngAddress.getLatitude();
                lng = (float) myLatLngAddress.getLongtitude();
                Log.d(TAG, lat + " " + lng + " " + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                Bundle bundle = new Bundle();
                bundle.putFloat("Latitude",lat);
                bundle.putFloat("Longitude",lng);


                PackingFragment fragment = new PackingFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Transaction start
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        btnRestaurant.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                RestaurantFragment fragment = new RestaurantFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Transaction start
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        btnShopping.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                ShoppingFragment fragment = new ShoppingFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Transaction start
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.save_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        float lat, lng;
        Calendar calendar;
        String address;
        String currentDate;
        String category;
        switch (item.getItemId()) {

            case save_parking:
                lat = (float) myLatLngAddress.getLatitude();
                lng = (float) myLatLngAddress.getLongtitude();
                address = myLatLngAddress.getAddress();
                category = "parking";

                calendar = Calendar.getInstance();
                currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                myLatLngAddress.setDate(calendar.getTime());
                Log.d(TAG, lat + " " + lng + " " + address + " " + currentDate + " " + myLatLngAddress.getDate() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                saveToLocationsDb(currentDate, address, category, lat, lng);
                saveToParkingDb(currentDate, address, category);
                saveToLatLntDb(lat, lng, address);
                // Write a message to the database


                break;
            case save_restaurant:
                lat = (float) myLatLngAddress.getLatitude();
                lng = (float) myLatLngAddress.getLongtitude();
                address = myLatLngAddress.getAddress();
                category = "restaurant";

                calendar = Calendar.getInstance();
                currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                myLatLngAddress.setDate(calendar.getTime());
                Log.d(TAG, lat + " " + lng + " " + address + " " + currentDate + " " + myLatLngAddress.getDate() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                saveToLocationsDb(currentDate, address, category, lat, lng);
                saveToRestaurantDb(currentDate, address, category);

                // Write a message to the database

                break;
            case save_shopping:
                lat = (float) myLatLngAddress.getLatitude();
                lng = (float) myLatLngAddress.getLongtitude();
                address = myLatLngAddress.getAddress();
                category = "shopping";

                calendar = Calendar.getInstance();
                currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                myLatLngAddress.setDate(calendar.getTime());
                Log.d(TAG, lat + " " + lng + " " + address + " " + currentDate + " " + myLatLngAddress.getDate() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                saveToLocationsDb(currentDate, address, category, lat, lng);
                saveToShoppingDb(currentDate, address, category);

                // Write a message to the database
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToLatLntDb(float lat, float lng, String address) {
        String id = databaseLatLngAddress.push().getKey();
        MyLatLngAddress myLatLngAddress = new MyLatLngAddress(lat,lng, address);
        databaseLatLngAddress.child(id).setValue(myLatLngAddress);
    }

    private void saveToShoppingDb(String currentDate, String address, String category) {
        String id = databaseShoppingAddress.push().getKey();
        Shopping shopping = new Shopping(id, currentDate, address);
        Toast.makeText(this,category,Toast.LENGTH_SHORT).show();
        databaseShoppingAddress.child(id).setValue(shopping);
    }

    private void saveToRestaurantDb(String currentDate, String address, String category) {
        String id = databaseRestaurantAddress.push().getKey();
        Restaurant restaurant = new Restaurant(id, currentDate, address);
        Toast.makeText(this,category,Toast.LENGTH_SHORT).show();
        databaseRestaurantAddress.child(id).setValue(restaurant);
    }

    private void saveToParkingDb(String currentDate, String address, String category) {
        String id = databaseParkingAddress.push().getKey();
        Parking parking = new Parking(id, currentDate, address);
        Toast.makeText(this,category,Toast.LENGTH_SHORT).show();
        databaseParkingAddress.child(id).setValue(parking);
    }

    private void saveToLocationsDb(String currentDate, String address, String category, float lat, float lng) {
        String id = databaseLocations.push().getKey();
        Locations locations = new Locations(id, currentDate, address, category, lat, lng);
        Toast.makeText(this,category,Toast.LENGTH_SHORT).show();
        databaseLocations.child(id).setValue(locations);



    }

//    private void saveToFireBase(String currentDate, String address, double lat, double lng) {
//       String id =  databaseUser.push().getKey();
//      Locations locations = new Locations( id, lat, lng, address, currentDate);
//      databaseLocation.child(id).setValue(locations);
//
//      Toast.makeText(this,"Location added",Toast.LENGTH_SHORT).show();
//
//
//    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            //this is from miad
                            double myCurrrentLatitude = currentLocation.getLatitude();
                            double myCurrrentLongitude = currentLocation.getLongitude();
                              //for two point distance only
                            myLatLngAddress.setLatitude(myCurrrentLatitude);
                            myLatLngAddress.setLongtitude(myCurrrentLongitude);
                            //my orginal
                          //  moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                             moveCamera(new LatLng(myCurrrentLatitude, myCurrrentLongitude), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(PlacesActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamers: moving the camera to : 1at: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
        } else
            marker.setPosition(latLng);
        String detailAddress = getCityName(latLng);
        Toast.makeText(this, detailAddress, Toast.LENGTH_LONG).show();
    }


    private void initMap() {
        Log.d(TAG, "initialize Map OK");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(PlacesActivity.this);
    }

    private void getlocationPermission() {
        Log.d(TAG, "Location Permission is OK");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "OnRequestPermissionResult: is called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++)
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "OnRequestPermissionsResult : permission failed");
                            return;
                        }
                }
                Log.d(TAG, "OnRequestPermission Granted");
                mLocationPermissionsGranted = true;

                //from My loation     *****************************************************************************
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Location mCurrentLocation = locationResult.getLastLocation();
                        LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                        String cityName = getCityName(myCoordinates);
                        Toast.makeText(PlacesActivity.this, cityName, Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 13.0f));
                        if (marker == null) {
                            marker = mMap.addMarker(new MarkerOptions().position(myCoordinates));
                        } else
                            marker.setPosition(myCoordinates);
                    }
                };


                //************************************************************************************************
                //initialize our map
                initMap();
            }

        }
    }

    // from My location *******************************************************************************
    private String getCityName(LatLng myCoordinates) {
        String myCity = "";
        Geocoder geocoder = new Geocoder(PlacesActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            myCity = addresses.get(0).getLocality();
            Log.d("mylog", "Complete Address: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + addresses.toString());
            Log.d("mylog", "Address: " + address);

            myLatLngAddress = new MyLatLngAddress(myCoordinates.latitude, myCoordinates.longitude, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myCity;
    }
    //****************************************************************************************************

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_LONG).show();
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }


}

