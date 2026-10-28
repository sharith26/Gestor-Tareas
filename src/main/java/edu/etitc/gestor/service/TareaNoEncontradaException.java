package edu.etitc.gestor.service;

public class TareaNoEncontradaException extends ServicioException {

    public TareaNoEncontradaException() {
        super("Tarea no encontrada.");
    }
}