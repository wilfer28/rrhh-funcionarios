package com.iudigital.rrhh.dao;

import com.iudigital.rrhh.excepciones.DaoException;
import com.iudigital.rrhh.modelo.Catalogo;
import java.util.List;

public interface CatalogoDao {
    List<Catalogo> listar(String tabla) throws DaoException;
}