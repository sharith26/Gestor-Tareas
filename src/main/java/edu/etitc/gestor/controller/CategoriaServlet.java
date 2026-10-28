package edu.etitc.gestor.controller;

import edu.etitc.gestor.service.CategoriaService;
import edu.etitc.gestor.service.ServicioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/categorias", "/categorias/*"})
public class CategoriaServlet extends HttpServlet {

    private static final Pattern RUTA_EDITAR = Pattern.compile("^/(\\d{1,9})/editar$");
    private static final Pattern RUTA_ELIMINAR = Pattern.compile("^/(\\d{1,9})/eliminar$");

    private final CategoriaService servicio = new CategoriaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String ruta = req.getPathInfo();
        if (ruta == null || ruta.equals("/")) {
            mostrarLista(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String ruta = req.getPathInfo();

        try {
            if (ruta == null || ruta.equals("/")) {
                servicio.crear(req.getParameter("nombre"));
                resp.sendRedirect(req.getContextPath() + "/categorias?msg=creada");
                return;
            }
            Matcher editar = RUTA_EDITAR.matcher(ruta);
            if (editar.matches()) {
                servicio.actualizar(Integer.parseInt(editar.group(1)), req.getParameter("nombre"));
                resp.sendRedirect(req.getContextPath() + "/categorias?msg=actualizada");
                return;
            }
            Matcher eliminar = RUTA_ELIMINAR.matcher(ruta);
            if (eliminar.matches()) {
                servicio.eliminar(Integer.parseInt(eliminar.group(1)));
                resp.sendRedirect(req.getContextPath() + "/categorias?msg=eliminada");
                return;
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            mostrarLista(req, resp);
        }
    }

    private void mostrarLista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("categorias", servicio.listar());
            req.getRequestDispatcher("/WEB-INF/views/categorias.jsp").forward(req, resp);
        } catch (ServicioException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}