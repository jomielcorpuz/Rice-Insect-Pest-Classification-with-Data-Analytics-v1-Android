package com.example.riceinsectpest;

import static android.Manifest.permission.CAMERA;
import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.riceinsectpest.ml.InsectmodelIncept;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class on_activity_result_cam_gal extends AppCompatActivity {

    ImageView imageView;
    Button showBottom,saveData;
    Toolbar toolbar, toolbar_BottomDialog;
    TextView imageName, confidence, txtPlantationName, txtPlantationAdd, plantationView,plantView,addView;
    Bitmap imageToStore;
    Spinner spinnerPlantation;
    String timeStamp,dateCapture,timeNow, image_name, Image_path, date_Taken, ID, Image_Name,filename,imgTreatment,area="no data";
    String d1 = "Brown Plant Hopper",d2 = "Mole Cricket", d3 = "Rice Bug",d4 ="Rice Leaf Folders",d5 ="Stem Borer",d6 ="Undefined",d1count,d2count,d3count,d4count,d5count,spinnerPlantationData;
    FirebaseDatabase db;
    DatabaseReference dbref,dbref3,dbrefPlant;
    DatabaseHelper databaseHelper;
    int imageSize = 224;
    int Counter;
    private String imageFILENAME = "IMG_";
    private String currentPHOTOPATH;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Dialog dialog;

    ArrayList<String> spinnerListPlantation;
    ArrayAdapter<String> adapterPlantation;

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 102;
    String addressName,cityName;
    String latlang;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_activity_result_cam_gal);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initialize();


        try {
            if (camera_gallery.counter == 1)
            {
                CameraActivity();
            }else {
                GalleryActivity();


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(on_activity_result_cam_gal.this, camera_gallery.class);
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
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadricePest();

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ShowDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        imageName.setVisibility(View.VISIBLE);
        showBottom.setVisibility(View.VISIBLE);
        saveData.setVisibility(View.VISIBLE);
        confidence.setVisibility(View.VISIBLE);

        txtPlantationName.setVisibility(View.VISIBLE);
        txtPlantationAdd.setVisibility(View.VISIBLE);
        plantationView.setVisibility(View.VISIBLE);
        plantView.setVisibility(View.VISIBLE);
        addView.setVisibility(View.VISIBLE);
        spinnerPlantation.setVisibility(View.VISIBLE);


        try {
            if (resultCode == RESULT_OK){
                if (camera_gallery.counter == 2 && requestCode == 100)
                {
                    if (data != null){
                        imageUri = data.getData();
                        try {
                            imageToStore = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                            Matrix matrix = new Matrix();
                            matrix.postRotate(270);
                            Bitmap cachedImage = Bitmap.createBitmap(imageToStore, 0, 0, imageToStore.getWidth(), imageToStore.getHeight(), matrix, true);
                            imageView.setImageBitmap(cachedImage);
                            imageToStore = Bitmap.createScaledBitmap(imageToStore, imageSize, imageSize, false);

                            //---------------------------Image Classifier CNN ---------------------------
                            classifyImage(imageToStore);

                            storeImage(imageToStore);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    if (camera_gallery.counter == 1 && requestCode == 101){
                        try{
                            imageUri = Uri.fromFile(new File(currentPHOTOPATH));
                            onImageResult(data);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                Intent intent = new Intent(on_activity_result_cam_gal.this, camera_gallery.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initialize(){
        //----- Initialize all Android Widgets ----

        imageView = findViewById(R.id.imageViewer);
        toolbar = findViewById(R.id.ToolBar);
        imageName = findViewById(R.id.imageName);
        showBottom = findViewById(R.id.ShowBottom);
        confidence = findViewById(R.id.Confidence);
        txtPlantationName = findViewById(R.id.txtPlantationName);
        txtPlantationAdd = findViewById(R.id.txtPlantationAdd);
        plantationView = findViewById(R.id.plantationView);
        plantView =  findViewById(R.id.plantView);
        addView = findViewById(R.id.addView);
        databaseHelper = new DatabaseHelper(this);
        saveData = findViewById(R.id.SaveData);
        dialog       = new Dialog(this);
        db           = FirebaseDatabase.getInstance();
        dbref        = db.getReference("CapturedData");

        dbref3        = db.getReference("DiseaseData");
        dbrefPlant = db.getReference("PlantationAddress");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        spinnerPlantation = (Spinner) findViewById(R.id.spinnerPlantation);
        spinnerListPlantation = new ArrayList<>();
        adapterPlantation = new ArrayAdapter<String>(on_activity_result_cam_gal.this, android.R.layout.simple_spinner_dropdown_item,spinnerListPlantation);


        getPlantData();



    }

    private void getPlantData(){
        try{
            dbrefPlant.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        spinnerListPlantation.add(dataSnapshot.child("plantationID").getValue().toString());
                    }
                    adapterPlantation.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            spinnerPlantation.setAdapter(adapterPlantation);
            spinnerPlantation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    spinnerPlantationData = spinnerPlantation.getSelectedItem().toString();
                    area = spinnerPlantationData;

                    Query checkUser = dbrefPlant.orderByChild("plantationID").equalTo(spinnerPlantationData);

                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String plantName = dataSnapshot.child(spinnerPlantationData).child("plantationName").getValue(String.class);
                                String plantAdd= dataSnapshot.child(spinnerPlantationData).child("address").getValue(String.class);
                                txtPlantationName.setText(plantName);
                                txtPlantationAdd.setText(plantAdd);

                            } else{

                                txtPlantationName.setText("NO DATA");
                                txtPlantationAdd.setText("NO DATA");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }catch (Exception e) {

        }

    }



    private void saveDiseaseData(){

        Query checkUser = dbref3.orderByChild("address").equalTo(area);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    d1count = snapshot.child(area).child("d1").getValue(String.class);
                    d2count = snapshot.child(area).child("d2").getValue(String.class);
                    d3count = snapshot.child(area).child("d3").getValue(String.class);
                    d4count = snapshot.child(area).child("d4").getValue(String.class);
                    d5count = snapshot.child(area).child("d5").getValue(String.class);

                    int countD1 = parseCount(d1count);
                    int countD2 = parseCount(d2count);
                    int countD3 = parseCount(d3count);
                    int countD4 = parseCount(d4count);
                    int countD5 = parseCount(d5count);

                    if(image_name.equals(d1)){
                        int a = countD1 + 1;
                        String Val1 = String.valueOf(a);
                        DataHelper dataHelper = new DataHelper(Val1,d2count,d3count,d4count,d5count,area);
                        dbref3.child(area).setValue(dataHelper);
                    }   else if(image_name.equals(d2)){
                        int a = countD2 + 1;
                        String Val2 = String.valueOf(a);
                        DataHelper dataHelper = new DataHelper(d1count,Val2,d3count,d4count,d5count,area);
                        dbref3.child(area).setValue(dataHelper);
                    }  else if(image_name.equals(d3)){
                        int a = countD3 + 1;
                        String Val3 = String.valueOf(a);
                        DataHelper dataHelper = new DataHelper(d1count,d2count,Val3,d4count,d5count,area);
                        dbref3.child(area).setValue(dataHelper);
                    }  else if(image_name.equals(d4)){
                        int a = countD4 + 1;
                        String Val4 = String.valueOf(a);
                        DataHelper dataHelper = new DataHelper(d1count,d2count,d3count,Val4,d5count,area);
                        dbref3.child(area).setValue(dataHelper);
                    }
                    else {
                        int a = countD5 + 1;
                        String Val5 = String.valueOf(a);
                        DataHelper dataHelper = new DataHelper(d1count,d2count,d3count,d4count,Val5,area);
                        dbref3.child(area).setValue(dataHelper);
                    }

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
    private boolean isConnected(on_activity_result_cam_gal activityThis) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activityThis.getSystemService(Context.CONNECTIVITY_SERVICE);
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

            }
        });
        dialog.show();


    }

    private void getLastLocation(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location !=null){
                                Geocoder geocoder = new Geocoder(on_activity_result_cam_gal.this, Locale.getDefault());
                                List<Address> addresses;
                                try {

                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                    latitude = addresses.get(0).getLatitude();
                                    longitude = addresses.get(0).getLongitude();
                                    latlang = latitude + ", " + longitude;

                                    Log.d("currentLoc", "Location: " + latlang);
                                    //addressName = addresses.get(0).getAddressLine(0);
                                    //cityName = addresses.get(0).getLocality();

                                   // currentLocation.setText(addressName+", "+cityName);
                                   // currentLat.setText(latitude);
                                   // currentLong.setText(longitude);

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
        ActivityCompat.requestPermissions(on_activity_result_cam_gal.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

    }



    private void uploadricePest() {
        getLastLocation();
        if(!isConnected(on_activity_result_cam_gal.this)){
            checkConn();
        }else{

            if(image_name.equals("Undefined")){
                Toast.makeText(getApplicationContext(), "Invalid Image", Toast.LENGTH_LONG).show();
            }
            else
            {
                if(area.equals("no data")){
                    Toast.makeText(getApplicationContext(), "Please select location", Toast.LENGTH_LONG).show();
                }else{


                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setTitle("Saving Data.....");
                    pd.show();


                    String newPath = filename + timeStamp;
                    StorageReference riversRef = storageReference.child("images/" + filename + "/" + newPath);
                    riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String Profile_ID = uri.toString();

                                    String saveID = timeStamp+timeNow+area;
                                    Toast.makeText(getApplicationContext(), "Image saved Successfully!", Toast.LENGTH_LONG).show();
                                    SaveDataHelper saveDataHelper = new SaveDataHelper(Profile_ID, image_name, imgTreatment, area, timeStamp, saveID,latlang);
                                    dbref.child(saveID).setValue(saveDataHelper);
                                    saveDiseaseData();


                                    pd.dismiss();
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pd.setMessage("Percentage: " + (int) progressPercent + "%");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploading Failed", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }

        }

    }

    private void CameraActivity(){

        // ----- Access Device Camera -----
        try {
            camera_gallery.counter = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                        File imageFILE = File.createTempFile(imageFILENAME, ".jpg", storageDirectory);
                        currentPHOTOPATH = imageFILE.getAbsolutePath();

                        Uri imageURI = FileProvider.getUriForFile(on_activity_result_cam_gal.this,
                                "com.example.riceinsectpest.fileprovider", imageFILE);

                        Log.d("IMAGE", "File path: " + currentPHOTOPATH);
                        Log.d("IMAGE", "File URI: " + imageURI);

                        if (imageURI != null) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                            startActivityForResult(intent, 101);
                        } else {
                            Log.e("ERROR", "imageURI is null");
                        }
                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                        Log.d("IMAGE","Value"+imageURI);
                        startActivityForResult(intent, 101);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception c) {
            c.printStackTrace();
        }
    }

    private void GalleryActivity(){

        // ----- Get Image from Gallery -----
        try{
            camera_gallery.counter = 2;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static int getCameraPhotoOrientation(ImageView context, String imagePath) {

        // ----- Set Image Orientation -----
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private void onImageResult(Intent data){

        // ----- Function for Camera while after pressing the camera button -----
        try {
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 2;
            Bitmap cachedImage = BitmapFactory.decodeFile(currentPHOTOPATH, o2);
            int rotate = getCameraPhotoOrientation(imageView, currentPHOTOPATH);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            cachedImage = Bitmap.createBitmap(cachedImage , 0, 0, cachedImage.getWidth(), cachedImage.getHeight(), matrix, true);
            imageView.setImageBitmap(cachedImage);
            imageToStore = Bitmap.createScaledBitmap(cachedImage, imageSize, imageSize, false);

            //---------------------------Image Classifier CNN ---------------------------
            classifyImage(imageToStore);
            storeImage(imageToStore);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void storeImage (Bitmap bitmap){

        // ----- Function for Storing the images in your Database -----
        try {
            //------------------------ FOR Image PAth ------------------------
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
            dateCapture = dateFormat1.format(new Date());
            String imageFileName = "IMG_" + dateCapture + ".jpg";
            filename = String.format(imageFileName, dateCapture);

            //------------------------ FOR Date Taken ------------------------
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm-ss");
            timeStamp = dateFormat.format(new Date());
            timeNow = timeFormat.format(new Date());
            image_name = imageName.getText().toString();
            Image_path = filename;
            date_Taken =  timeStamp;

            databaseHelper.StoreImage(new SuperClass(ID, image_name, bitmap , Image_path, date_Taken));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        // ----- Responsible for back pressing your back button -----
        Intent intent = new Intent(on_activity_result_cam_gal.this, camera_gallery.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
            dialog.getWindow().setGravity(Gravity.TOP);

            //----- Function for closing the Dialog
            toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

        }else if (Disease_Name.equals("Fusarium Wilt")) {
            dialog.setContentView(R.layout.bottomsheetdialog_fusarium);

            toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.TOP);

            //----- Function for closing the Dialog
            toolbar_BottomDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
        }else if (Disease_Name.equals("BBT Bunchy Top")) {
            dialog.setContentView(R.layout.bottomsheetdialog_bunchy_top);

            toolbar_BottomDialog = dialog.findViewById(R.id.ToolBar);

            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.TOP);

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

    @SuppressLint("SetTextI18n")
    public void classifyImage(Bitmap image) {

        try {
            InsectmodelIncept model = InsectmodelIncept.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            int pixel = 0;
            for (int i = 0; i < imageSize; ++i) {
                for (int j = 0; j < imageSize; ++j) {
                    final int val = intValues[pixel++];


                    int IMAGE_MEAN = 0;
                    float IMAGE_STD = 255.0f;
                    byteBuffer.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    byteBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    byteBuffer.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);

                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            InsectmodelIncept.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; ++i) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Brown Plant Hopper","Mole Cricket", "Rice Bug", "Rice Leaf Folders",  "Stem Borer", "Undefined"};

            imageName.setText(classes[maxPos]);
            Image_Name = imageName.getText().toString();

            if (Image_Name.equals("Brown Plant Hopper")) {
                Counter = 1;
            } else if (Image_Name.equals("Mole Cricket")) {
                Counter = 2;
            }

            String CONFIDENCE_LEVEL = String.format("%.2f", maxConfidence * 100);
            confidence.setText(CONFIDENCE_LEVEL+"%");


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }//---------------------------------------
    }
}