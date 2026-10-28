<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión - Gestor de tareas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<main class="tarjeta">
    <h1>Iniciar sesión</h1>

    <c:if test="${not empty param.registrado}">
        <p class="ok">Cuenta creada. Ya puedes iniciar sesión.</p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <label for="email">Correo</label>
        <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required>

        <label for="password">Contraseña</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Entrar</button>
    </form>

    <p class="enlace">¿No tienes cuenta?
        <a href="${pageContext.request.contextPath}/registro">Regístrate</a>
    </p>
</main>
</body>
</html>