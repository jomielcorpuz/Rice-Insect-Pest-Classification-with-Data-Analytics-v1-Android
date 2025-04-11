package com.example.riceinsectpest;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.MyViewHolder>{

    private final com.example.riceinsectpest.RecyclerView_Interface recyclerViewInterface;
    ArrayList<SuperClass> objetModelClassArrayList;

    private static String ImagePATH;
    private static String ImAgE_NamE;
    private static String IMAGE_ID;
    private static int Imagepos;

    public RecyclerView_Adapter (ArrayList<SuperClass> objetModelClassArrayList, com.example.riceinsectpest.RecyclerView_Interface recyclerViewInterface){
        this.objetModelClassArrayList = objetModelClassArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView imageName, Date_Taken, Image_ID_Holder;
        ImageView objectImageView;
        Button Detail_Button;

        public MyViewHolder(@NonNull View itemView, com.example.riceinsectpest.RecyclerView_Interface recyclerView_interface) {
            super(itemView);

        try{
                imageName = itemView.findViewById(R.id.Image_Name);
                Date_Taken = itemView.findViewById(R.id.Date_Taken);
                objectImageView = itemView.findViewById(R.id.Image_Holder);
                Detail_Button = itemView.findViewById(R.id.Detail_Button);
                Image_ID_Holder = itemView.findViewById(R.id.Image_ID);

                Detail_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Imagepos = getAdapterPosition();
                        IMAGE_ID = Image_ID_Holder.getText().toString();

                        PopupMenu popupMenu = new PopupMenu(Detail_Button.getContext(), Detail_Button);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.Rename) {
                                    // ----- Rename in History Activity -----
                                    // ... your existing code for Rename
                                    ImAgE_NamE = imageName.getText().toString();
                                    AutoCompleteTextView Image_NameHolder;
                                    Button Cancel1, Rename;

                                    Dialog Rename_builder = new Dialog(Detail_Button.getContext());
                                    Rename_builder.setContentView(R.layout.rename_dialogbox);
                                    Rename_builder.setCancelable(true);

                                    Cancel1 = Rename_builder.findViewById(R.id.cancel_BUTTON);
                                    Rename = Rename_builder.findViewById(R.id.Rename_Button);
                                    Image_NameHolder = Rename_builder.findViewById(R.id.Image_NameHolder);

                                    Image_NameHolder.setText(ImAgE_NamE);

                                    Rename.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DatabaseHelper DB = new DatabaseHelper(Rename_builder.getContext());
                                            if (!Image_NameHolder.getText().toString().equals("")){
                                                DB.UpdateImageName(IMAGE_ID, Image_NameHolder.getText().toString());
                                                Toast.makeText(Rename_builder.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent (view.getContext(), history.class);
                                                view.getContext().startActivity(intent);
                                            }else {
                                                Toast.makeText(Rename_builder.getContext(), "Don't leave blank", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Cancel1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Rename_builder.cancel();
                                        }
                                    });
                                    Rename_builder.show();
                                } else if (item.getItemId() == R.id.Delete) {
                                    // ----- Delete in History Activity -----
                                    // ... your existing code for Delete
                                    Dialog Delete_builder = new Dialog(Detail_Button.getContext());
                                    Delete_builder.setContentView(R.layout.delete_dialogbox);

                                    Delete_builder.setCancelable(true);
                                    Button Cancel2, Delete;

                                    Cancel2 = Delete_builder.findViewById(R.id.cancel_BUTTON);
                                    Delete = Delete_builder.findViewById(R.id.Delete_button);

                                    Delete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try{
                                                DatabaseHelper DB = new DatabaseHelper(Delete_builder.getContext());
                                                DB.DeleteData(IMAGE_ID);
                                                Delete_builder.dismiss();
                                                Intent intent = new Intent (view.getContext(), history.class);
                                                view.getContext().startActivity(intent);

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    Cancel2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Delete_builder.cancel();
                                        }
                                    });
                                    Delete_builder.show();
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (recyclerView_interface != null)
                        {
                            Imagepos = getAdapterPosition();

                            if (Imagepos != RecyclerView.NO_POSITION)
                            {
                                recyclerView_interface.onItemClick(Imagepos);

                            }
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_row, viewGroup, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public int getItemCount() { return objetModelClassArrayList.size(); }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SuperClass objetModelClass = objetModelClassArrayList.get(position);
        holder.Image_ID_Holder.setText(objetModelClass.getID());
        holder.imageName.setText(objetModelClass.getImage_Name());
        ImagePATH = objetModelClass.getImage_Path();
        holder.Date_Taken.setText(objetModelClass.getDate_Taken());
        holder.objectImageView.setImageBitmap(objetModelClass.getImage());

    }
}
