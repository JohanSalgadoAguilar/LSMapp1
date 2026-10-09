package com.example.lsmapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetalleSenaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_sena);

        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        ImageView imgDetalle = findViewById(R.id.imgDetalle);

        // Recibir los datos enviados desde el Adapter
        String nombre = getIntent().getStringExtra("NOMBRE_SENA");
        String imagenUrl = getIntent().getStringExtra("URL_SENA");

        if (nombre != null) {
            tvTitulo.setText(nombre);
            setTitle(nombre);
        }

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(this)
                    .load(imagenUrl)
                    .into(imgDetalle);
        }
    }
}