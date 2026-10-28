package edu.etitc.gestor.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class CsrfFilter implements Filter {

    private static final String ATRIBUTO = "csrfToken";
    private static final String PARAMETRO = "csrf";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String ruta = req.getRequestURI().substring(req.getContextPath().length());
        if (ruta.startsWith("/css/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(true);
        String token = (String) sesion.getAttribute(ATRIBUTO);
        if (token == null) {
            token = nuevoToken();
            sesion.setAttribute(ATRIBUTO, token);
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            req.setCharacterEncoding("UTF-8");
            String enviado = req.getParameter(PARAMETRO);
            boolean valido = enviado != null && MessageDigest.isEqual(
                    enviado.getBytes(StandardCharsets.UTF_8),
                    token.getBytes(StandardCharsets.UTF_8));
            if (!valido) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Solicitud no válida. Recarga la página e intenta de nuevo.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String nuevoToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}