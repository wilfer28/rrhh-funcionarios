package com.iudigital.rrhh.dao.impl;

import com.iudigital.rrhh.dao.CatalogoDao;
import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.modelo.Catalogo;
import com.iudigital.rrhh.util.Conexion;
import java.sql.*;
import java.util.*;

public class CatalogoDaoImpl implements CatalogoDao {
    private static final Set<String> PERMITIDAS = Set.of("tipo_documento", "estado_civil");

    @Override
    public List<Catalogo> listar(String tabla) throws DaoException {
        if (!PERMITIDAS.contains(tabla)) throw new DaoException("Catálogo no permitido: " + tabla);
        List<Catalogo> lista = new ArrayList<>();
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre FROM " + tabla + " ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Catalogo(rs.getInt("id"), rs.getString("nombre")));
        } catch (SQLException e) {
            throw new DaoException("Error al consultar el catálogo " + tabla, e);
        }
        return lista;
    }
}
