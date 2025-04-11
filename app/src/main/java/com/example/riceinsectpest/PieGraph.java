package com.example.riceinsectpest;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PieGraph extends AppCompatActivity {

    ArrayList arrayList;
    PieChart pieChart;
    Spinner spinner;
    String area;
    Dialog dialog;
    TextView bph, mc, rb, rlf, sb, graphPlantName,graphPlantAdd;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;
    FirebaseDatabase db;
    DatabaseReference dbref,dbref2;
    int countD1,countD2,countD3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pie_graph);
        pieChart = findViewById(R.id.pieChart);
        bph = findViewById(R.id.countBPH);
        mc = findViewById(R.id.countMC);
        rb = findViewById(R.id.countRB);
        rlf = findViewById(R.id.countRLF);
        sb = findViewById(R.id.countSB);
        graphPlantName = findViewById(R.id.graphPlantName);
        graphPlantAdd = findViewById(R.id.graphPlantAddress);
        dialog = new Dialog(this);
        db           = FirebaseDatabase.getInstance();
        dbref        = db.getReference("PlantationAddress");
        dbref2        = db.getReference("DiseaseData");
        getData(1,1,1,1,1);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(35f);
        pieChart.setHoleColor(Color.WHITE);

        PieDataSet dataSet = new PieDataSet(arrayList, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        spinner = (Spinner) findViewById(R.id.spinnerAddress);
        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(PieGraph.this, android.R.layout.simple_spinner_dropdown_item,spinnerList);

        ProgressDialog progressDialog = new ProgressDialog(PieGraph.this);
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

                if(!isConnected(PieGraph.this)){
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

                    int countD1 = parseCount(d1count);
                    int countD2 = parseCount(d2count);
                    int countD3 = parseCount(d3count);
                    int countD4 = parseCount(d4count);
                    int countD5 = parseCount(d5count);
                    Log.d("TAG", "onDataChange: "+ countD1+"\n"+countD2+"\n"+countD3+"\n"+countD4+"\n"+countD5);
                    bph.setText(countD1+" Disease in "+area);
                    mc.setText(countD2+" Disease in "+area);
                    rb.setText(countD3+" Disease in "+area);
                    rlf.setText(countD4+" Disease in "+area);
                    sb.setText(countD5+" Disease in "+area);

                    getData(countD1,countD2,countD3,countD4,countD5);


                    PieDataSet dataSet = new PieDataSet(arrayList, "");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    PieData data = new PieData(dataSet);
                    data.setValueTextSize(10f);
                    data.setValueTextColor(Color.BLACK);
                    pieChart.clear();
                    pieChart.setData(data);


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
    private boolean isConnected(PieGraph pieGraph) {
        ConnectivityManager connectivityManager = (ConnectivityManager) pieGraph.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                Intent intent = new Intent(PieGraph.this, DataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.show();


    }


    private void getData(int d1, int d2, int d3, int d4, int d5){
        if(!isConnected(PieGraph.this)){
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PieGraph.this, DataActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}