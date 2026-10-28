package edu.etitc.gestor.service;

import edu.etitc.gestor.dao.CategoriaDAO;
import edu.etitc.gestor.dao.TareaDAO;
import edu.etitc.gestor.dao.UsuarioDAO;
import edu.etitc.gestor.model.Categoria;
import edu.etitc.gestor.model.Estado;
import edu.etitc.gestor.model.FiltroTareas;
import edu.etitc.gestor.model.Prioridad;
import edu.etitc.gestor.model.Tarea;
import edu.etitc.gestor.model.Usuario;
import edu.etitc.gestor.util.Validador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TareaService {

    private static final int MAX_TITULO = 150;
    private static final int MAX_DESCRIPCION = 1000;

    private final TareaDAO tareaDAO = new TareaDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Tarea> listar(int usuarioId, FiltroTareas filtro) throws ServicioException {
        try {
            return tareaDAO.listar(usuarioId, filtro);
        } catch (SQLException e) {
            throw new ServicioException("No se pudieron cargar las tareas.", e);
        }
    }

    public Tarea obtener(int usuarioId, int tareaId) throws ServicioException {
        try {
            Tarea tarea = tareaDAO.buscarPorId(tareaId);
            if (tarea == null || !tarea.puedeAcceder(usuarioId)) {
                throw new TareaNoEncontradaException();   // mismo error en ambos casos
            }
            return tarea;
        } catch (SQLException e) {
            throw new ServicioException("No se pudo cargar la tarea.", e);
        }
    }

    public List<Categoria> listarCategorias() throws ServicioException {
        try {
            return categoriaDAO.listar();
        } catch (SQLException e) {
            throw new ServicioException("No se pudieron cargar las categorías.", e);
        }
    }

    public List<Usuario> listarUsuarios() throws ServicioException {
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException e) {
            throw new ServicioException("No se pudieron cargar los usuarios.", e);
        }
    }


    /** Regla 2: el estado no es un parámetro, siempre se crea como PENDIENTE. */
    public Tarea crear(int usuarioId, String titulo, String descripcion, String prioridad,
                       String fechaLimite, String categoriaId, String responsableId)
            throws ServicioException {

        Tarea tarea = new Tarea();
        tarea.setCreadorId(usuarioId);
        tarea.setEstado(Estado.PENDIENTE);
        try {
            aplicarDatos(tarea, titulo, descripcion, prioridad, fechaLimite,
                    categoriaId, responsableId, true);
            tareaDAO.crear(tarea);
            return tarea;
        } catch (SQLException e) {
            throw new ServicioException("No se pudo crear la tarea. Intenta de nuevo.", e);
        }
    }

    public Tarea actualizar(int usuarioId, int tareaId, String titulo, String descripcion,
                            String estado, String prioridad, String fechaLimite,
                            String categoriaId, String responsableId)
            throws ServicioException {

        Tarea tarea = obtener(usuarioId, tareaId);   // verifica permiso (regla 5)

        Estado nuevoEstado = Estado.desdeTexto(estado);   // regla 1: estado válido
        if (nuevoEstado == null) {
            throw new ServicioException("El estado debe ser PENDIENTE, EN_PROGRESO o TERMINADA.");
        }
        try {
            boolean puedeAsignar = tarea.getCreadorId() == usuarioId;
            aplicarDatos(tarea, titulo, descripcion, prioridad, fechaLimite,
                    categoriaId, responsableId, puedeAsignar);
            tarea.setEstado(nuevoEstado);
            tareaDAO.actualizar(tarea);
            return tarea;
        } catch (SQLException e) {
            throw new ServicioException("No se pudo guardar la tarea. Intenta de nuevo.", e);
        }
    }

    public void eliminar(int usuarioId, int tareaId) throws ServicioException {
        Tarea tarea = obtener(usuarioId, tareaId);
        if (tarea.getCreadorId() != usuarioId) {
            throw new ServicioException("Solo quien creó la tarea puede eliminarla.");
        }
        try {
            tareaDAO.eliminar(tareaId);
        } catch (SQLException e) {
            throw new ServicioException("No se pudo eliminar la tarea. Intenta de nuevo.", e);
        }
    }

    private void aplicarDatos(Tarea tarea, String titulo, String descripcion, String prioridad,
                              String fechaLimite, String categoria, String responsable,
                              boolean puedeAsignar) throws ServicioException, SQLException {

        if (Validador.vacio(titulo)) {
            throw new ServicioException("El título es obligatorio.");
        }
        if (titulo.trim().length() > MAX_TITULO) {
            throw new ServicioException("El título no puede superar " + MAX_TITULO + " caracteres.");
        }
        if (descripcion != null && descripcion.trim().length() > MAX_DESCRIPCION) {
            throw new ServicioException("La descripción no puede superar " + MAX_DESCRIPCION + " caracteres.");
        }

        Prioridad nuevaPrioridad = Prioridad.desdeTexto(prioridad);
        if (nuevaPrioridad == null) {
            throw new ServicioException("La prioridad debe ser BAJA, MEDIA o ALTA.");
        }

        LocalDate limite = null;
        if (!Validador.vacio(fechaLimite)) {
            try {
                limite = LocalDate.parse(fechaLimite.trim());
            } catch (DateTimeParseException e) {
                throw new ServicioException("La fecha límite no es válida.");
            }
        }

        Integer categoriaId = parsearId(categoria, "La categoría no es válida.");
        if (categoriaId != null && !categoriaDAO.existe(categoriaId)) {
            throw new ServicioException("La categoría seleccionada no existe.");
        }

        Integer responsableId = tarea.getResponsableId();
        if (puedeAsignar) {
            responsableId = parsearId(responsable, "El responsable no es válido.");
            if (responsableId != null && usuarioDAO.buscarPorId(responsableId) == null) {
                throw new ServicioException("El responsable seleccionado no existe.");
            }
        }

        tarea.setTitulo(titulo.trim());
        tarea.setDescripcion(Validador.vacio(descripcion) ? null : descripcion.trim());
        tarea.setPrioridad(nuevaPrioridad);
        tarea.setFechaLimite(limite);
        tarea.setCategoriaId(categoriaId);
        tarea.setResponsableId(responsableId);
    }

    private Integer parsearId(String texto, String mensaje) throws ServicioException {
        if (Validador.vacio(texto)) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new ServicioException(mensaje);
        }
    }
}