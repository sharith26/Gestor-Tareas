<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registro - Gestor de tareas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<main class="tarjeta">
    <h1>Crear cuenta</h1>

    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/registro">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <label for="nombre">Nombre</label>
        <input type="text" id="nombre" name="nombre" value="<c:out value='${nombre}'/>" required>

        <label for="email">Correo</label>
        <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>

        <label for="password">Contraseña (mínimo 8 caracteres)</label>
        <input type="password" id="password" name="password" required>

        <label for="confirmacion">Confirmar contraseña</label>
        <input type="password" id="confirmacion" name="confirmacion" required>

        <button type="submit">Registrarme</button>
    </form>

    <p class="enlace">¿Ya tienes cuenta?
        <a href="${pageContext.request.contextPath}/login">Inicia sesión</a>
    </p>
</main>
</body>
</html>