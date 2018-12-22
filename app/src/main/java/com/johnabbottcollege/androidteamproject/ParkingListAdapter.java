package com.johnabbottcollege.androidteamproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ParkingListAdapter extends ArrayAdapter<Parking> {
private Activity context;
List<Parking>parkingList;

//    public ParkingListAdapter(Activity context, List<Parking> parkingList) {
//        super(context, R.layout.location_item,parkingList);
//        this.context = context;
//        this.parkingList = parkingList;
//    }

    public ParkingListAdapter(Activity context, int location_item, ArrayList<Parking> parkingList) {
        super(context, location_item, parkingList);
        this.context = context;
        this.parkingList = parkingList;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.location_item,null,true);

        TextView tvdate = (TextView)listViewItem.findViewById(R.id.tv_date);

        TextView tvAddress = listViewItem.findViewById(R.id.tv_address);

        Parking parking = parkingList.get(position);
        tvdate.setText(parking.getCurrentDate());
        tvAddress.setText(parking.getAddress());

        return listViewItem;


    }
}
