package com.example.riceinsectpest;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageLocation extends AppCompatActivity {

    Spinner spinnerPro,spinnerMun,spinnerBrgy;
    Button btnPro,btnMun,btnBrgy,btnSaveLocation;
    Dialog dialog;
    EditText txtPurok;
    FirebaseDatabase db;
    DatabaseReference dbref,dbref2,dbref3,dbref4;
    ArrayList<String> spinnerListAddMun,spinnerProvince, spinnerMunicipality, spinnerListBarPro,spinnerBarMunicipality, spinnerBarangay ;
    ArrayAdapter<String> adapterAddMun, adapterProvince, adapterMunicipality, adapterBarPro, adapterBarMunicipality, adapterBarangay;
    String provinceSpinnerMun,barSpinnerPro,barSpinnerMun;
    String spinnerProvinceData, spinnerMunData, spinnerBarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_location);

        initialize();

        getProvinceData();

        btnPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProvince();
            }
        });
        btnMun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMunicipality();
            }
        });
        btnBrgy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBarangay();
            }
        });
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePurok();
            }
        });


    }

    private void initialize() {
        spinnerPro = findViewById(R.id.spinnerPro);
        spinnerMun = findViewById(R.id.spinnerMun);
        spinnerBrgy = findViewById(R.id.spinnerBrgy);
        btnPro = findViewById(R.id.btnPro);
        btnMun = findViewById(R.id.btnMun);
        btnBrgy = findViewById(R.id.btnBrgy);
        btnSaveLocation = findViewById(R.id.btnSaveLocation);
        txtPurok = findViewById(R.id.txtPurok);
        dialog       = new Dialog(this);
        db = FirebaseDatabase.getInstance();
        dbref = db.getReference("ProvinceAddress");
        dbref2 = db.getReference("MunicipalityAddress");
        dbref3 = db.getReference("BarangayAddress");
        dbref4 = db.getReference("PurokAddress");
        spinnerListAddMun = new ArrayList<>();
        spinnerProvince = new ArrayList<>();
        spinnerMunicipality = new ArrayList<>();
        spinnerBarangay = new ArrayList<>();
        spinnerListBarPro = new ArrayList<>();
        spinnerBarMunicipality = new ArrayList<>();
        adapterAddMun = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerListAddMun);
        adapterProvince = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerProvince);
        adapterMunicipality = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerMunicipality);
        adapterBarPro = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerListBarPro);
        adapterBarMunicipality = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerBarMunicipality);
        adapterBarangay = new ArrayAdapter<String>(ManageLocation.this, android.R.layout.simple_spinner_dropdown_item,spinnerBarangay);
    }

    private void savePurok(){
        if(spinnerMunData.equals("")){
            Toast.makeText(getApplicationContext(), "Please choose a Municipality", Toast.LENGTH_LONG).show();
        }else if(spinnerBarData.equals("")){
            Toast.makeText(getApplicationContext(), "Please choose a Barangay", Toast.LENGTH_LONG).show();
        }else if(txtPurok.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Please input a Purok", Toast.LENGTH_LONG).show();
        }else{
            String purok = txtPurok.getText().toString();
            String purokID = purok+" "+spinnerBarData+", "+spinnerMunData+", "+spinnerProvinceData;
            SavePurok savePurok = new SavePurok(spinnerProvinceData,spinnerMunData,spinnerBarData,purok,purokID);
            dbref4.child(purokID).setValue(savePurok);
            Toast.makeText(getApplicationContext(),"New province added.",Toast.LENGTH_LONG).show();
            txtPurok.setText("");

            Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_LONG).show();
        }
    }

    private void getProvinceData(){
        try{
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        spinnerProvince.add(dataSnapshot.child("province").getValue().toString());
                    }
                    adapterProvince.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            spinnerPro.setAdapter(adapterProvince);
            spinnerPro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    adapterMunicipality.clear();
                    adapterBarangay.clear();
                    spinnerProvinceData = spinnerPro.getSelectedItem().toString();

                    getMunData();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }catch (Exception e) {

        }

    }

    private void getMunData(){
        try{

            Query checkData= dbref2.orderByChild("province").equalTo(spinnerProvinceData);

            checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            spinnerMunicipality.add(dataSnapshot.child("municipality").getValue().toString());
                        }
                        adapterMunicipality.notifyDataSetChanged();
                        spinnerMun.setAdapter(adapterMunicipality);
                        spinnerMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                spinnerMunData = spinnerMun.getSelectedItem().toString();
                                adapterBarangay.clear();
                                getBarangayData();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    } else {
                        adapterMunicipality.clear();
                        adapterBarangay.clear();
                        spinnerMunData = "";
                        spinnerBarData = "";
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });


        }catch (Exception e) {

        }

    }

    private void getBarangayData(){
        try{

            Query checkData= dbref3.orderByChild("province").equalTo(spinnerProvinceData);

            checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Query checkData= dbref3.orderByChild("municipality").equalTo(spinnerMunData);

                        checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        spinnerBarangay.add(dataSnapshot.child("brgy").getValue().toString());
                                    }
                                    adapterBarangay.notifyDataSetChanged();
                                    spinnerBrgy.setAdapter(adapterBarangay);
                                    spinnerBrgy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            spinnerBarData = spinnerBrgy.getSelectedItem().toString();
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                } else {
                                    adapterBarangay.clear();
                                    spinnerBarData = "";
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        adapterBarangay.clear();
                        spinnerBarData = "";
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                }
            });


        }catch (Exception e) {

        }

    }

    private void openProvince(){

        Log.d("ManageLocation","Open province.");
        dialog.setContentView(R.layout.add_province);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnSave = dialog.findViewById(R.id.btnSavePro);
        Button btnCancel = dialog.findViewById(R.id.btnCanPro);
        EditText editTextPro = dialog.findViewById(R.id.txtInsertPro);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextPro.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Input New Province",Toast.LENGTH_LONG).show();
                }else{

                    String province = editTextPro.getText().toString();
                    SaveProvince saveProvince = new SaveProvince(province);
                    dbref.child(province).setValue(saveProvince);
                    Toast.makeText(getApplicationContext(),"New province added."+ saveProvince,Toast.LENGTH_LONG).show();
                    Log.d("ManageLocation","New province added."+ saveProvince);
                    editTextPro.setText("");
                    spinnerProvince.clear();

                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openMunicipality(){
        dialog.setContentView(R.layout.add_municipality);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnSave = dialog.findViewById(R.id.btnSaveMun);
        Button btnCancel = dialog.findViewById(R.id.btnCanMun);
        EditText editTextMun = dialog.findViewById(R.id.txtInsertMun);
        Spinner spinnerMun = dialog.findViewById(R.id.spinnerMunPro);

        adapterAddMun.clear();

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    spinnerListAddMun.add(dataSnapshot.child("province").getValue().toString());
                }
                adapterAddMun.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        spinnerMun.setAdapter(adapterAddMun);
        spinnerMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinceSpinnerMun = spinnerMun.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextMun.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Input New Municipality",Toast.LENGTH_LONG).show();
                }else{

                    String mun = editTextMun.getText().toString();
                    String munID = mun+", "+provinceSpinnerMun;
                    SaveMunicipality saveMunicipality = new SaveMunicipality(provinceSpinnerMun,mun,munID);
                    dbref2.child(munID).setValue(saveMunicipality);
                    Toast.makeText(getApplicationContext(),"New Municipality added.",Toast.LENGTH_LONG).show();
                    editTextMun.setText("");

                }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openBarangay(){
        dialog.setContentView(R.layout.add_barangay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnSave = dialog.findViewById(R.id.btnSaveBar);
        Button btnCancel = dialog.findViewById(R.id.btnCanBar);
        EditText editTextBar = dialog.findViewById(R.id.txtInsertBar);
        Spinner spinnerBarPro = dialog.findViewById(R.id.spinnerBarPro);
        Spinner spinnerBarMun = dialog.findViewById(R.id.spinnerBarMun);

        adapterBarPro.clear();
        adapterBarMunicipality.clear();

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    spinnerListBarPro.add(dataSnapshot.child("province").getValue().toString());
                }
                adapterBarPro.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        spinnerBarPro.setAdapter(adapterBarPro);
        spinnerBarPro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapterBarMunicipality.clear();
                barSpinnerPro = spinnerBarPro.getSelectedItem().toString();

                Query checkData= dbref2.orderByChild("province").equalTo(barSpinnerPro);

                checkData.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                spinnerBarMunicipality.add(dataSnapshot.child("municipality").getValue().toString());
                            }
                            adapterBarMunicipality.notifyDataSetChanged();
                            spinnerBarMun.setAdapter(adapterBarMunicipality);
                            spinnerBarMun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    barSpinnerMun = spinnerBarMun.getSelectedItem().toString();
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else {
                            adapterBarMunicipality.clear();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextBar.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Please Input New Barangay",Toast.LENGTH_LONG).show();
                }else{

                    String bar = editTextBar.getText().toString();
                    String barID = bar+", "+barSpinnerMun+", "+barSpinnerPro;
                    SaveBarangay saveBarangay = new SaveBarangay(barSpinnerPro,barSpinnerMun,bar,barID);
                    dbref3.child(barID).setValue(saveBarangay);
                    Toast.makeText(getApplicationContext(),"New Barangay added.",Toast.LENGTH_LONG).show();
                    editTextBar.setText("");
                    spinnerBarangay.clear();
                    getBarangayData();

                }


            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ManageLocation.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}