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

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private final Context context;
    private final List<Categoria> listaCategorias;

    public CategoriaAdapter(Context context, List<Categoria> listaCategorias) {
        this.context = context;
        this.listaCategorias = listaCategorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria categoria = listaCategorias.get(position);

        holder.tvNombre.setText(categoria.getNombre());
        holder.tvDescripcion.setText(categoria.getDescripcion());
        holder.imgPortada.setImageResource(categoria.getImagenResId());

        // Evento al hacer clic en una categoría
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CatalogoActivity.class);
            intent.putExtra("CATEGORIA_ID", categoria.getId());
            intent.putExtra("CATEGORIA_TITULO", categoria.getNombre());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion;
        ImageView imgPortada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionCategoria);
            imgPortada = itemView.findViewById(R.id.imgCategoria);
        }
    }
}