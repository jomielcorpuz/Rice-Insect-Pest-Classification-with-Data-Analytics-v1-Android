package com.example.riceinsectpest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Rice_Pest_DB";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "ImagesPest_TBL";

    // below variable is for our id column.
    private static final String ID_COL = "Image_ID";

    // below variable is for our image name column
    private static final String IMAGENAME_COL = "Image_Name";

    // below variable id for our image path column.
    private static final String IMAGEPATH_COL = "Image_Path";

    // below variable for our images column.
    private static final String IMAGES_COL = "Images";

    // below variable is for our date taken column.
    private static final String DATETAKEN_COL = "Date_Taken";

    private byte[] imageInByte;
    private ByteArrayOutputStream objectByteArrayOutputStream;
    public static Bitmap objectBitmap;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            String query = "CREATE TABLE " + TABLE_NAME +
                    " (" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + IMAGENAME_COL + " TEXT,"
                    + IMAGEPATH_COL + " TEXT,"
                    + IMAGES_COL + " BLOB,"
                    + DATETAKEN_COL + " TEXT)";

            // at last we are calling a exec sql
            // method to execute above sql query
            DB.execSQL(query);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void StoreImage (SuperClass superClass) {

        SQLiteDatabase DB = this.getWritableDatabase();
        Bitmap imageToStoreBitmap = superClass.getImage();

        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG, 100, objectByteArrayOutputStream);

        imageInByte = objectByteArrayOutputStream.toByteArray();
        ContentValues values = new ContentValues();

        values.put(IMAGENAME_COL, superClass.getImage_Name());
        values.put(IMAGEPATH_COL, superClass.getImage_Path());
        values.put(IMAGES_COL, imageInByte);
        values.put(DATETAKEN_COL, superClass.getDate_Taken());

        // after adding all values we are passing
        // content values to our table.
        DB.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        DB.close();
    }

    public ArrayList<SuperClass> getAllImageData() {
        try
        {
            SQLiteDatabase objectSqLiteDatabase = this.getReadableDatabase();
            ArrayList<SuperClass> objectModelClassList = new ArrayList<>();

            Cursor objectCursor = objectSqLiteDatabase.rawQuery("Select * from "+ TABLE_NAME, null);
            if (objectCursor.getCount() !=0 )
            {
                while (objectCursor.moveToNext())
                {
                    String ID = objectCursor.getString(0);
                    String nameofImage = objectCursor.getString(1);
                    String image_Path = objectCursor.getString(2);
                    String date_Taken = objectCursor.getString(4);
                    byte[] imageByte = objectCursor.getBlob(3);


                    objectBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
                    objectModelClassList.add(new SuperClass(ID, nameofImage, objectBitmap, image_Path, date_Taken));

                }
                objectSqLiteDatabase.close();
                return objectModelClassList;
            }else
            {
                return null;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor ReadData(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from "+ TABLE_NAME, null);
        return cursor;
    }

    public void DeleteData(String Image_ID) {
        SQLiteDatabase DB = this.getWritableDatabase();

        DB.delete(TABLE_NAME, "Image_ID=?", new String[]{Image_ID});
        DB.close();
    }

    public void UpdateImageName(String ID, String ImageName){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGENAME_COL, ImageName);
        DB.update(TABLE_NAME, values, "Image_ID = ?", new String[]{ID} );

    }
    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        try{

            DB.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(DB);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
