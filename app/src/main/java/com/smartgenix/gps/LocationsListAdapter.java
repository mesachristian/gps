package com.smartgenix.gps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationHolder> {

    private Context context;
    private List<Location> locations;

    public LocationsListAdapter(Context context, List<Location> locations){
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new LocationHolder(inflater.inflate(R.layout.location_list_item,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
