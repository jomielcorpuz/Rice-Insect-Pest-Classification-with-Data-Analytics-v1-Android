package com.example.riceinsectpest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class BarchatAct extends AppCompatActivity {
    BarChart barChart;
    ArrayList arrayList;
    Spinner spinner;
    String area;
    Dialog dialog;
    TextView bph, mc, rb, rlf, sb, graphPlantName,graphPlantAdd, location;
    int countD1,countD2,countD3,countD4,countD5;

    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;
    FirebaseDatabase db;
    DatabaseReference dbref,dbref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        bph = findViewById(R.id.countBPH);
        mc = findViewById(R.id.countMC);
        rb = findViewById(R.id.countRB);
        rlf = findViewById(R.id.countRLF);
        sb = findViewById(R.id.countSB);
        location = findViewById(R.id.location);
        graphPlantName = findViewById(R.id.graphPlantName);
        graphPlantAdd = findViewById(R.id.graphPlantAddress);
        dialog = new Dialog(this);
        db           = FirebaseDatabase.getInstance();
        dbref        = db.getReference("PlantationAddress");
        dbref2        = db.getReference("DiseaseData");
        getData(1,1,1,1,1);


        barChart = (BarChart) findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false);

        setupBarChartData();
        spinner = (Spinner) findViewById(R.id.spinnerAddress);
        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(BarchatAct.this, android.R.layout.simple_spinner_dropdown_item,spinnerList);
        ProgressDialog progressDialog = new ProgressDialog(BarchatAct.this);
        progressDialog.startLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismissDialog();

            }
        },5000);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    spinnerList.add(dataSnapshot.child("plantationID").getValue().toString());

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!isConnected(BarchatAct.this)){
                    checkConn();
                }else {

                    area = spinner.getSelectedItem().toString();
                    saveDiseaseData();
                    arrayList.clear();
                    Query checkUser = dbref.orderByChild("plantationID").equalTo(area);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String plantName = dataSnapshot.child(area).child("plantationName").getValue(String.class);
                                String plantAdd= dataSnapshot.child(area).child("address").getValue(String.class);
                                graphPlantName.setText(plantName);
                                graphPlantAdd.setText(plantAdd);

                            } else{

                                graphPlantName.setText("NO DATA");
                                graphPlantAdd.setText("NO DATA");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveDiseaseData(){

        Query checkUser = dbref2.orderByChild("address").equalTo(area);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String d1count = snapshot.child(area).child("d1").getValue(String.class);
                    String d2count = snapshot.child(area).child("d2").getValue(String.class);
                    String d3count = snapshot.child(area).child("d3").getValue(String.class);
                    String d4count = snapshot.child(area).child("d4").getValue(String.class);
                    String d5count = snapshot.child(area).child("d5").getValue(String.class);

                     countD1 = parseCount(d1count);
                     countD2 = parseCount(d2count);
                     countD3 = parseCount(d3count);
                     countD4 = parseCount(d4count);
                     countD5 = parseCount(d5count);
                    Log.d("TAG", "onDataChange: "+ countD1+"\n"+countD2+"\n"+countD3+"\n"+countD4+"\n"+countD5);
                    bph.setText(countD1+" Diseases ");
                    mc.setText(countD2+" Diseases ");
                    rb.setText(countD3+" Diseases ");
                    rlf.setText(countD4+" Diseases ");
                    sb.setText(countD5+" Diseases ");
                    location.setText(area);
                    getData(countD1,countD2,countD3,countD4,countD5);

                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                    barEntries.add(new BarEntry(0f, countD1)); // Brown Plant Hopper
                    barEntries.add(new BarEntry(1f, countD2)); // Mole Cricket
                    barEntries.add(new BarEntry(2f, countD3)); // Rice Bug
                    barEntries.add(new BarEntry(3f, countD4)); // Rice Leaf Folders
                    barEntries.add(new BarEntry(4f, countD5)); // Stem Borer


                    BarDataSet barDataSet = new BarDataSet(barEntries, "Disease Count");


                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.TOP); // Set the position of the X-axis to the top
                    xAxis.setTextSize(0f); // Set text size to 0 to hide labels
                    xAxis.setTextColor(Color.TRANSPARENT); // Set text color to transparent to hide labels
                    xAxis.setDrawAxisLine(false); // Hide the axis line
                    xAxis.setDrawGridLines(false); // Hide the grid lines


                   // barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    int[] colors = new int[]{
                            getResources().getColor(R.color.label1),
                            getResources().getColor(R.color.label2),
                            getResources().getColor(R.color.label3),
                            getResources().getColor(R.color.label4),
                            getResources().getColor(R.color.label5)
                    };


                    barDataSet.setColors(colors);


                    // Create a BarData object and set the dataset
                    BarData barData = new BarData(barDataSet);

                    // Customize BarData as needed
                    barData.setBarWidth(0.9f);

                    // Set the data for the BarChart
                    barChart.setData(barData);

                    // Refresh the chart
                    barChart.invalidate();

                  /*  PieDataSet dataSet = new PieDataSet(arrayList, "");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData data = new PieData(dataSet);
                    data.setValueTextSize(10f);
                    data.setValueTextColor(Color.BLACK);
                    pieChart.clear();
                    pieChart.setData(data);*/


                    //Toast.makeText(getApplicationContext(),d1count,Toast.LENGTH_LONG).show();
                }
                else {


                    //Toast.makeText(getApplicationContext(),"Nothing",Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupBarChartData() {
        // Add bar entries (replace with your actual data)
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, countD1)); // Brown Plant Hopper
        barEntries.add(new BarEntry(1f, countD2)); // Mole Cricket
        barEntries.add(new BarEntry(2f, countD3)); // Rice Bug
        barEntries.add(new BarEntry(3f, countD4)); // Rice Leaf Folders
        barEntries.add(new BarEntry(4f, countD5)); // Stem Borer

        // Create a BarDataSet object
        BarDataSet barDataSet = new BarDataSet(barEntries, "Disease Count");

        // Set up colors for the bars (if needed)
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create a BarData object and set the dataset
        BarData barData = new BarData(barDataSet);

        // Customize BarData as needed
        barData.setBarWidth(0.9f);

        // Set the data for the BarChart
        barChart.setData(barData);

        // Refresh the chart
        barChart.invalidate();
    }


    private void getData(int d1, int d2, int d3, int d4, int d5){
        if(!isConnected(BarchatAct.this)){
            checkConn();
        }else
        {
            // arrayList.clear();
            arrayList= new ArrayList<>();
            arrayList.add(new PieEntry(d1, "Brown Plant Hopper"));
            arrayList.add(new PieEntry(d2, "Mole Cricket"));
            arrayList.add(new PieEntry(d3, "Rice Bug"));
            arrayList.add(new PieEntry(d4, "Rice Leaf Folders"));
            arrayList.add(new PieEntry(d5, "Stem Borer"));
        }

    }
    private int parseCount(String count) {
        if (count != null && !count.isEmpty()) {
            try {
                return Integer.parseInt(count);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        // Return a default value or handle the case where the count is not a valid integer
        return 0;
    }
    private boolean isConnected(BarchatAct barchatAct) {
        ConnectivityManager connectivityManager = (ConnectivityManager) barchatAct.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        }
        else{
            return false;
        }
    }
    private void checkConn(){
        dialog.setContentView(R.layout.internet_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(BarchatAct.this, DataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.show();


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BarchatAct.this, DataActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

