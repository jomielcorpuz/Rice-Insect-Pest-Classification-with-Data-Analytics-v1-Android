package com.example.riceinsectpest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class HeatMap extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap googleMap;

     double langs[] = {145.708, 144.845, 144.192, 143.67,141.083, 125.352398, 125.6110};
     double lats[] = {-37.1886,-37.8361, -38.4034, -38.7597, -36.9672, 6.757509, 7.0736};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping);

          SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.heatMap);
           mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Move camera to a default location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.7749, -122.4194), 10));
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Now, you can add a heatmap layer
        addHeatmap();
    }
    private List<LatLng> generateHeatmapData() {
        // Replace this with your actual data points
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(37.7749, -122.4194));
        list.add(new LatLng(37.7751, -122.4189));
        list.add(new LatLng(37.7754, -122.4184));
        // ... add more points as needed

        return list;
    }

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
    private List<LatLng> readItems(double [] lats, double [] langs) {
        List<LatLng> result = new ArrayList<>();

        for (int i = 0; i < langs.length; i++) {

            double lat = lats[i];
            double lng = langs[i];
            result.add(new LatLng(lat, lng));
        }
        return result;
    }


}



