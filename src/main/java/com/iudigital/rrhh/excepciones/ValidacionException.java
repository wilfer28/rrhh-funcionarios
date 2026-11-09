package com.iudigital.rrhh.excepciones;

// Errores de validación de los datos que escribe el usuario
public class ValidacionException extends Exception {
    public ValidacionException(String mensaje) { super(mensaje); }
}