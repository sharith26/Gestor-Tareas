package edu.etitc.gestor.dao;

import edu.etitc.gestor.model.Usuario;
import edu.etitc.gestor.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, password_hash) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getPasswordHash());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nombre, email, password_hash FROM usuarios WHERE email = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("password_hash"));
                }
            }
        }
        return null;
    }

    public boolean existeEmail(String email) throws SQLException {
        return buscarPorEmail(email) != null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, email, password_hash FROM usuarios WHERE id = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("password_hash"));
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email FROM usuarios ORDER BY nombre";
        try (Connection con = ConexionBD.obtener();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"), null));
            }
        }
        return lista;
    }
}