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



public class ShoppingFragment extends Fragment {
    public static final String TAG = "Packing Fragment";

    DatabaseReference databaseShopping;

    ListView listViewShopping;
    ArrayList<Shopping> shoppingList;

    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_shopping_fragmen, container, false);


        databaseShopping = FirebaseDatabase.getInstance().getReference().child("Shopping");

        listViewShopping = (ListView) view.findViewById(R.id.lv_shopping_location);
        shoppingList = new ArrayList<>();


        fetchData();
        return view;
    }

    private void fetchData() {
        databaseShopping.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingList.clear();
                for (DataSnapshot shoppingSnapshot : dataSnapshot.getChildren()) {
                    Iterable<DataSnapshot> ds2 = shoppingSnapshot.getChildren();
                    Shopping shopping = ds2.iterator().next().getValue(Shopping.class);
                    //shopping.setAddress((String)shoppingSnapshot.child("address").getValue());
                    shoppingList.add(shopping);

                }
                ShoppingListAdapter adapter = new ShoppingListAdapter(getActivity(), shoppingList);
                //attaching adapter to the listview
                listViewShopping.setAdapter(adapter);
                // arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
