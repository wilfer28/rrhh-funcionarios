package com.iudigital.rrhh.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Funcionario {
    private int id;
    private Catalogo tipoDocumento;
    private String numeroIdentificacion, nombres, apellidos, sexo, direccion, telefono;
    private Catalogo estadoCivil;
    private LocalDate fechaNacimiento;
    private List<GrupoFamiliar> grupoFamiliar = new ArrayList<>();
    private List<FormacionAcademica> formaciones = new ArrayList<>();
    // + constructor vacío y getters/setters de TODOS los atributos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Catalogo getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(Catalogo tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Catalogo getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(Catalogo estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public List<GrupoFamiliar> getGrupoFamiliar() {
        return grupoFamiliar;
    }

    public void setGrupoFamiliar(List<GrupoFamiliar> grupoFamiliar) {
        this.grupoFamiliar = grupoFamiliar;
    }

    public List<FormacionAcademica> getFormaciones() {
        return formaciones;
    }

    public void setFormaciones(List<FormacionAcademica> formaciones) {
        this.formaciones = formaciones;
    }

   
}