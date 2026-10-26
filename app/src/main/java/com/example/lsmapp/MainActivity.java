package com.example.lsmapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    // Evita abrir la pantalla dos veces si el usuario toca rápido
    private boolean navegando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbarMain);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Menú hamburguesa en la Toolbar
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

        // Opciones del menú lateral derecho
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.END);

            if (id == R.id.nav_abecedario) {
                abrirCatalogo(null, "abecedario", "Abecedario");
            } else if (id == R.id.nav_numeros) {
                abrirCatalogo(null, "numeros", "Números");
            } else if (id == R.id.nav_saludos) {
                abrirCatalogo(null, "saludos", "Saludos");
            } else if (id == R.id.nav_despedidas) {
                abrirCatalogo(null, "despedidas", "Despedidas");
            }
            // nav_inicio: ya estamos en inicio, solo se cierra el menú
            return true;
        });

        // Botones principales
        MaterialButton btnAbecedario = findViewById(R.id.btnAbecedario);
        MaterialButton btnNumeros = findViewById(R.id.btnNumeros);
        MaterialButton btnSaludos = findViewById(R.id.btnSaludos);
        MaterialButton btnDespedidas = findViewById(R.id.btnDespedidas);
        MaterialButton btnMinijuego = findViewById(R.id.btnMinijuego); // <--- NUEVO BOTÓN DE MINIJUEGO

        btnAbecedario.setOnClickListener(v -> abrirCatalogo(v, "abecedario", "Abecedario"));
        btnNumeros.setOnClickListener(v -> abrirCatalogo(v, "numeros", "Números"));
        btnSaludos.setOnClickListener(v -> abrirCatalogo(v, "saludos", "Saludos"));
        btnDespedidas.setOnClickListener(v -> abrirCatalogo(v, "despedidas", "Despedidas"));

        // <--- NUEVO EVENTO PARA ABRIR EL MINIJUEGO
        btnMinijuego.setOnClickListener(v -> {
            if (navegando) return;
            navegando = true;
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_press));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, MinijuegoActivity.class);
                startActivity(intent);
            }, 220);
        });

        // Animación de entrada: el logo aparece con rebote y los botones suben uno por uno
        animarEntrada(findViewById(R.id.imgLogoPrincipal),
                btnAbecedario, btnNumeros, btnSaludos, btnDespedidas, btnMinijuego);

        // Botón/gesto de atrás: primero cierra el menú lateral si está abierto
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void animarEntrada(View logo, View... botones) {
        // Logo: crece desde cero con rebote
        logo.setScaleX(0f);
        logo.setScaleY(0f);
        logo.setAlpha(0f);
        logo.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();

        // Botones: suben desde abajo con fundido, uno tras otro
        for (int i = 0; i < botones.length; i++) {
            View boton = botones[i];
            boton.setAlpha(0f);
            boton.setTranslationY(120f);
            boton.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(250L + i * 120L)
                    .setDuration(500)
                    .setInterpolator(new OvershootInterpolator(1.1f))
                    .start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navegando = false; // al regresar al menú, se puede volver a navegar
    }

    /**
     * @param origen botón tocado (si es null, no hay efecto de rebote y se abre de inmediato)
     */
    private void abrirCatalogo(View origen, String id, String titulo) {
        if (navegando) return;
        navegando = true;

        long retraso = 0;
        if (origen != null) {
            // 1) Efecto de rebote en el botón
            origen.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_press));
            retraso = 220;
        }

        // 2) Abrir el catálogo; el deslizamiento lo aplica el tema (ver estilos)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, CatalogoActivity.class);
            intent.putExtra("CATEGORIA_ID", id);
            intent.putExtra("CATEGORIA_TITULO", titulo);
            startActivity(intent);
        }, retraso);
    }
}