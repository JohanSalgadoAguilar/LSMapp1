package com.example.lsmapp;

public class Sena {
    private String categoriaId;
    private String nombre;
    private String imagenUrl;

    // Constructor vacío obligatorio para Firebase Firestore
    public Sena() {}

    public Sena(String categoriaId, String nombre, String imagenUrl) {
        this.categoriaId = categoriaId;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}