package edu.etitc.gestor.model;

public enum Prioridad {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta");

    private final String etiqueta;

    Prioridad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static Prioridad desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Prioridad.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}