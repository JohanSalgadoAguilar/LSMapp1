package com.example.lsmapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogoActivity extends AppCompatActivity {

    private static final String TAG = "CatalogoActivity";
    private RecyclerView rvCatalogo;
    private SenaAdapter adapter;
    private List<Sena> listaSenas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        String categoriaId = getIntent().getStringExtra("CATEGORIA_ID");
        String categoriaTitulo = getIntent().getStringExtra("CATEGORIA_TITULO");

        if (categoriaTitulo != null) {
            setTitle(categoriaTitulo);
        }

        rvCatalogo = findViewById(R.id.rvCatalogo);
        rvCatalogo.setLayoutManager(new GridLayoutManager(this, 2));

        listaSenas = new ArrayList<>();
        adapter = new SenaAdapter(listaSenas);
        rvCatalogo.setAdapter(adapter);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar datos desde la base de datos
        cargarSenasDesdeFirestore(categoriaId);
    }

    private void cargarSenasDesdeFirestore(String categoriaId) {
        if (categoriaId == null) {
            Toast.makeText(this, "Error: El ID de la categoría es nulo", Toast.LENGTH_LONG).show();
            return;
        }

        // Convertir a minúsculas para asegurar coincidencia estricta con Firestore ("saludos")
        String categoriaLimpia = categoriaId.trim().toLowerCase();

        // Consulta limpia a Firestore sin pedir índice compuesto
        db.collection("senas")
                .whereEqualTo("categoriaId", categoriaLimpia)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listaSenas.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Sena sena = document.toObject(Sena.class);
                            listaSenas.add(sena);
                        }

                        // Ordenar la lista alfabéticamente (A-Z) en la memoria del dispositivo
                        Collections.sort(listaSenas, (s1, s2) -> {
                            if (s1.getNombre() == null || s2.getNombre() == null) return 0;
                            return s1.getNombre().compareToIgnoreCase(s2.getNombre());
                        });

                        if (listaSenas.isEmpty()) {
                            Toast.makeText(CatalogoActivity.this,
                                    "No se encontraron señas para: " + categoriaLimpia,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Exception e = task.getException();
                        String errorMsg = (e != null) ? e.getMessage() : "Error desconocido";
                        Log.e(TAG, "Error consultando Firestore: " + errorMsg);
                        Toast.makeText(CatalogoActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}