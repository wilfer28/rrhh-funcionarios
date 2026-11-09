package com.iudigital.rrhh.dao;

import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.modelo.Funcionario;
import java.util.List;

public interface FuncionarioDao {
    List<Funcionario> listar() throws DaoException;
    Funcionario buscarPorId(int id) throws DaoException;
    void crear(Funcionario f) throws DaoException;
    void actualizar(Funcionario f) throws DaoException;
    void eliminar(int id) throws DaoException;
}
