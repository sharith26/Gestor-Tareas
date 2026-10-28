package edu.etitc.gestor.service;

import java.sql.SQLException;
import java.util.List;

import edu.etitc.gestor.dao.CategoriaDAO;
import edu.etitc.gestor.model.Categoria;
import edu.etitc.gestor.util.Validador;

public class CategoriaService {

    private static final int MAX_NOMBRE = 80;

    private final CategoriaDAO dao = new CategoriaDAO();

    public List<Categoria> listar() throws ServicioException {
        try {
            return dao.listar();
        } catch (SQLException e) {
            throw new ServicioException("No se pudieron cargar las categorías.", e);
        }
    }

    public void crear(String nombre) throws ServicioException {
        String limpio = validarNombre(nombre);
        try {
            if (dao.existeNombre(limpio, 0)) {
                throw new ServicioException("Ya existe una categoría con ese nombre.");
            }
            dao.crear(limpio);
        } catch (SQLException e) {
            throw new ServicioException("No se pudo crear la categoría. Intenta de nuevo.", e);
        }
    }

    public void actualizar(int id, String nombre) throws ServicioException {
        String limpio = validarNombre(nombre);
        try {
            if (!dao.existe(id)) {
                throw new ServicioException("La categoría no existe.");
            }
            if (dao.existeNombre(limpio, id)) {
                throw new ServicioException("Ya existe una categoría con ese nombre.");
            }
            dao.actualizar(id, limpio);
        } catch (SQLException e) {
            throw new ServicioException("No se pudo guardar la categoría. Intenta de nuevo.", e);
        }
    }

    public void eliminar(int id) throws ServicioException {
        try {
            if (!dao.existe(id)) {
                throw new ServicioException("La categoría no existe.");
            }
            int tareas = dao.contarTareas(id);
            if (tareas > 0) {
                throw new ServicioException("No se puede eliminar: tiene " + tareas
                        + (tareas == 1 ? " tarea asociada." : " tareas asociadas."));
            }
            dao.eliminar(id);
        } catch (SQLException e) {
            throw new ServicioException("No se pudo eliminar la categoría. Intenta de nuevo.", e);
        }
    }

    private String validarNombre(String nombre) throws ServicioException {
        if (Validador.vacio(nombre)) {
            throw new ServicioException("El nombre de la categoría es obligatorio.");
        }
        String limpio = nombre.trim();
        if (limpio.length() > MAX_NOMBRE) {
            throw new ServicioException("El nombre no puede superar " + MAX_NOMBRE + " caracteres.");
        }
        return limpio;
    }
}