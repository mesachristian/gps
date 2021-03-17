package com.smartgenix.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private TextView latitudeView, longitudeView, elevationView, distanceView;
    private RecyclerView locationsRecyclerView;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Button saveBtn;

    private List<com.smartgenix.gps.Location> locations;
    private List<JSONObject> locationsJSON;

    public static final int RADIUS_OF_EARTH_KM = 6371;
    public static final double LAT_DORADO = 4.7016454391405755;
    public static final double LON_DORADO = -74.14606501681753;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locations = new ArrayList<>();
        locationsJSON = new ArrayList<>();
        latitudeView = findViewById(R.id.latitude_field);
        longitudeView = findViewById(R.id.longitude_field);
        elevationView = findViewById(R.id.elevation_field);
        distanceView = findViewById(R.id.distance_field);
        saveBtn = findViewById(R.id.save_locations_button);

        locationsRecyclerView = findViewById(R.id.locations_recycler_view);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = createLocationRequest();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeJSONObject();
                locations.clear();
                locationsJSON.clear();
                Toast.makeText(MainActivity.this, "Datos Guardados!", Toast.LENGTH_SHORT).show();
            }
        });

        checkGPSPermissions();

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                if(location != null){
                    updateUI(location);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void startLocationUpdates(){
        int gpsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(gpsPermission == PackageManager.PERMISSION_GRANTED && coarsePermission == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void checkGPSPermissions(){
        int gpsPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(gpsPermission != PackageManager.PERMISSION_GRANTED && coarsePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2000);
        }
    }

    private void updateUI(Location location){
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        String elevation = String.valueOf(location.getAltitude());

        latitudeView.setText(latitude);
        longitudeView.setText(longitude);
        elevationView.setText(elevation);

        double distanceAirport = distance(LAT_DORADO, LON_DORADO, location.getLatitude(), location.getLongitude());

        String distanceText = "Distancia al aeropuerto: " + String.valueOf(distanceAirport) +"km";
        distanceView.setText(distanceText);

        String strDate = new Date(System.currentTimeMillis()).toString();;

        com.smartgenix.gps.Location locationObject = new com.smartgenix.gps.Location(latitude, longitude, strDate);
        locationsJSON.add(locationObject.toJSON());
        locations.add(locationObject);

        // Actualizar el recycler view
        LocationsListAdapter locationsListAdapter = new LocationsListAdapter(getApplicationContext(), locations);
        locationsRecyclerView.setAdapter(locationsListAdapter);
    }

    private void writeJSONObject() {
        Writer output = null;
        String filename = "locations.json";

        try {
            File file = new File(getBaseContext().getExternalFilesDir(null), filename);
            output = new BufferedWriter(new FileWriter(file));
            output.write(locationsJSON.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Log error
        }
    }

    public double distance(double lat1, double long1, double lat2, double long2){
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance /   2) * Math.sin(latDistance / 2)+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))* Math.sin(lngDistance /   2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result*100.0)/100.0;
    }
}
