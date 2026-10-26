package com.example.lsmapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DetalleSenaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPagerDetalle;
    private TextView tvTituloToolbar, tvTituloDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_detalle_sena);

        drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbarDetalle);
        tvTituloToolbar = findViewById(R.id.tvTituloToolbar);
        tvTituloDetalle = findViewById(R.id.tvTituloDetalle);
        viewPagerDetalle = findViewById(R.id.viewPagerDetalle);
        NavigationView navigationView = findViewById(R.id.navigationView);

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

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                Intent intent = new Intent(DetalleSenaActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_abecedario) {
                abrirCatalogo("abecedario", "Abecedario");
            } else if (id == R.id.nav_numeros) {
                abrirCatalogo("numeros", "Números");
            } else if (id == R.id.nav_saludos) {
                abrirCatalogo("saludos", "Saludos");
            } else if (id == R.id.nav_despedidas) {
                abrirCatalogo("despedidas", "Despedidas");
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // Recibir datos iniciales del Intent
        String nombreSena = getIntent().getStringExtra("NOMBRE_SENA");
        int posicionInicial = getIntent().getIntExtra("POSICION_ACTUAL", 0);
        String categoriaId = getIntent().getStringExtra("CATEGORIA_ID");

        if (nombreSena != null) {
            tvTituloToolbar.setText(nombreSena);
            tvTituloDetalle.setText(nombreSena);
        }

        // Cargar y ordenar la galería para que coincida exactamente con el catálogo
        cargarGaleriaDesdeNube(categoriaId, posicionInicial);
    }

    private void cargarGaleriaDesdeNube(String categoriaId, int posicionInicial) {
        if (categoriaId == null || categoriaId.trim().isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("senas")
                .whereEqualTo("categoriaId", categoriaId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Sena> listaCategoria = queryDocumentSnapshots.toObjects(Sena.class);

                    if (!listaCategoria.isEmpty()) {
                        // Ordenamiento inteligente basado en números extraídos del texto (ej. "Numero 6" -> 6)
                        listaCategoria.sort((s1, s2) -> {
                            int num1 = extraerNumero(s1.getNombre());
                            int num2 = extraerNumero(s2.getNombre());

                            // Si ambos tienen números válidos, los ordenamos numéricamente (1, 2, ..., 9, 20, 50, 100)
                            if (num1 != -1 && num2 != -1) {
                                return Integer.compare(num1, num2);
                            }
                            // Si no tienen formato numérico, recurrimos al orden alfabético normal
                            return s1.getNombre().compareToIgnoreCase(s2.getNombre());
                        });

                        DetallePagerAdapter pagerAdapter = new DetallePagerAdapter(listaCategoria);
                        viewPagerDetalle.setAdapter(pagerAdapter);

                        // Posicionar exactamente en el elemento seleccionado asegurando el rango válido
                        if (posicionInicial >= 0 && posicionInicial < listaCategoria.size()) {
                            viewPagerDetalle.setCurrentItem(posicionInicial, false);
                        }

                        // Actualizar textos dinámicamente al deslizar
                        viewPagerDetalle.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                Sena senaActual = listaCategoria.get(position);
                                tvTituloToolbar.setText(senaActual.getNombre());
                                tvTituloDetalle.setText(senaActual.getNombre());
                            }
                        });
                    }
                });
    }

    // Método auxiliar para extraer números de nombres como "Numero 6", "Número 20", etc.
    private int extraerNumero(String texto) {
        if (texto == null) return -1;
        try {
            String soloDigitos = texto.replaceAll("\\D+", "");
            if (!soloDigitos.isEmpty()) {
                return Integer.parseInt(soloDigitos);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void abrirCatalogo(String id, String titulo) {
        Intent intent = new Intent(DetalleSenaActivity.this, CatalogoActivity.class);
        intent.putExtra("CATEGORIA_ID", id);
        intent.putExtra("CATEGORIA_TITULO", titulo);
        startActivity(intent);
        finish();
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