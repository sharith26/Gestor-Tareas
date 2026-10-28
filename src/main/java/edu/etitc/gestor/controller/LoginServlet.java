package edu.etitc.gestor.controller;

import edu.etitc.gestor.model.Usuario;
import edu.etitc.gestor.service.ServicioException;
import edu.etitc.gestor.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioService servicio = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion != null && sesion.getAttribute("usuarioId") != null) {
            resp.sendRedirect(req.getContextPath() + "/inicio");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Usuario usuario = servicio.autenticar(email, password);

            HttpSession anterior = req.getSession(false);
            if (anterior != null) {
                anterior.invalidate();
            }
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("usuarioId", usuario.getId());
            sesion.setAttribute("usuarioNombre", usuario.getNombre());

            resp.sendRedirect(req.getContextPath() + "/inicio");
        } catch (ServicioException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}