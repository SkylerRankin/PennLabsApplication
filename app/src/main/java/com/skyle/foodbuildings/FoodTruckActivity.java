package com.skyle.foodbuildings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class FoodTruckActivity extends AppCompatActivity implements OnMapReadyCallback {

    //a variable so that extra variable names are consistent across classes
    public final static String NAME = "1";

    //the position of the current food truck in the data file
    private int pos;

    private FusedLocationProviderClient location;

    //a reference to the map for later use
    private GoogleMap temp_map;

    @Override
    protected void onCreate(Bundle save) {
        super.onCreate(save);
        setContentView(R.layout.activity_food_truck);

        //add the up button to the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //populate the layout with all the data based on the pos variable
        if (getIntent().getExtras().containsKey(FoodTruckActivity.NAME)) {
            pos = (Integer) (getIntent().getExtras().get(FoodTruckActivity.NAME));
            ((TextView) findViewById(R.id.truck_detail_name)).setText(FoodTruckData.trucks[pos].name);
            ((TextView) findViewById(R.id.truck_detail_address)).setText(FoodTruckData.trucks[pos].address);
            ((TextView) findViewById(R.id.truck_detail_description)).setText(FoodTruckData.trucks[pos].description);
            ((RatingBar) findViewById(R.id.truck_detail_rating)).setRating((float) FoodTruckData.trucks[pos].rating);
            if (FoodTruckData.trucks[pos].hours != null) {
                ((TextView) findViewById(R.id.truck_detail_hours)).setText(FoodTruckData.trucks[pos].hours);
            } else {
                ((TextView) findViewById(R.id.truck_detail_hours)).setText("Hours not listed");
            }
            if (FoodTruckData.trucks[pos].phone != null) {
                ((TextView) findViewById(R.id.truck_detail_phone)).setText(FoodTruckData.trucks[pos].phone);
            } else {
                ((TextView) findViewById(R.id.truck_detail_phone)).setText("Phone not listed");
            }
        }

        //set the map callback
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize the location object for later use
        location = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        temp_map = map;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //add a marker for the food truck, and display its title
        map.addMarker(new MarkerOptions()
                .position(new LatLng(FoodTruckData.trucks[pos].coordinate_x, FoodTruckData.trucks[pos].coordinate_y))
                .title(FoodTruckData.trucks[pos].name))
                .showInfoWindow();

        //if there is permission, show the user location. Otherwise, request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location l) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(l.getLatitude(), l.getLongitude()))
                            .title("Your Location"));

                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }

    //If permission was granted, the reload the map
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
