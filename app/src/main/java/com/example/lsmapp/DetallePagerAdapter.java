package com.example.lsmapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DetallePagerAdapter extends RecyclerView.Adapter<DetallePagerAdapter.SliderViewHolder> {

    private List<Sena> listaSenas;

    public DetallePagerAdapter(List<Sena> listaSenas) {
        this.listaSenas = listaSenas;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Sena sena = listaSenas.get(position);

        // 1. Cargar imagen de apoyo con Glide
        String imgUrl = sena.getImagenUrl();
        if (imgUrl != null && !imgUrl.trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imgUrl)
                    .into(holder.imgSlider);
            holder.imgSlider.setVisibility(View.VISIBLE);
        } else {
            holder.imgSlider.setImageDrawable(null);
        }

        // 2. Gestionar el video con el enlace raw de forma segura
        String videoUrl = sena.getVideoUrl();
        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            try {
                holder.videoView.setVisibility(View.VISIBLE);

                MediaController mediaController = new MediaController(holder.itemView.getContext());
                mediaController.setAnchorView(holder.videoView);
                holder.videoView.setMediaController(mediaController);

                holder.videoView.setVideoURI(Uri.parse(videoUrl));

                holder.videoView.setOnPreparedListener(mp -> {
                    // Ajustar proporción exacta para que no se vea gigante ni deformado
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();
                    if (videoWidth > 0 && videoHeight > 0) {
                        holder.videoView.setVideoSize(videoWidth, videoHeight);
                    }

                    mp.setLooping(true); // Repetir en bucle
                    holder.videoView.start();
                });

                holder.videoView.setOnErrorListener((mp, what, extra) -> {
                    // Si ocurre un error cargando el video, lo ocultamos limpiamente
                    holder.videoView.setVisibility(View.GONE);
                    return true;
                });

            } catch (Exception e) {
                e.printStackTrace();
                holder.videoView.setVisibility(View.GONE);
            }
        } else {
            // Si la seña no tiene video registrado, ocultamos el reproductor
            holder.videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewRecycled(@NonNull SliderViewHolder holder) {
        super.onViewRecycled(holder);
        // Detener reproducción al deslizar y reciclar la vista para evitar consumo en segundo plano
        if (holder.videoView != null) {
            holder.videoView.stopPlayback();
        }
    }

    @Override
    public int getItemCount() {
        return listaSenas != null ? listaSenas.size() : 0;
    }

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlider;
        CustomVideoView videoView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlider = itemView.findViewById(R.id.imgSlider);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}