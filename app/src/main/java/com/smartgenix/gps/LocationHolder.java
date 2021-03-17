package com.smartgenix.gps;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationHolder extends RecyclerView.ViewHolder {

    private Location location;
    private TextView locationDataView;

    public LocationHolder(@NonNull View itemView) {
        super(itemView);
        locationDataView = itemView.findViewById(R.id.location_data);
    }

    public void bind(Location location){
        this.location = location;
        String data = "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude() + " on " + location.getDate();
        locationDataView.setText(data);
    }

}
