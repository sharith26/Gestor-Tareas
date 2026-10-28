package edu.etitc.gestor.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class InicializadorBD implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConexionBD.inicializarEsquema();
    }
}