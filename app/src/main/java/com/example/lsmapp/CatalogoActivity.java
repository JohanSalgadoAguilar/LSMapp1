package com.example.lsmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CatalogoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView rvCatalogo;
    private SenaAdapter adapter;
    private List<Sena> listaSenas;
    private FirebaseFirestore db;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_catalogo);

        drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        rvCatalogo = findViewById(R.id.rvCatalogo);
        NavigationView navigationView = findViewById(R.id.navigationView);

        db = FirebaseFirestore.getInstance();
        listaSenas = new ArrayList<>();

        // Configurar RecyclerView y Adaptador de inmediato
        if (rvCatalogo != null) {
            rvCatalogo.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new SenaAdapter(this, listaSenas);
            rvCatalogo.setAdapter(adapter);
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
            toolbar.inflateMenu(R.menu.toolbar_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_menu) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }
                    return true;
                }
                return false;
            });
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                Intent intent = new Intent(CatalogoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_abecedario) {
                cargarCategoria("abecedario", "Abecedario");
            } else if (id == R.id.nav_numeros) {
                cargarCategoria("numeros", "Números");
            } else if (id == R.id.nav_saludos) {
                cargarCategoria("saludos", "Saludos");
            } else if (id == R.id.nav_despedidas) {
                cargarCategoria("despedidas", "Despedidas");
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // Recibir extras del Intent e iniciar carga
        String categoriaId = getIntent().getStringExtra("CATEGORIA_ID");
        String categoriaTitulo = getIntent().getStringExtra("CATEGORIA_TITULO");

        if (categoriaId != null) {
            cargarCategoria(categoriaId, categoriaTitulo != null ? categoriaTitulo : "Catálogo");
        }
    }

    private void cargarCategoria(String id, String titulo) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(titulo);
        }

        // Consulta a Firebase Firestore
        db.collection("senas")
                .whereEqualTo("categoriaId", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaSenas.clear(); // Limpiamos la lista actual
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Sena> resultList = queryDocumentSnapshots.toObjects(Sena.class);

                        // ORDENAMIENTO CONDICIONAL
                        if ("numeros".equals(id)) {
                            // Extrae únicamente los dígitos numéricos ignorando palabras (ej. "numero 20" -> 20)
                            resultList.sort((s1, s2) -> {
                                try {
                                    String limpio1 = s1.getNombre().replaceAll("[^0-9]", "").trim();
                                    String limpio2 = s2.getNombre().replaceAll("[^0-9]", "").trim();

                                    int num1 = limpio1.isEmpty() ? 0 : Integer.parseInt(limpio1);
                                    int num2 = limpio2.isEmpty() ? 0 : Integer.parseInt(limpio2);

                                    return Integer.compare(num1, num2);
                                } catch (Exception e) {
                                    return s1.getNombre().compareToIgnoreCase(s2.getNombre());
                                }
                            });
                        } else {
                            // Orden alfabético intacto para Abecedario y demás categorías
                            resultList.sort((s1, s2) -> s1.getNombre().compareToIgnoreCase(s2.getNombre()));
                        }

                        listaSenas.addAll(resultList);
                    }
                    // Notificamos siempre al adaptador
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(CatalogoActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}