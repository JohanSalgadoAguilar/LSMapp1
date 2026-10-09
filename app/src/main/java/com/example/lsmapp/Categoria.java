package com.example.lsmapp;

public class Categoria {
    private String id;
    private String nombre;
    private String descripcion;
    private int imagenResId;

    public Categoria(String id, String nombre, String descripcion, int imagenResId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenResId = imagenResId;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}