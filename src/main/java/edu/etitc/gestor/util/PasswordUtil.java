package edu.etitc.gestor.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashear(String password) {
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        byte[] hash = derivar(password.toCharArray(), sal, ITERACIONES);
        return ITERACIONES + ":"
                + Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String password, String almacenado) {
        try {
            String[] partes = almacenado.split(":");
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            byte[] calculado = derivar(password.toCharArray(), sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] password, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, sal, iteraciones, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }
}