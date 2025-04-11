package com.example.riceinsectpest;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SetLocation extends FragmentActivity implements OnMapReadyCallback {

    Vibrator vibrator;
    Button pin, save;
    EditText editloc;
    TextView mapLat, mapLong;
    FirebaseDatabase db;
    DatabaseReference dbref, dbref2, dbrefPro, dbrefMun, dbrefBrgy, dbrefPurok, dbrefPlantation;
    String username, location, mainadd, fname, phone, pass, email, type, pickup, add;
    Dialog dialog;
    GoogleMap map;
    SupportMapFragment mapFragment;
    Spinner spinnerPro, spinnerMun, spinnerBrgy, spinnerPurok;
    ArrayList<String> spinnerListPro, spinnerListMun, spinnerListBrgy, spinnerListPurok;
    ArrayAdapter<String> adapterPro, adapterMun, adapterBrgy, adapterPurok;
    String spinnerProvinceData, spinnerMunData, spinnerBarData, spinnerPurokData, fullAddress, plantationName, longAddress, latAddress, plantationID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_location);

        spinnerPro = findViewById(R.id.spinnerProvinceLocation);
        spinnerMun = findViewById(R.id.spinnerMunicipalityLocation);
        spinnerBrgy = findViewById(R.id.spinnerBrgyLocation);
        spinnerPurok = findViewById(R.id.spinnerPurokLocation);

        mapLat = findViewById(R.id.mapLat);
        mapLong = findViewById(R.id.mapLong);


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference("SelectedLocation");
        dbref2 = db.getReference("DiseaseData");

        dbrefPro = db.getReference("ProvinceAddress");
        dbrefMun = db.getReference("MunicipalityAddress");
        dbrefBrgy = db.getReference("BarangayAddress");
        dbrefPurok = db.getReference("PurokAddress");
        dbrefPlantation = db.getReference("PlantationAddress");
        pin = findViewById(R.id.btnGen);
        save = findViewById(R.id.btnsave);
        editloc = findViewById(R.id.txtSetPlantation);
        dialog = new Dialog(this);

        spinnerListPro = new ArrayList<>();
        spinnerListMun = new ArrayList<>();
        spinnerListBrgy = new ArrayList<>();
        spinnerListPurok = new ArrayList<>();
        adapterPro = new ArrayAdapter<String>(SetLocation.this, android.R.layout.simple_spinner_dropdown_item, spinnerListPro);
        adapterMun = new ArrayAdapter<String>(SetLocation.this, android.R.layout.simple_spinner_dropdown_item, spinnerListMun);
        adapterBrgy = new ArrayAdapter<String>(SetLocation.this, android.R.layout.simple_spinner_dropdown_item, spinnerListBrgy);
        adapterPurok = new ArrayAdapter<String>(SetLocation.this, android.R.layout.simple_spinner_dropdown_item, spinnerListPurok);

        save.setVisibility(View.INVISIBLE);

        getProvinceData();

        mainadd = "Bansalan, Davao del Sur";

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateMap();


            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editloc.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input a Plantation Name", Toast.LENGTH_LONG).show();
                } else {
                    plantationName = editloc.getText().toString();
                    plantationID = plantationName + ", " + fullAddress;
                    Query checkUser = dbrefPlantation.orderByChild("plantationID").equalTo(plantationID);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(getApplicationContext(), "Plantation already existed", LENGTH_SHORT).show();

                            } else {

                                SavePlantation savePlantation = new SavePlantation(plantationName, fullAddress, longAddress, latAddress, plantationID);
                                dbrefPlantation.child(plantationID).setValue(savePlantation);
                                String d1 = "0", d2 = "0", d3 = "0", d4 = "0", d5 = "0";
                                DataHelper dataHelper = new DataHelper(d1, d2, d3, d4, d5, plantationID);
                                dbref2.child(plantationID).setValue(dataHelper);

                                Toast.makeText(getApplicationContext(), "Plantation Added", LENGTH_SHORT).show();
                                editloc.setText("");
                                save.setVisibility(View.INVISIBLE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);


    }

    private boolean isConnected(SetLocation setLocation) {
        ConnectivityManager connectivityManager = (ConnectivityManager) setLocation.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else {
            return false;
        }
    }


    private void checkConn() {
        dialog.setContentView(R.layout.internet_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(SetLocation.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.show();


    }


    private void getProvinceData() {
        try {
            dbrefPro.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        spinnerListPro.add(dataSnapshot.child("province").getValue().toString());
                    }
                    adapterPro.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            spinnerPro.setAdapter(adapterPro);
            spinnerPro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    adapterMun.clear();
                    adapterBrgy.clear();
                    adapterPurok.clear();
                    spinnerProvinceData = spinnerPro.getSelectedItem().toString();

                    getMunData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {

        }

    }

    private void getMunData() {
        try {

            Query checkData = dbrefMun.orderByChild("province").equalTo(spinnerProvinceData);

            checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            spinnerListMun.add(dataSnapshot.child("municipality").getValue().toString());
                        }
                        adapterMun.notifyDataSetChanged();
                        spinnerMun.setAdapter(adapterMun);
                        spinnerMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                spinnerMunData = spinnerMun.getSelectedItem().toString();
                                adapterBrgy.clear();
                                adapterPurok.clear();
                                getBarangayData();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    } else {
                        adapterMun.clear();
                        adapterBrgy.clear();
                        adapterPurok.clear();
                        spinnerMunData = "";
                        spinnerBarData = "";
                        spinnerPurokData = "";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {

        }

    }

    private void getBarangayData() {
        try {

            Query checkData = dbrefBrgy.orderByChild("province").equalTo(spinnerProvinceData);

            checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Query checkData = dbrefBrgy.orderByChild("municipality").equalTo(spinnerMunData);

                        checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        spinnerListBrgy.add(dataSnapshot.child("brgy").getValue().toString());
                                    }
                                    adapterBrgy.notifyDataSetChanged();
                                    spinnerBrgy.setAdapter(adapterBrgy);
                                    spinnerBrgy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            spinnerBarData = spinnerBrgy.getSelectedItem().toString();
                                            adapterPurok.clear();
                                            getPurokData();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                } else {
                                    adapterBrgy.clear();
                                    spinnerBarData = "";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        adapterBrgy.clear();
                        spinnerBarData = "";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {

        }

    }

    private void getPurokData() {
        try {

            Query checkData = dbrefPurok.orderByChild("province").equalTo(spinnerProvinceData);

            checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Query checkData = dbrefPurok.orderByChild("municipality").equalTo(spinnerMunData);

                        checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Query checkData = dbrefPurok.orderByChild("brgy").equalTo(spinnerBarData);

                                    checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    spinnerListPurok.add(dataSnapshot.child("purok").getValue().toString());
                                                }
                                                adapterPurok.notifyDataSetChanged();
                                                spinnerPurok.setAdapter(adapterPurok);
                                                spinnerPurok.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        spinnerPurokData = spinnerPurok.getSelectedItem().toString();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {
                                                    }
                                                });
                                            } else {
                                                adapterPurok.clear();
                                                spinnerPurokData = "";
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    adapterPurok.clear();
                                    spinnerPurokData = "";
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        adapterPurok.clear();
                        spinnerPurokData = "";
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "fdafdkhfldsf", Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {

        }

    }

    private void generateMap() {
        if (spinnerMunData.equals("")) {
            Toast.makeText(getApplicationContext(), "Please choose a Municipality", Toast.LENGTH_LONG).show();
        } else if (spinnerBarData.equals("")) {
            Toast.makeText(getApplicationContext(), "Please choose a Barangay", Toast.LENGTH_LONG).show();
        } else if (spinnerPurokData.equals("")) {
            Toast.makeText(getApplicationContext(), "Please choose a Purok", Toast.LENGTH_LONG).show();
        } else if (editloc.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please input a Plantation Name", Toast.LENGTH_LONG).show();
        } else {

            fullAddress = spinnerPurokData + " " + spinnerBarData + ", " + spinnerMunData + ", " + spinnerProvinceData;

            mainadd = fullAddress;
            try {
                onMapReady(map);
                save.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Address not found", LENGTH_SHORT).show();
            }


        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (!isConnected(SetLocation.this)) {
            checkConn();
        }
        else{

                map = googleMap;
                location = mainadd;
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(SetLocation.this);
                try{
                    addressList = geocoder.getFromLocationName(location,1);
                }catch (IOException e){
                    e.printStackTrace();
                }
                Address address = addressList.get(0);

                longAddress = String.valueOf(address.getLongitude());
                latAddress = String.valueOf(address.getLatitude());
                mapLong.setText(longAddress);
                mapLat.setText(latAddress);

                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.addMarker(new MarkerOptions().position(latLng).title(location));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // ----- Responsible for back pressing your back button -----

        Intent intent = new Intent(SetLocation.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }







}