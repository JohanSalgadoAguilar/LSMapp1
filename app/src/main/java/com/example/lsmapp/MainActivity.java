package com.example.lsmapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvCategorias = findViewById(R.id.rvCategorias);

        // Configuración de la cuadrícula de 2 columnas
        rvCategorias.setLayoutManager(new GridLayoutManager(this, 2));

        // Lista con las imágenes reales de portada
        List<Categoria> lista = new ArrayList<>();
        lista.add(new Categoria("abecedario", "Abecedario", "Aprende el alfabeto en señas", R.drawable.portada_abecedario));
        lista.add(new Categoria("saludos", "Saludos", "Saluda con confianza", R.drawable.portada_saludos));
        lista.add(new Categoria("despedidas", "Despedidas", "Despídete con respeto y calidez", R.drawable.portada_despedidas));
        lista.add(new Categoria("numeros", "Números", "Cuenta y comunícate en señas", R.drawable.portada_numeros));

        // Pasamos el contexto (this) y la lista de categorías
        CategoriaAdapter adapter = new CategoriaAdapter(this, lista);
        rvCategorias.setAdapter(adapter);
    }
}