package edu.etitc.gestor.dao;

import edu.etitc.gestor.model.Estado;
import edu.etitc.gestor.model.FiltroTareas;
import edu.etitc.gestor.model.Prioridad;
import edu.etitc.gestor.model.Tarea;
import edu.etitc.gestor.util.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    private static final String SELECT_BASE =
            "SELECT t.id, t.titulo, t.descripcion, t.estado, t.prioridad, t.fecha_limite, "
          + "t.categoria_id, t.responsable_id, t.creador_id, "
          + "c.nombre AS categoria_nombre, u.nombre AS responsable_nombre "
          + "FROM tareas t "
          + "LEFT JOIN categorias c ON c.id = t.categoria_id "
          + "LEFT JOIN usuarios u ON u.id = t.responsable_id ";

    public void crear(Tarea t) throws SQLException {
        String sql = "INSERT INTO tareas (titulo, descripcion, estado, prioridad, fecha_limite, "
                   + "categoria_id, responsable_id, creador_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarComunes(ps, t);
            ps.setInt(8, t.getCreadorId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Tarea t) throws SQLException {
        String sql = "UPDATE tareas SET titulo = ?, descripcion = ?, estado = ?, prioridad = ?, "
                   + "fecha_limite = ?, categoria_id = ?, responsable_id = ? WHERE id = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarComunes(ps, t);
            ps.setInt(8, t.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM tareas WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Tarea buscarPorId(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE t.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Tarea> listar(int usuarioId, FiltroTareas filtro) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE)
                .append("WHERE (t.creador_id = ? OR t.responsable_id = ?)");
        List<Object> params = new ArrayList<>();
        params.add(usuarioId);
        params.add(usuarioId);

        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            sql.append(" AND LOWER(t.titulo) LIKE ?");
            params.add("%" + filtro.getTexto().trim().toLowerCase() + "%");
        }
        if (filtro.getEstado() != null) {
            sql.append(" AND t.estado = ?");
            params.add(filtro.getEstado().name());
        }
        if (filtro.getCategoriaId() != null) {
            sql.append(" AND t.categoria_id = ?");
            params.add(filtro.getCategoriaId());
        }
        if (filtro.getResponsableId() != null) {
            sql.append(" AND t.responsable_id = ?");
            params.add(filtro.getResponsableId());
        }
        if (filtro.isSoloVencidas()) {
            sql.append(" AND t.fecha_limite < CURRENT_DATE AND t.estado <> 'TERMINADA'");
        }
        sql.append(" ORDER BY CASE WHEN t.fecha_limite IS NULL THEN 1 ELSE 0 END, t.fecha_limite, t.id DESC");

        List<Tarea> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private void asignarComunes(PreparedStatement ps, Tarea t) throws SQLException {
        ps.setString(1, t.getTitulo());
        ps.setString(2, t.getDescripcion());
        ps.setString(3, t.getEstado().name());
        ps.setString(4, t.getPrioridad().name());
        if (t.getFechaLimite() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(t.getFechaLimite()));
        }
        asignarEntero(ps, 6, t.getCategoriaId());
        asignarEntero(ps, 7, t.getResponsableId());
    }

    private void asignarEntero(PreparedStatement ps, int posicion, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(posicion, Types.INTEGER);
        } else {
            ps.setInt(posicion, valor);
        }
    }

    private Integer enteroONull(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }

    private Tarea mapear(ResultSet rs) throws SQLException {
        Tarea t = new Tarea();
        t.setId(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescripcion(rs.getString("descripcion"));
        t.setEstado(Estado.valueOf(rs.getString("estado")));
        t.setPrioridad(Prioridad.valueOf(rs.getString("prioridad")));
        Date fecha = rs.getDate("fecha_limite");
        if (fecha != null) {
            t.setFechaLimite(fecha.toLocalDate());
        }
        t.setCategoriaId(enteroONull(rs, "categoria_id"));
        t.setResponsableId(enteroONull(rs, "responsable_id"));
        t.setCreadorId(rs.getInt("creador_id"));
        t.setCategoriaNombre(rs.getString("categoria_nombre"));
        t.setResponsableNombre(rs.getString("responsable_nombre"));
        return t;
    }
}