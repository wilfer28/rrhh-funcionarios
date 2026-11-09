package com.iudigital.rrhh.util;

import com.iudigital.rrhh.excepciones.DaoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = System.getenv().getOrDefault("RRHH_DB_URL",
        "jdbc:mysql://localhost:3306/rrhh_iudigital?serverTimezone=America/Bogota");
    private static final String USER = System.getenv().getOrDefault("RRHH_DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("RRHH_DB_PASS", "");

    public static Connection getConnection() throws DaoException {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new DaoException("No se pudo conectar a la base de datos. Verifique que MySQL esté activo.", e);
        }
    }
}
