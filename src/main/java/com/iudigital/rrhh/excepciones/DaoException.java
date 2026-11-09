package com.iudigital.rrhh.excepciones;

// Errores de acceso a datos (SQL, conexión, duplicados)
public class DaoException extends Exception {
    public DaoException(String mensaje, Throwable causa) { super(mensaje, causa); }
    public DaoException(String mensaje) { super(mensaje); }
}