package com.skyle.foodbuildings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class AllMapActivity extends AppCompatActivity implements OnMapReadyCallback{

    //Save a reference to the google map so that the markers can be updated after the map is loaded
    private GoogleMap temp_map;
    //Save the user location
    private FusedLocationProviderClient location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add the up button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //set the map callback that loads when the map is ready for configuration
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize the location service for later use
        location = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        temp_map = map;

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //for each food truck, add a marker at the coordinates and with the name as the title
        for (FoodTruckData.FoodTruck ft : FoodTruckData.trucks) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(ft.coordinate_x, ft.coordinate_y))
                    .title(ft.name));
        }

        //if the permission is there, set the current location as a marker, and show its title
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location l) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(l.getLatitude(), l.getLongitude()))
                            .title("Your Location"))
                            .showInfoWindow();
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }

    //the callback for when the location permissions aren't granted. Notify user that stuff might not work if they don't accept the permission.
    //Once permission is granted, reload the map using temp_map
    @Override
    public void onRequestPermissionsResult(int req, String permissions[], int[] grant) {
        switch (req) {
            case 1: {
                if (grant.length > 0 && grant[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(temp_map);
                } else {
                    Toast.makeText(this, "Some functionality may not work without location permissions", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
