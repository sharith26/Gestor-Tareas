package edu.etitc.gestor.controller;

import edu.etitc.gestor.service.ServicioException;
import edu.etitc.gestor.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private final UsuarioService servicio = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/registro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String nombre = req.getParameter("nombre");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmacion = req.getParameter("confirmacion");

        try {
            servicio.registrar(nombre, email, password, confirmacion);
            resp.sendRedirect(req.getContextPath() + "/login?registrado=1");
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("nombre", nombre);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/registro.jsp").forward(req, resp);
        }
    }
}