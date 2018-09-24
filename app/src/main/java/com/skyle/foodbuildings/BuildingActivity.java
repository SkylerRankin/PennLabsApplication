package com.skyle.foodbuildings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BuildingActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //a key so that the extra variable names are consistent
    public static final String QUERY = "1";
    //a reference to the map for later use
    private GoogleMap temp_map;
    private boolean data_loaded = false;
    private JSONObject json;
    private FusedLocationProviderClient location;

    //loaded values
    private List<Double> lats;
    private List<Double> lngs;
    private List<String> titles;
    private List<String> links;

    @Override
    protected void onCreate(Bundle save) {
        super.onCreate(save);
        setContentView(R.layout.activity_building);

        //add the up button to the toolbar; must also set the parent activity in the manifest file
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //call an async task to make a call the the api, and put the data into the layout
        //the searched term is included to the constructor
        if (getIntent().getExtras().containsKey(BuildingActivity.QUERY)) {
            new LoadBuildingTask(this).execute((String) getIntent().getExtras().get(BuildingActivity.QUERY));
        }

        //set the callback for when the map is ready configuration
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize the location variable for use later
        location = LocationServices.getFusedLocationProviderClient(this);

        lats = new ArrayList<>();
        lngs = new ArrayList<>();
        titles = new ArrayList<>();
        links = new ArrayList<>();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        temp_map = map;

        //if the building information isn't loaded from the api yet, then don't show the markers
        if (!data_loaded)
            return;

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.setOnMarkerClickListener(this);
        //add a marker for each building but show the title of the first
        for (int i = lats.size()-1; i >= 0; --i) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lats.get(i), lngs.get(i)))
                    .title(titles.get(i)))
                    .showInfoWindow();
        }


        //if there is permission, show the users location. Otherwise, request permission with the callback
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

    //If the user grants permission, the reload the map. Otherwise, notify them that functionality may not work
    @Override
    public void onRequestPermissionsResult(int req, String permissions[], int[] grant) {
        if (req == 1) {
            if (grant.length > 0 && grant[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(temp_map);
            } else {
                Toast.makeText(this, "Some functionality may not work without location permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    //When the user clicks on a building marker on the map, update the title and link text views to reflect their selection
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getTitle().equals("Your Location")) {
            int pos = titles.indexOf(marker.getTitle());
            ((TextView) findViewById(R.id.building_name)).setText(titles.get(pos));
            ((TextView) findViewById(R.id.building_link)).setText(links.get(pos));
        }
        return false;
    }

    //An async task for pulling building data
    private class LoadBuildingTask extends AsyncTask<String, Integer, String> {

        //a reference to the activity so that Toasts can be shown
        private Activity context;

        public LoadBuildingTask(Activity c) {
            context = c;
        }

        //make the api call, and return the json as a string
        @Override
        protected String doInBackground(String... strings) {
            String res = null;
            try {
                URL url = new URL("https://api.pennlabs.org/buildings/search?q="+strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream stream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(connection.getInputStream())));
                StringBuilder s = new StringBuilder();
                try {
                    String line = reader.readLine();
                    while (line != null) {
                        s.append(line+"\n");
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Error when reaching API", Toast.LENGTH_LONG).show();
                }
                try {
                    stream.close();
                } catch (IOException e){}
                res = s.toString();
            } catch (MalformedURLException e) {
                Toast.makeText(context, "Error when reaching API", Toast.LENGTH_LONG).show();
            }catch (IOException e) {
                Toast.makeText(context, "Error when reaching API", Toast.LENGTH_LONG).show();
            }
            data_loaded = true;
            return res;
        }

        //after the pulling is done, access the UI with the json string from the background function
        @Override
        protected void onPostExecute(String raw) {
            try {
                json = new JSONObject(raw);
                JSONArray root = (JSONArray) json.get("result_data");
                lats.clear();
                lngs.clear();
                titles.clear();
                links.clear();

                //if there are multiple results for a query, then add all of them
                for (int i = 0; i < root.length(); ++i) {
                    JSONObject result = (JSONObject) root.get(i);
                    lats.add(i, Double.parseDouble((String)result.get("latitude")));
                    lngs.add(i, Double.parseDouble((String)result.get("longitude")));
                    titles.add(i, (String) result.get("title"));
                    links.add(i, (String) result.get("http_link"));
                }

                //display the first result in the layout first
                if (root.length() > 0) {
                    ((TextView) context.findViewById(R.id.building_name)).setText(titles.get(0));
                    ((TextView) context.findViewById(R.id.building_link)).setText(links.get(0));
                    onMapReady(temp_map);
                } else {
                    ((TextView) context.findViewById(R.id.building_name)).setText(R.string.no_match_label);
                    ((TextView) context.findViewById(R.id.building_link)).setText("");
                }

            } catch (JSONException e) {
                ((TextView) context.findViewById(R.id.building_name)).setText(R.string.no_match_label);
                ((TextView) context.findViewById(R.id.building_link)).setText("");
            }
        }
    }
}
