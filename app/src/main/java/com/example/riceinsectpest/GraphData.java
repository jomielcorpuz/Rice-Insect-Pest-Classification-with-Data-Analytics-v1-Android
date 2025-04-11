package com.example.riceinsectpest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GraphData extends AppCompatActivity {

    ArrayList arrayList;
    BarChart barChart;
    Spinner spinner;
    String area;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;
    FirebaseDatabase db;
    DatabaseReference dbref,dbref2;
    int countD1,countD2,countD3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_data);
        barChart = findViewById(R.id.barChart);
        db           = FirebaseDatabase.getInstance();
        dbref        = db.getReference("SelectedLocation");
        dbref2        = db.getReference("PestData");
        getData(0,0,0);

        BarDataSet barDataSet = new BarDataSet(arrayList,"sample");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        spinner = (Spinner) findViewById(R.id.spinnerAddress);
        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(GraphData.this, android.R.layout.simple_spinner_dropdown_item,spinnerList);

        ProgressDialog progressDialog = new ProgressDialog(GraphData.this);
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
                    spinnerList.add(dataSnapshot.child("location").getValue().toString());

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

                area = spinner.getSelectedItem().toString();
                saveDiseaseData();
                arrayList.clear();

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

                    countD1 = Integer.parseInt(d1count);
                    countD2 = Integer.parseInt(d2count);
                    countD3 = Integer.parseInt(d3count);

                    getData(countD1,countD2,countD3);

                    BarDataSet barDataSet = new BarDataSet(arrayList,"Types of Rice Pest");
                    BarData barData = new BarData(barDataSet);
                    barChart.clear();
                    barChart.setData(barData);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);
                    barDataSet.setValueTextSize(16f);


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




    private void getData(int d1, int d2, int d3){
        arrayList= new ArrayList<>();
        arrayList.add(new BarEntry(2f,d1));
        arrayList.add(new BarEntry(3f,d2));
        arrayList.add(new BarEntry(4f,d3));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GraphData.this, DataActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}