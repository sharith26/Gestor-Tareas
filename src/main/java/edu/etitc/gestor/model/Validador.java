package edu.etitc.gestor.util;

import java.util.regex.Pattern;

public final class Validador {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validador() {
    }

    public static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }
}