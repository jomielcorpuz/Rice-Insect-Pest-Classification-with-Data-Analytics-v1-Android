package com.example.riceinsectpest;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PinLocation extends FragmentActivity implements OnMapReadyCallback {

    Vibrator vibrator;
    TextView nameDisease;
    Spinner spinner;
    FirebaseDatabase db;
    DatabaseReference dbref;
    String location,mainadd,pinData="no data",diseaseType;

    GoogleMap googleMap;
    SupportMapFragment mapFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_location);


        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference("SelectedLocation");
        nameDisease = findViewById(R.id.nameDisease);


        spinner = findViewById(R.id.spinnerDisease);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.diseaseArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProgressDialog progressDialog = new ProgressDialog(PinLocation.this);
                progressDialog.startLoading();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismissDialog();

                    }
                },5000);

                diseaseType = parent.getItemAtPosition(position).toString();

                pinLocation(diseaseType);

                if(diseaseType.equals("")){
                    Toast.makeText(getApplicationContext(),"Insert Pest",Toast.LENGTH_LONG).show();
                }else{
                    googleMap.clear();
                    pinLocation(diseaseType);
                }
                nameDisease.setText(diseaseType+" pest affected places");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mainadd = "Davao City";


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);


    }
    //NEW HEATMAP USING LATLANG AS LOCATION
    private void pinLocation(String type) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CapturedData");
        Query checkUser = reference.orderByChild("disease").equalTo(type);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String latLangString = dataSnapshot.child("latlang").getValue(String.class);
                        if (latLangString != null && !latLangString.isEmpty()) {
                            String[] latLangArray = latLangString.split(", ");
                            double latitude = Double.parseDouble(latLangArray[0]);
                            double longitude = Double.parseDouble(latLangArray[1]);

                            LatLng latLng = new LatLng(latitude, longitude);

                            // Call onMapReady with retrieved latLng
                            onMapReady(googleMap, latLng);
                        }
                    }
                } else {
                    // Handle the case where disease data is not found
                    Toast.makeText(getApplicationContext(), "Disease not found on this area.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(getApplicationContext(), "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
    }

    // Overloaded onMapReady method to include LatLng parameter
    public void onMapReady(GoogleMap map, LatLng latLng) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        // Create a heatmap layer with a single LatLng
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(Collections.singletonList(latLng))
                .build();

        // Add the heatmap layer to the map
        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }



    //OLD HEATMAP NOT USING LATLANG as location
    /*private void pinLocation(String type){


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CapturedData");
            Query checkUser = reference.orderByChild("disease").equalTo(type);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            pinData = dataSnapshot.child("latlang").getValue(String.class);
                            onMapReady(googleMap);
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(), "Disease not found on this area.", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();
                }
            });

    }

    @Override
    public void onMapReady(GoogleMap map) {
            googleMap = map;
            if (pinData.equals("no data")) {
                location = mainadd;
                Toast.makeText(this,"No data",Toast.LENGTH_SHORT).show();
            } else {
                location = pinData;
            }
            List<Address> addressList = null;

            Geocoder geocoder = new Geocoder(PinLocation.this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
           // Toast.makeText(this,"Location: "+ latLng,Toast.LENGTH_SHORT).show();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //map.addMarker(new MarkerOptions().position(latLng).title(location));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));

           // Create a heatmap layer
           HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(Collections.singleton(latLng))
                .build();

        // Add the heatmap layer to the map
        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));




    }*/

/*
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Move camera to a default location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7749, -122.4194), 10));

        // Now, you can add a heatmap layer
        addHeatmap();
    }*/
/*
    private List<LatLng> generateHeatmapData() {

        Geocoder geocoder = new Geocoder(PinLocation.this);

        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Replace this with your actual data points
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(37.7749, -122.4194));
        list.add(new LatLng(37.7751, -122.4189));
        list.add(new LatLng(37.7754, -122.4184));
        // ... add more points as needed

        return list;
    }*/
/*

    private void addHeatmap() {
        // Create a list of LatLng points for your heatmap data
        List<LatLng> list = generateHeatmapData();

        // Create a heatmap layer
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        // Add the heatmap layer to the map
        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // ----- Responsible for back pressing your back button -----

        Intent intent = new Intent(PinLocation.this, DataActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }







}