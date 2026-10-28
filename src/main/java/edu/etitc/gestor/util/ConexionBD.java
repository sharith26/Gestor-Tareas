package edu.etitc.gestor.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public final class ConexionBD {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {
    }

    /** Abre una conexión nueva. Quien la pida debe cerrarla (try-with-resources). */
    public static Connection obtener() throws SQLException {
        String url = PROPS.getProperty("db.url")
                .replace("${user.home}", System.getProperty("user.home").replace('\\', '/'));
        return DriverManager.getConnection(
                url,
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password", ""));
    }

    /** Ejecuta schema.sql para crear las tablas si no existen. */
    public static void inicializarEsquema() {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró schema.sql");
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            // Quitar comentarios de línea
            StringBuilder limpio = new StringBuilder();
            for (String linea : script.split("\\R")) {
                if (!linea.trim().startsWith("--")) {
                    limpio.append(linea).append('\n');
                }
            }

            try (Connection con = obtener(); Statement st = con.createStatement()) {
                for (String sentencia : limpio.toString().split(";")) {
                    if (!sentencia.isBlank()) {
                        st.execute(sentencia);
                    }
                }
            }
            System.out.println("[BD] Esquema listo.");
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Error inicializando la base de datos", e);
        }
    }
}