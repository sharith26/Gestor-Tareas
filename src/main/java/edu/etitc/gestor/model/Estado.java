package edu.etitc.gestor.model;

public enum Estado {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    TERMINADA("Terminada");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Estado desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Estado.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}