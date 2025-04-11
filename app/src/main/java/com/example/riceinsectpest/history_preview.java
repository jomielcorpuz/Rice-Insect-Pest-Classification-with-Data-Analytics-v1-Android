package com.example.riceinsectpest;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class history_preview extends AppCompatActivity {

    TextView imageName, Date_Take;
    ImageView imageViewer;
    String ImagePATH;
    String ImAgE_NamE, ID;
    Toolbar toolbar, toolbar_BottomDialog;
    Button showBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_preview);

        initialize();
        DataHolder_From_History();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(history_preview.this, history.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        showBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog();

            }
        });
        imageViewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog();

            }
        });
    }
    private void initialize(){
        imageViewer = findViewById(R.id.imageViewer);
        imageName = findViewById(R.id.imageName);
        toolbar = findViewById(R.id.ToolBar);
        showBottom = findViewById(R.id.ShowBottom);
    }

    private void DataHolder_From_History(){

        // ----- Get Data from History after you click it -----
        String DateTakes = "No Data";
        Bitmap Image = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            ID = bundle.getString("ID");
            ImAgE_NamE = bundle.getString("Image Name");
            Image = bundle.getParcelable("Image");
            DateTakes = bundle.getString("Date");
            ImagePATH = bundle.getString("Image_Path");

        }

        imageName.setText(ImAgE_NamE);

        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        Bitmap cachedImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix, true);
        imageViewer.setImageBitmap(cachedImage);

        //Date_Take.setText(DateTakes);
    }

    public void showAnthracnoseDialog(){

        //----- Function for Showing of Results / Pop-up Dialog-----

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog_black_sigatoka);

        toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        //----- Function for closing the Dialog
        toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public void ShowDialog(){

        //----- Function for Showing of Results / Pop-up Dialog-----

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        String Disease_Name = imageName.getText().toString();

        if (Disease_Name.equals("Black Sigatoka")){
            dialog.setContentView(R.layout.bottomsheetdialog_black_sigatoka);

            toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            //----- Function for closing the Dialog
            toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

        }else if (Disease_Name.equals("Fusarium Wilt")){
            dialog.setContentView(R.layout.bottomsheetdialog_bunchy_top);

            toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            //----- Function for closing the Dialog
            toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
        }else if (Disease_Name.equals("BBT Bunchy Top")) {
            dialog.setContentView(R.layout.bottomsheetdialog_fusarium);

            toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            //------Function for closing the dialog

            toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

        }else if (Disease_Name.equals("Healthy Leaf")){
            Toast.makeText(this,"Leaf is Healthy.... Please keep searching", LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "No Leaf Detected....", LENGTH_SHORT).show();
        }
    }
}