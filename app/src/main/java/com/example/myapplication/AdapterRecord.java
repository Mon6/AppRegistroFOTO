package com.example.myapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.HolderRecord>{

    //Variables
    private Context context;
    private ArrayList<ModelRecord> recordsList;

    // DB Helper
    MyDbHelper dbHelper;

    //Constructor
    public AdapterRecord(Context context, ArrayList<ModelRecord> recordsList){
        this.context = context;
        this.recordsList = recordsList;
        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public HolderRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_record, parent, false);

        return new HolderRecord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecord holder,final int position) {

        // obtener datos, establecer datos, ver clics en el método

        //Obtener datos
        ModelRecord model = recordsList.get(position);
        final String id = model.getId();
        final String name = model.getName();
        final String image = model.getImage();
        final String bio = model.getBio();
        final String phone = model.getPhone();
        final String email = model.getEmail();
        final String dob = model.getDob();
        final String addedTime = model.getAddedTime();
        final String updatedTime = model.getUpdatedTime();

        //Establecer Datos
        holder.nameTv.setText(name);
        holder.phoneTv.setText(phone);
        holder.emailTv.setText(email);
        holder.dobTv.setText(dob);

        // si el usuario no adjunta la imagen, imageUri será nulo, por lo tanto,
        // configure una imagen predeterminada en ese caso
        if (image.equals("null")){
            // no hay imagen en el registro, establecer predeterminado
            holder.profileIv.setImageResource(R.drawable.ic_person_black);
        }
        else {
            // tener imagen en el registro
            holder.profileIv.setImageURI(Uri.parse(image));
        }


        // manejar clicks de elementos (ir a la actividad de registro de detalles)

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pass record id to next activity to show details of thet record
                Intent intent = new Intent(context, DetalleRegistroActivity.class);
                intent.putExtra("RECORD_ID", id);
                context.startActivity(intent);
            }
        });

        //manejar clicks de botones (mostrar opciones como editar, eliminar)

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mostrar el menu de opciones
                showMoreDialog(
                        ""+position,
                        ""+id,
                        ""+name,
                        ""+phone,
                        ""+email,
                        ""+dob,
                        ""+bio,
                        ""+image,
                        ""+addedTime,
                        ""+updatedTime);

            }
        });
    }

    public void showMoreDialog( String position, final String id, final String name, final String phone, final String email,
                                final String dob, final String bio, final String image, final String addedTime, final String updatedTime){
        //opciones para mostrar en el dialogo
        String [] options = {"Editar", "Eliminar"};
        //Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Agregar elementos al dialogo
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //se hace click en editar
                    //inicie la actividad para actualizar los registros existentes
                    Intent intent = new Intent(context, AgregarRegistroActivity.class);
                    intent.putExtra("ID", id);
                    intent.putExtra("NAME", name);
                    intent.putExtra("PHONE", phone);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("DOB", dob);
                    intent.putExtra("BIO", bio);
                    intent.putExtra("IMAGE", image);
                    intent.putExtra("ADDEDTIME", addedTime);
                    intent.putExtra("UPDATEDTIME", updatedTime);
                    intent.putExtra("isEditMode", true);//nesecita para establecer datos existente
                    context.startActivity(intent);
                }
                else if (which == 1){
                    //Hace click en la opcion eliminar
                    dbHelper.deleteData(id);
                    //Actualizar registro llamando en actividades en el metodo de reanudar
                    ((MainActivity)context).onResume();
                }

            }

        });
        //Mostrar el dialogo
        builder.create().show();
    }


    @Override
    public int getItemCount() {
        return recordsList.size();// devuelve el tamaño de la lista / número o registros
    }

    class HolderRecord extends RecyclerView.ViewHolder{
        //vistas
        ImageView profileIv;
        TextView nameTv, phoneTv, emailTv, dobTv;
        ImageButton moreBtn;
        public HolderRecord(@NonNull View itemView){
            super(itemView);

            //Inicializamos la vistas
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            dobTv = itemView.findViewById(R.id.dobTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);

        }
    }
}
