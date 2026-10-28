package edu.etitc.gestor.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/inicio", "/tareas", "/tareas/*", "/categorias", "/categorias/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession sesion = req.getSession(false);
        boolean autenticado = sesion != null && sesion.getAttribute("usuarioId") != null;

        if (!autenticado) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}