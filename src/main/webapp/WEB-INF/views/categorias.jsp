<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Categorías - Gestor de tareas</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body class="app">
<div class="contenedor angosto">

    <div class="barra">
        <h1>Categorías</h1>
        <a class="btn btn-gris" href="${ctx}/tareas">Volver a las tareas</a>
    </div>

    <c:if test="${param.msg == 'creada'}"><p class="ok">Categoría creada.</p></c:if>
    <c:if test="${param.msg == 'actualizada'}"><p class="ok">Categoría actualizada.</p></c:if>
    <c:if test="${param.msg == 'eliminada'}"><p class="ok">Categoría eliminada.</p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>

    <form method="post" action="${ctx}/categorias" class="panel fila-inline">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
        <input type="text" name="nombre" maxlength="80" placeholder="Nombre de la nueva categoría" required>
        <button type="submit" class="btn">Agregar</button>
    </form>

    <div class="panel">
        <c:choose>
            <c:when test="${empty categorias}">
                <p>Aún no hay categorías.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="cat" items="${categorias}">
                    <div class="fila-categoria">
                        <form method="post" action="${ctx}/categorias/${cat.id}/editar" class="fila-inline">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                            <input type="text" name="nombre" maxlength="80" required
                                   value="<c:out value='${cat.nombre}'/>">
                            <button type="submit" class="btn btn-sm">Guardar</button>
                        </form>
                        <form method="post" action="${ctx}/categorias/${cat.id}/eliminar"
                              onsubmit="return confirm('¿Eliminar esta categoría?');">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                            <button type="submit" class="btn btn-sm btn-rojo">Eliminar</button>
                        </form>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>