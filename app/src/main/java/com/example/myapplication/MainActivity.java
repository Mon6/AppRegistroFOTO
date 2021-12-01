package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;





public class MainActivity extends AppCompatActivity {

    //Views
    private FloatingActionButton addRecordBtn;

    //RecyclerView
    private RecyclerView recordsRv;

    //DB Helper
    private MyDbHelper dbHelper;

    //Action Bar
    ActionBar actionBar;

    //Ordenar Opciones
    String orderByNewest = Constants.C_ADDED_TIMESTAMP + " DESC";
    String orderByOldest = Constants.C_ADDED_TIMESTAMP + " ASC";
    String orderByTitleASC = Constants.C_NAME + " ASC";
    String orderByTitleDESC = Constants.C_NAME + " DESC";

    //Para actualizar Registros, actualiza con la ultima opcion de ordenacion elegida
    String currentOrderByStatus = orderByNewest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar Vista
        addRecordBtn = findViewById(R.id.addRecordBtn);
        recordsRv = findViewById(R.id.recordsRv);
        //Inicializamos db helper Clase
        dbHelper = new MyDbHelper(this);

        //Inicializacion ActionBar
        actionBar = getSupportActionBar();
        actionBar.setTitle("Registros");

        // Cargando Registros
        loadRecords(orderByNewest);

        // Click para Iniciar a añadir y grabar en la activity
        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Iniciar la Activity
                Intent intent = new Intent(MainActivity.this, AgregarRegistroActivity.class);
                intent.putExtra("isEditMode", false);//desea establecer nuevos datos, set false.
                startActivity(intent);
            }
        });
    }

    private void loadRecords(String orderBy){
        currentOrderByStatus = orderBy;
        AdapterRecord adapterRecord = new AdapterRecord(MainActivity.this,
                dbHelper.getAllRecords(orderBy));

        recordsRv.setAdapter(adapterRecord);

        //Establecer el numero de Registros
        actionBar.setSubtitle("Total: "+dbHelper.getRecordsCount());
    }

    private void searchRecords(String query){
        AdapterRecord adapterRecord = new AdapterRecord(MainActivity.this,
                dbHelper.searchRecords(query));

        recordsRv.setAdapter(adapterRecord);

    }

    @Override
    protected void onResume(){
        super.onResume();
        loadRecords(currentOrderByStatus);// Refresca o actualiza la lista de registros
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // buscar cuando se hace clic en el botón de búsqueda en el teclado
                searchRecords(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // busca mientras escribes
                searchRecords(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        //maneja elementos del menu
        int id = item.getItemId();
        if (id==R.id.action_sort){
            //Mostrar opciones de ordenacion (Mostrar Dialogo)
            sortOptionDialog();
        }else if (id==R.id.action_delete_all){
            //Elimina todos los datos

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Seguro que quieres eliminar todos los registros?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick (DialogInterface dialog,int which){
                            dbHelper.deleteAllData();
                            onResume();
                        }
                    })
                    .setNegativeButton ("Cancelar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                        }
                    });
            builder.show();

        }


        return super.onOptionsItemSelected(item);
    }

    private void sortOptionDialog() {
        //opciones para mostrar el dialogo
        String[] options = {"Titulo Ascendente", "Titulo Descendente", "El mas Nuevo", "Mas Antiguo"};

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Ordenado Por").setItems(options, new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    loadRecords(orderByTitleASC);
                }
                if (which == 1){
                    loadRecords(orderByTitleDESC);
                }
                if (which == 2){
                    loadRecords(orderByNewest);
                }
                if (which == 3){
                    loadRecords(orderByOldest);
                }
            }

        }).create().show();
    }


}
