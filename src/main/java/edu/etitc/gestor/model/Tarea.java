package edu.etitc.gestor.model;

import java.time.LocalDate;

public class Tarea {

    private int id;
    private String titulo;
    private String descripcion;
    private Estado estado = Estado.PENDIENTE;
    private Prioridad prioridad = Prioridad.MEDIA;
    private LocalDate fechaLimite;
    private Integer categoriaId;
    private Integer responsableId;
    private int creadorId;

    private String categoriaNombre;
    private String responsableNombre;

    public Tarea() {
    }

    public boolean isVencida() {
        return fechaLimite != null
                && estado != Estado.TERMINADA
                && fechaLimite.isBefore(LocalDate.now());
    }

    public boolean puedeAcceder(int usuarioId) {
        return creadorId == usuarioId
                || (responsableId != null && responsableId == usuarioId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }

    public Integer getResponsableId() { return responsableId; }
    public void setResponsableId(Integer responsableId) { this.responsableId = responsableId; }

    public int getCreadorId() { return creadorId; }
    public void setCreadorId(int creadorId) { this.creadorId = creadorId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public String getResponsableNombre() { return responsableNombre; }
    public void setResponsableNombre(String responsableNombre) { this.responsableNombre = responsableNombre; }
}