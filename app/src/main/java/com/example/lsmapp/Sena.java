package com.example.lsmapp;

public class Sena {
    private String nombre;
    private String imagenUrl;
    private String videoUrl;
    private String categoriaId;

    // Constructor vacío obligatorio para que Firebase pueda hacer el mapeo automatico
    public Sena() {}

    public Sena(String nombre, String imagenUrl, String videoUrl, String categoriaId) {
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.videoUrl = videoUrl;
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre != null ? nombre : "";
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl != null ? imagenUrl : "";
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getVideoUrl() {
        return videoUrl != null ? videoUrl : "";
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategoriaId() {
        return categoriaId != null ? categoriaId : "";
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }
}