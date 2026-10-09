package com.example.lsmapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SenaAdapter extends RecyclerView.Adapter<SenaAdapter.ViewHolder> {

    private final List<Sena> listaSenas;

    public SenaAdapter(List<Sena> listaSenas) {
        this.listaSenas = listaSenas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sena, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sena sena = listaSenas.get(position);

        // Si el nombre no es nulo ni está vacío, lo muestra; si no, pone un texto por defecto
        if (sena.getNombre() != null && !sena.getNombre().trim().isEmpty()) {
            holder.tvNombre.setText(sena.getNombre());
        } else {
            holder.tvNombre.setText("Sin nombre");
        }

        // Cargar imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(sena.getImagenUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgSena);

        // Evento al tocar cualquier seña para verla en pantalla completa
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetalleSenaActivity.class);
            intent.putExtra("NOMBRE_SENA", sena.getNombre());
            intent.putExtra("URL_SENA", sena.getImagenUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaSenas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView imgSena;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreSena);
            imgSena = itemView.findViewById(R.id.imgSena);
        }
    }
}