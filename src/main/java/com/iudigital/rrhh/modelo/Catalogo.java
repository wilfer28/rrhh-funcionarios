package com.iudigital.rrhh.modelo;

// Sirve para los combos: tipo de documento y estado civil
public class Catalogo {
    private int id;
    private String nombre;
    public Catalogo(int id, String nombre) { this.id = id; this.nombre = nombre; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    @Override public String toString() { return nombre; } // lo que muestra el combo
}