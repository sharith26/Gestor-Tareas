package edu.etitc.gestor.dao;

import edu.etitc.gestor.model.Categoria;
import edu.etitc.gestor.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categorias ORDER BY nombre";
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    public boolean existe(int id) throws SQLException {
        String sql = "SELECT 1 FROM categorias WHERE id = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeNombre(String nombre, int excluirId) throws SQLException {
        String sql = "SELECT 1 FROM categorias WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarTareas(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tareas WHERE categoria_id = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void crear(String nombre) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("INSERT INTO categorias (nombre) VALUES (?)")) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    public void actualizar(int id, String nombre) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("UPDATE categorias SET nombre = ? WHERE id = ?")) {
            ps.setString(1, nombre);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("DELETE FROM categorias WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}