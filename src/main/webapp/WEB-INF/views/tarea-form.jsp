<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- Si hubo error se conservan los valores que escribió el usuario; si no, los de la tarea --%>
<c:set var="vTitulo" value="${empty error ? tarea.titulo : param.titulo}"/>
<c:set var="vDescripcion" value="${empty error ? tarea.descripcion : param.descripcion}"/>
<c:set var="vEstado" value="${empty error ? tarea.estado.name() : param.estado}"/>
<c:set var="vPrioridad" value="${empty error ? tarea.prioridad.name() : param.prioridad}"/>
<c:set var="vFecha" value="${empty error ? tarea.fechaLimite : param.fechaLimite}"/>
<c:set var="vCategoria" value="${empty error ? tarea.categoriaId : param.categoria}"/>
<c:set var="vResponsable" value="${empty error ? tarea.responsableId : param.responsable}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${edicion ? 'Editar tarea' : 'Nueva tarea'} - Gestor de tareas</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body class="app">
<main class="tarjeta ancha">
    <h1>${edicion ? 'Editar tarea' : 'Nueva tarea'}</h1>

    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form method="post"
          action="${ctx}/tareas/${edicion ? tarea.id : 'nueva'}${edicion ? '/editar' : ''}">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">

        <label for="titulo">Título *</label>
        <input type="text" id="titulo" name="titulo" maxlength="150" required
               value="<c:out value='${vTitulo}'/>">

        <label for="descripcion">Descripción</label>
        <textarea id="descripcion" name="descripcion" rows="3" maxlength="1000"><c:out value="${vDescripcion}"/></textarea>

        <c:choose>
            <c:when test="${edicion}">
                <label for="estado">Estado *</label>
                <select id="estado" name="estado" required>
                    <c:forEach var="e" items="${estados}">
                        <option value="${e.name()}" ${e.name() == vEstado ? 'selected' : ''}>
                            <c:out value="${e.etiqueta}"/>
                        </option>
                    </c:forEach>
                </select>
            </c:when>
            <c:otherwise>
                <p class="suave">La tarea se creará con estado <strong>Pendiente</strong>.</p>
            </c:otherwise>
        </c:choose>

        <label for="prioridad">Prioridad *</label>
        <select id="prioridad" name="prioridad" required>
            <c:forEach var="p" items="${prioridades}">
                <option value="${p.name()}" ${p.name() == vPrioridad ? 'selected' : ''}>
                    <c:out value="${p.etiqueta}"/>
                </option>
            </c:forEach>
        </select>

        <label for="fechaLimite">Fecha límite (opcional)</label>
        <input type="date" id="fechaLimite" name="fechaLimite" value="<c:out value='${vFecha}'/>">

        <label for="categoria">Categoría</label>
        <select id="categoria" name="categoria">
            <option value="">Sin categoría</option>
            <c:forEach var="c" items="${categorias}">
                <option value="${c.id}" ${c.id == vCategoria ? 'selected' : ''}>
                    <c:out value="${c.nombre}"/>
                </option>
            </c:forEach>
        </select>

        <label for="responsable">Responsable</label>
        <select id="responsable" name="responsable" ${puedeAsignar ? '' : 'disabled'}>
            <option value="">Sin asignar</option>
            <c:forEach var="u" items="${usuarios}">
                <option value="${u.id}" ${u.id == vResponsable ? 'selected' : ''}>
                    <c:out value="${u.nombre}"/> (<c:out value="${u.email}"/>)
                </option>
            </c:forEach>
        </select>
        <c:if test="${not puedeAsignar}">
            <p class="suave">Solo quien creó la tarea puede cambiar el responsable.</p>
        </c:if>

        <div class="fila-acciones separado">
            <button type="submit" class="btn">Guardar</button>
            <a class="btn btn-gris" href="${ctx}/tareas">Cancelar</a>
        </div>
    </form>
</main>
</body>
</html>