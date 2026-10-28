package edu.etitc.gestor.service;

import edu.etitc.gestor.dao.UsuarioDAO;
import edu.etitc.gestor.model.Usuario;
import edu.etitc.gestor.util.PasswordUtil;
import edu.etitc.gestor.util.Validador;

import java.sql.SQLException;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario registrar(String nombre, String email, String password, String confirmacion)
            throws ServicioException {

        if (Validador.vacio(nombre)) {
            throw new ServicioException("El nombre es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new ServicioException("El nombre no puede superar 100 caracteres.");
        }
        if (!Validador.emailValido(email)) {
            throw new ServicioException("Ingresa un correo válido.");
        }
        if (password == null || password.length() < 8) {
            throw new ServicioException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!password.equals(confirmacion)) {
            throw new ServicioException("Las contraseñas no coinciden.");
        }

        String emailNormalizado = email.trim().toLowerCase();

        try {
            if (dao.existeEmail(emailNormalizado)) {
                throw new ServicioException("Ese correo ya está registrado.");
            }
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setEmail(emailNormalizado);
            usuario.setPasswordHash(PasswordUtil.hashear(password));
            dao.crear(usuario);
            return usuario;
        } catch (SQLException e) {
            throw new ServicioException("No se pudo completar el registro. Intenta de nuevo.", e);
        }
    }

    public Usuario autenticar(String email, String password) throws ServicioException {
        if (Validador.vacio(email) || Validador.vacio(password)) {
            throw new ServicioException("Ingresa tu correo y tu contraseña.");
        }
        try {
            Usuario usuario = dao.buscarPorEmail(email.trim().toLowerCase());
            if (usuario == null || !PasswordUtil.verificar(password, usuario.getPasswordHash())) {
                throw new ServicioException("Correo o contraseña incorrectos.");
            }
            return usuario;
        } catch (SQLException e) {
            throw new ServicioException("No se pudo iniciar sesión. Intenta de nuevo.", e);
        }
    }
}