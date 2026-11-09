package com.iudigital.rrhh.dao.impl;

import com.iudigital.rrhh.dao.FuncionarioDao;
import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.modelo.*;
import com.iudigital.rrhh.util.Conexion;
import java.sql.*;
import java.util.*;

public class FuncionarioDaoImpl implements FuncionarioDao {

    private static final String SQL_LISTAR =
        "SELECT f.*, td.nombre AS td_nombre, ec.nombre AS ec_nombre FROM funcionarios f "
      + "JOIN tipo_documento td ON td.id = f.tipo_documento_id "
      + "JOIN estado_civil ec ON ec.id = f.estado_civil_id ORDER BY f.apellidos, f.nombres";
    private static final String SQL_BUSCAR = "SELECT f.*, td.nombre AS td_nombre, ec.nombre AS ec_nombre FROM funcionarios f "
      + "JOIN tipo_documento td ON td.id = f.tipo_documento_id "
      + "JOIN estado_civil ec ON ec.id = f.estado_civil_id WHERE f.id = ?";
    private static final String SQL_INSERT = "INSERT INTO funcionarios (tipo_documento_id, numero_identificacion, "
      + "nombres, apellidos, estado_civil_id, sexo, direccion, telefono, fecha_nacimiento) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE funcionarios SET tipo_documento_id=?, numero_identificacion=?, "
      + "nombres=?, apellidos=?, estado_civil_id=?, sexo=?, direccion=?, telefono=?, fecha_nacimiento=? WHERE id=?";

    @Override
    public List<Funcionario> listar() throws DaoException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar los funcionarios", e);
        }
        return lista;
    }

    @Override
    public Funcionario buscarPorId(int id) throws DaoException {
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_BUSCAR)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DaoException("No existe el funcionario con id " + id);
                Funcionario f = mapear(rs);
                cargarHijos(c, f);
                return f;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el funcionario", e);
        }
    }

    @Override
    public void crear(Funcionario f) throws DaoException {
        try (Connection c = Conexion.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    asignar(ps, f);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); id = rs.getInt(1); }
                }
                guardarHijos(c, id, f);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DaoException("Ya existe un funcionario con ese número de identificación", e);
        } catch (SQLException e) {
            throw new DaoException("Error al crear el funcionario", e);
        }
    }

    @Override
    public void actualizar(Funcionario f) throws DaoException {
        try (Connection c = Conexion.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(SQL_UPDATE)) {
                    asignar(ps, f);
                    ps.setInt(10, f.getId());
                    ps.executeUpdate();
                }
                borrarHijos(c, f.getId());   // se reemplazan por los de la pantalla
                guardarHijos(c, f.getId(), f);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DaoException("Ya existe otro funcionario con ese número de identificación", e);
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el funcionario", e);
        }
    }

    @Override
    public void eliminar(int id) throws DaoException {
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM funcionarios WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();   // ON DELETE CASCADE borra familia y formación
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el funcionario", e);
        }
    }

    // ---------- métodos auxiliares ----------
    private void asignar(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setInt(1, f.getTipoDocumento().getId());
        ps.setString(2, f.getNumeroIdentificacion());
        ps.setString(3, f.getNombres());
        ps.setString(4, f.getApellidos());
        ps.setInt(5, f.getEstadoCivil().getId());
        ps.setString(6, (String) f.getSexo());
        ps.setString(7, f.getDireccion());
        ps.setString(8, f.getTelefono());
        ps.setDate(9, java.sql.Date.valueOf(f.getFechaNacimiento()));
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setTipoDocumento(new Catalogo(rs.getInt("tipo_documento_id"), rs.getString("td_nombre")));
        f.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setEstadoCivil(new Catalogo(rs.getInt("estado_civil_id"), rs.getString("ec_nombre")));
        f.setSexo(rs.getString("sexo"));
        f.setDireccion(rs.getString("direccion"));
        f.setTelefono(rs.getString("telefono"));
        f.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        return f;
    }

    private void cargarHijos(Connection c, Funcionario f) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT nombres, apellidos, fecha_nacimiento, rol FROM grupo_familiar WHERE funcionario_id = ?")) {
            ps.setInt(1, f.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GrupoFamiliar g = new GrupoFamiliar();
                    g.setNombres(rs.getString(1));
                    g.setApellidos(rs.getString(2));
                    var d = rs.getDate(3);
                    g.setFechaNacimiento(d == null ? null : d.toLocalDate());
                    g.setRol(rs.getString(4));
                    f.getGrupoFamiliar().add(g);
                }
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT universidad, nivel_estudio, titulo FROM formacion_academica WHERE funcionario_id = ?")) {
            ps.setInt(1, f.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FormacionAcademica a = new FormacionAcademica();
                    a.setUniversidad(rs.getString(1));
                    a.setNivelEstudio(rs.getString(2));
                    a.setTitulo(rs.getString(3));
                    f.getFormaciones().add(a);
                }
            }
        }
    }

    private void borrarHijos(Connection c, int id) throws SQLException {
        for (String t : new String[]{"grupo_familiar", "formacion_academica"}) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM " + t + " WHERE funcionario_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    private void guardarHijos(Connection c, int id, Funcionario f) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO grupo_familiar (funcionario_id, nombres, apellidos, fecha_nacimiento, rol) VALUES (?,?,?,?,?)")) {
            for (GrupoFamiliar g : f.getGrupoFamiliar()) {
                ps.setInt(1, id);
                ps.setString(2, (String) g.getNombres());
                ps.setString(3, (String) g.getApellidos());
                ps.setDate(4, g.getFechaNacimiento() == null ? null : java.sql.Date.valueOf(g.getFechaNacimiento()));
                ps.setString(5, (String) g.getRol());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO formacion_academica (funcionario_id, universidad, nivel_estudio, titulo) VALUES (?,?,?,?)")) {
            for (FormacionAcademica a : f.getFormaciones()) {
                ps.setInt(1, id);
                ps.setString(2, (String) a.getUniversidad());
                ps.setString(3, (String) a.getNivelEstudio());
                ps.setString(4, (String) a.getTitulo());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}