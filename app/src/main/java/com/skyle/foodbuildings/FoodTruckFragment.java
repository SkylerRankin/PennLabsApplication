package com.skyle.foodbuildings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class FoodTruckFragment extends Fragment implements View.OnClickListener {

    private FusedLocationProviderClient location;

    public FoodTruckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle save) {
        super.onCreate(save);
        location = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle save) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_food_truck, container, false);

        //make the recycler view use a linear layout
        ((RecyclerView)linearLayout.findViewById(R.id.foodtruck_recycler)).setLayoutManager(new LinearLayoutManager(getActivity()));

        //set the adapter for the recycler view
        final FoodTruckListAdapter adapter = new FoodTruckListAdapter();
        adapter.setListener(new FoodTruckListener());

        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.foodtruck_recycler);
        recyclerView.setAdapter(adapter);

        linearLayout.findViewById(R.id.all_button).setOnClickListener(this);

        //connect the spinner selections to the sorting function in the FoodTruckData class
        ((Spinner) linearLayout.findViewById(R.id.sort_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos) {
                    case 0:
                        FoodTruckData.sort(FoodTruckData.SortMethod.Alphabetical, getContext(), null);
                        break;
                    case 1:
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            location.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location l) {
                                    FoodTruckData.sort(FoodTruckData.SortMethod.Closest, getContext(), l);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
                        }

                        break;
                    case 2:
                        FoodTruckData.sort(FoodTruckData.SortMethod.Rating, getContext(), null);
                        break;
                }
                //refersh the recyclerview
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return linearLayout;
    }

    //load the activity that shows all trucks on a single map
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.all_button) {
            Intent i = new Intent(getActivity(), AllMapActivity.class);
            getActivity().startActivity(i);
        }
    }

    //when an individual truck is selected, open the activity and pass the position of the truck to the new activity
    private class FoodTruckListener implements FoodTruckListAdapter.Listener {
        @Override
        public void onClick(int pos) {
            Intent i = new Intent(getActivity(), FoodTruckActivity.class);
            i.putExtra(FoodTruckActivity.NAME, pos);
            getActivity().startActivity(i);
        }
    }
}
