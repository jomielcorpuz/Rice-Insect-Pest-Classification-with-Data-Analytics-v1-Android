package com.example.riceinsectpest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class history extends AppCompatActivity implements RecyclerView_Interface {

    Toolbar toolbar;
    TextView Info;

    ArrayList<SuperClass> superClassArrayList = new ArrayList<>();
    private RecyclerView_Adapter objectRVAdapter;
    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        try {
            initialize();
            ReadDataforView();
        }catch (Exception e){
            e.printStackTrace();
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(history.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initialize(){
        //----- Initialize all Android Widgets ----

        toolbar = findViewById(R.id.ToolBar);
        Info = findViewById(R.id.Info);

        recyclerView = findViewById(R.id.Recycler_Viewer);
        superClassArrayList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
    }
    public void getData() {
        try
        {
            objectRVAdapter = new RecyclerView_Adapter(databaseHelper.getAllImageData(), this);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(objectRVAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ReadDataforView(){
        try {
            Cursor cursor = databaseHelper.ReadData();
            if (cursor.moveToFirst()){
                Info.setVisibility(View.INVISIBLE);
                getData();
            }else {
                Info.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, history_preview.class);
        intent.putExtra("ID", objectRVAdapter.objetModelClassArrayList.get(position).getID());
        intent.putExtra("Image Name", objectRVAdapter.objetModelClassArrayList.get(position).getImage_Name());
        intent.putExtra("Image", objectRVAdapter.objetModelClassArrayList.get(position).getImage());
        intent.putExtra("Image_Path", objectRVAdapter.objetModelClassArrayList.get(position).getImage_Path());
        intent.putExtra("Date", objectRVAdapter.objetModelClassArrayList.get(position).getDate_Taken());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(history.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}