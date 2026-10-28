package edu.etitc.gestor.controller;

import edu.etitc.gestor.model.Estado;
import edu.etitc.gestor.model.FiltroTareas;
import edu.etitc.gestor.model.Prioridad;
import edu.etitc.gestor.model.Tarea;
import edu.etitc.gestor.service.ServicioException;
import edu.etitc.gestor.service.TareaNoEncontradaException;
import edu.etitc.gestor.service.TareaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/tareas", "/tareas/*"})
public class TareaServlet extends HttpServlet {

    private static final Pattern RUTA_EDITAR = Pattern.compile("^/(\\d{1,9})/editar$");
    private static final Pattern RUTA_ELIMINAR = Pattern.compile("^/(\\d{1,9})/eliminar$");

    private final TareaService servicio = new TareaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int usuarioId = usuarioActual(req);
        String ruta = req.getPathInfo();   // null, "/", "/nueva", "/5/editar"...

        if (ruta == null || ruta.equals("/")) {
            mostrarLista(req, resp, usuarioId);
            return;
        }
        if (ruta.equals("/nueva")) {
            mostrarFormulario(req, resp, usuarioId, new Tarea(), false);
            return;
        }
        Matcher editar = RUTA_EDITAR.matcher(ruta);
        if (editar.matches()) {
            try {
                Tarea tarea = servicio.obtener(usuarioId, Integer.parseInt(editar.group(1)));
                mostrarFormulario(req, resp, usuarioId, tarea, true);
            } catch (TareaNoEncontradaException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (ServicioException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int usuarioId = usuarioActual(req);
        String ruta = req.getPathInfo();

        if ("/nueva".equals(ruta)) {
            guardarNueva(req, resp, usuarioId);
            return;
        }
        if (ruta != null) {
            Matcher editar = RUTA_EDITAR.matcher(ruta);
            if (editar.matches()) {
                guardarEdicion(req, resp, usuarioId, Integer.parseInt(editar.group(1)));
                return;
            }
            Matcher eliminar = RUTA_ELIMINAR.matcher(ruta);
            if (eliminar.matches()) {
                eliminar(req, resp, usuarioId, Integer.parseInt(eliminar.group(1)));
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void guardarNueva(HttpServletRequest req, HttpServletResponse resp, int usuarioId)
            throws ServletException, IOException {
        try {
            servicio.crear(usuarioId,
                    req.getParameter("titulo"),
                    req.getParameter("descripcion"),
                    req.getParameter("prioridad"),
                    req.getParameter("fechaLimite"),
                    req.getParameter("categoria"),
                    req.getParameter("responsable"));
            resp.sendRedirect(req.getContextPath() + "/tareas?msg=creada");
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarFormulario(req, resp, usuarioId, new Tarea(), false);
        }
    }

    private void guardarEdicion(HttpServletRequest req, HttpServletResponse resp,
                                int usuarioId, int tareaId) throws ServletException, IOException {
        Tarea tarea;
        try {
            tarea = servicio.obtener(usuarioId, tareaId);
        } catch (TareaNoEncontradaException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (ServicioException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        try {
            servicio.actualizar(usuarioId, tareaId,
                    req.getParameter("titulo"),
                    req.getParameter("descripcion"),
                    req.getParameter("estado"),
                    req.getParameter("prioridad"),
                    req.getParameter("fechaLimite"),
                    req.getParameter("categoria"),
                    req.getParameter("responsable"));
            resp.sendRedirect(req.getContextPath() + "/tareas?msg=actualizada");
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarFormulario(req, resp, usuarioId, tarea, true);
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp,
                          int usuarioId, int tareaId) throws ServletException, IOException {
        try {
            servicio.eliminar(usuarioId, tareaId);
            resp.sendRedirect(req.getContextPath() + "/tareas?msg=eliminada");
        } catch (TareaNoEncontradaException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarLista(req, resp, usuarioId);
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp, int usuarioId)
            throws ServletException, IOException {
        FiltroTareas filtro = new FiltroTareas();
        filtro.setTexto(req.getParameter("q"));
        filtro.setEstado(Estado.desdeTexto(req.getParameter("estado")));
        filtro.setCategoriaId(entero(req.getParameter("categoria")));
        filtro.setResponsableId(entero(req.getParameter("responsable")));
        filtro.setSoloVencidas("1".equals(req.getParameter("vencidas")));

        try {
            req.setAttribute("tareas", servicio.listar(usuarioId, filtro));
            req.setAttribute("categorias", servicio.listarCategorias());
            req.setAttribute("usuarios", servicio.listarUsuarios());
            req.setAttribute("estados", Estado.values());
            req.getRequestDispatcher("/WEB-INF/views/tareas.jsp").forward(req, resp);
        } catch (ServicioException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp,
                                   int usuarioId, Tarea tarea, boolean edicion)
            throws ServletException, IOException {
        try {
            req.setAttribute("tarea", tarea);
            req.setAttribute("edicion", edicion);
            req.setAttribute("puedeAsignar", !edicion || tarea.getCreadorId() == usuarioId);
            req.setAttribute("categorias", servicio.listarCategorias());
            req.setAttribute("usuarios", servicio.listarUsuarios());
            req.setAttribute("estados", Estado.values());
            req.setAttribute("prioridades", Prioridad.values());
            req.getRequestDispatcher("/WEB-INF/views/tarea-form.jsp").forward(req, resp);
        } catch (ServicioException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private int usuarioActual(HttpServletRequest req) {
        return (Integer) req.getSession().getAttribute("usuarioId");
    }

    private Integer entero(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}