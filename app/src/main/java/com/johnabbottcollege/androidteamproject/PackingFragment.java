package com.johnabbottcollege.androidteamproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;



public class PackingFragment extends Fragment {
    public static final String TAG = "Packing Fragment";

    DatabaseReference databaseParking;
    DatabaseReference databaseLocation;

    ListView listViewParking;
    ArrayList<Parking> parkingList;
    ArrayList<Locations>locationsArrayList;
    View view;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_parking_fragmen, container, false);


        databaseParking = FirebaseDatabase.getInstance().getReference().child("Parking");
        databaseLocation = FirebaseDatabase.getInstance().getReference().child("Locations");


        listViewParking = (ListView) view.findViewById(R.id.lv_parking_location);
        parkingList = new ArrayList<>();
        locationsArrayList = new ArrayList<>();
//        imageView = view.findViewById(R.id.imageButton);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(),PlacesActivity.class);
//                startActivity(intent);
//            }
//        });

        fetchData();
        listViewParking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentLat = parent.getItemAtPosition(position).toString();

//                Intent latIntent = new Intent(getContext(),MapActivity.class);
//                latIntent.putExtra("latitude",currentLat);
//

            }
        });

//
//       // Locations selectedLocation = locationsArrayList.get(position);
//        float oldLat = (float) selectedLocation.getLat();
//        float oldLng = (float) selectedLocation.getLng();
//        // Log.d(TAG, " old Lat and old Lng $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ +" + lat + " " + lng);


        return view;
    }



    private void fetchData() {
        databaseParking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkingList.clear();
                for (DataSnapshot parkingSnapshot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> ds2 = parkingSnapshot.getChildren();
                    Parking parking = ds2.iterator().next().getValue(Parking.class);
                    //parking.setAddress((String)parkingSnapshot.child("address").getValue());
                    parkingList.add(parking);
                }

                ParkingListAdapter adapter = new ParkingListAdapter(getActivity(),R.layout.location_item,parkingList);
                LocationListAdapter locationListAdapter = new LocationListAdapter(getActivity(),locationsArrayList);
                //attaching adapter to the listview
                listViewParking.setAdapter(adapter);
                 listViewParking.setAdapter(locationListAdapter);


                // arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        } );
        databaseLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationsArrayList.clear();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> loca = locationSnapshot.getChildren();
                    Locations locations = loca.iterator().next().getValue(Locations.class);
                    //parking.setAddress((String)parkingSnapshot.child("address").getValue());
                    locationsArrayList.add(locations);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}