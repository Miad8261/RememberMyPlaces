package com.johnabbottcollege.androidteamproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;



public class RestaurantFragment extends Fragment {
    public static final String TAG = "Restaurant Fragment";

    DatabaseReference databaseRestaurant;

    ListView listViewRestaurant;
    ArrayList<Restaurant> RestaurantList;

    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_restaurant_fragmen, container, false);


        databaseRestaurant = FirebaseDatabase.getInstance().getReference().child("Restaurant");

        listViewRestaurant = (ListView) view.findViewById(R.id.lv_restaurant_location);
        RestaurantList = new ArrayList<>();


        fetchData();
        return view;
    }

    private void fetchData() {
        databaseRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RestaurantList.clear();
                for (DataSnapshot RestaurantSnapshot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> ds2 = RestaurantSnapshot.getChildren();
                    Restaurant Restaurant = ds2.iterator().next().getValue(Restaurant.class);
                    //Restaurant.setAddress((String)RestaurantSnapshot.child("address").getValue());
                    RestaurantList.add(Restaurant);

                }
                RestaurantListAdapter adapter = new RestaurantListAdapter(getActivity(), RestaurantList);
                //attaching adapter to the listview
                listViewRestaurant.setAdapter(adapter);
                // arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
