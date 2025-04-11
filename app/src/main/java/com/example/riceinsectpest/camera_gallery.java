package com.example.riceinsectpest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class camera_gallery extends AppCompatActivity {

    AppCompatButton Camera_Button,Gallery_Button;
    TextView Camera, Gallery,currentLocation, currentLong, currentLat;
    Toolbar toolbar;
    public static int counter = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    String latitude,longitude,addressName,cityName;
    private static final int REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);

        initialize();

        Camera_Button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                // ----- Check Permission -----
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(camera_gallery.this, on_activity_result_cam_gal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 101);
                    counter = 1;

                }else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
                }
            }
        });


        Gallery_Button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // ----- Check Permission -----
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(camera_gallery.this, on_activity_result_cam_gal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 100);
                    counter = 2;

                }else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(camera_gallery.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 101) {
                    Intent intent = new Intent(camera_gallery.this, on_activity_result_cam_gal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 101);
                    counter = 1;

                }else if (requestCode == 100) {

                    Intent intent = new Intent(camera_gallery.this, on_activity_result_cam_gal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 100);
                    counter = 2;
                }
            }else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

            if(requestCode == REQUEST_CODE){
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                }else{
                    Toast.makeText(camera_gallery.this, "Required Permission", Toast.LENGTH_SHORT).show();
                }

            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private void initialize(){
        //----- Initialize all Android Widgets ----

        Camera_Button = findViewById(R.id.Camera);
        Gallery_Button = findViewById(R.id.GalleryBtn);
        toolbar = findViewById(R.id.ToolBar);
        Camera = findViewById(R.id.Icon_Name_Cam);
        currentLocation = findViewById(R.id.currentLocation);
        currentLat = findViewById(R.id.currentLat);
        currentLong  = findViewById(R.id.currentLong);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location !=null){
                                Geocoder geocoder = new Geocoder(camera_gallery.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    latitude = String.valueOf(addresses.get(0).getLatitude());
                                    longitude = String.valueOf(addresses.get(0).getLongitude());
                                    addressName = addresses.get(0).getAddressLine(0);
                                    cityName = addresses.get(0).getLocality();

                                    currentLocation.setText(addressName+", "+cityName);
                                    currentLat.setText(latitude);
                                    currentLong.setText(longitude);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    });
        }else{
            askPermission();
        }
    }

    private void askPermission(){
        ActivityCompat.requestPermissions(camera_gallery.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(camera_gallery.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}