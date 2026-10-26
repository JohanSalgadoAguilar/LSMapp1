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

public class SenaAdapter extends RecyclerView.Adapter<SenaAdapter.SenaViewHolder> {

    private Context context;
    private List<Sena> listaSenas;

    public SenaAdapter(Context context, List<Sena> listaSenas) {
        this.context = context;
        this.listaSenas = listaSenas;
    }

    public SenaAdapter(List<Sena> listaSenas) {
        this.listaSenas = listaSenas;
    }

    @NonNull
    @Override
    public SenaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_sena, parent, false);
        return new SenaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SenaViewHolder holder, int position) {
        // Obtenemos el objeto correspondiente a esta posición exacta de la lista ordenada
        Sena sena = listaSenas.get(position);

        holder.tvNombreSena.setText(sena.getNombre());

        Context currentContext = holder.itemView.getContext();

        // Limpiar imagen anterior de Glide para evitar cruces al reciclar vistas
        Glide.with(currentContext).clear(holder.imgSena);

        // Cargar la imagen correspondiente usando Glide
        String imgUrl = sena.getImagenUrl();
        if (imgUrl != null && !imgUrl.trim().isEmpty()) {
            Glide.with(currentContext)
                    .load(imgUrl)
                    .into(holder.imgSena);
        }

        // Usamos la posición directa del bucle para asegurar que el click apunte al objeto correcto
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(currentContext, DetalleSenaActivity.class);
            intent.putExtra("NOMBRE_SENA", sena.getNombre());
            intent.putExtra("IMAGEN_SENA", sena.getImagenUrl());
            intent.putExtra("VIDEO_SENA", sena.getVideoUrl());
            intent.putExtra("POSICION_ACTUAL", position);
            intent.putExtra("CATEGORIA_ID", sena.getCategoriaId());
            currentContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaSenas != null ? listaSenas.size() : 0;
    }

    public static class SenaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSena;
        TextView tvNombreSena;

        public SenaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSena = itemView.findViewById(R.id.imgSena);
            tvNombreSena = itemView.findViewById(R.id.tvNombreSena);
        }
    }
}