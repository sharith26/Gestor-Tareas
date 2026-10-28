package edu.etitc.gestor.model;

public class FiltroTareas {

    private String texto;
    private Estado estado;
    private Integer categoriaId;
    private Integer responsableId;
    private boolean soloVencidas;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }

    public Integer getResponsableId() { return responsableId; }
    public void setResponsableId(Integer responsableId) { this.responsableId = responsableId; }

    public boolean isSoloVencidas() { return soloVencidas; }
    public void setSoloVencidas(boolean soloVencidas) { this.soloVencidas = soloVencidas; }
}