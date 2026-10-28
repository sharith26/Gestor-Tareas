<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis tareas - Gestor de tareas</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body class="app">
<div class="contenedor">

    <div class="barra">
        <h1>Tareas de <c:out value="${sessionScope.usuarioNombre}"/></h1>
        <div class="fila-acciones">
            <a class="btn" href="${ctx}/tareas/nueva">Nueva tarea</a>
            <a class="btn btn-gris" href="${ctx}/categorias">Categorías</a>
                        <form method="post" action="${ctx}/logout">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                <button type="submit" class="btn btn-gris">Cerrar sesión</button>
            </form>
        </div>
    </div>

    <c:if test="${param.msg == 'creada'}"><p class="ok">Tarea creada.</p></c:if>
    <c:if test="${param.msg == 'actualizada'}"><p class="ok">Tarea actualizada.</p></c:if>
    <c:if test="${param.msg == 'eliminada'}"><p class="ok">Tarea eliminada.</p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>

    <form method="get" action="${ctx}/tareas" class="panel filtros">
        <div>
            <label for="q">Buscar por título</label>
            <input type="search" id="q" name="q" value="<c:out value='${param.q}'/>">
        </div>
        <div>
            <label for="estado">Estado</label>
            <select id="estado" name="estado">
                <option value="">Todos</option>
                <c:forEach var="e" items="${estados}">
                    <option value="${e.name()}" ${param.estado == e.name() ? 'selected' : ''}>
                        <c:out value="${e.etiqueta}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label for="categoria">Categoría</label>
            <select id="categoria" name="categoria">
                <option value="">Todas</option>
                <c:forEach var="c" items="${categorias}">
                    <option value="${c.id}" ${param.categoria == c.id ? 'selected' : ''}>
                        <c:out value="${c.nombre}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label for="responsable">Responsable</label>
            <select id="responsable" name="responsable">
                <option value="">Todos</option>
                <c:forEach var="u" items="${usuarios}">
                    <option value="${u.id}" ${param.responsable == u.id ? 'selected' : ''}>
                        <c:out value="${u.nombre}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label><input type="checkbox" name="vencidas" value="1" ${param.vencidas == '1' ? 'checked' : ''}> Solo vencidas</label>
        </div>
        <div class="fila-acciones">
            <button type="submit" class="btn">Filtrar</button>
            <a class="btn btn-gris" href="${ctx}/tareas">Limpiar</a>
        </div>
    </form>

    <div class="panel tabla-scroll">
        <c:choose>
            <c:when test="${empty tareas}">
                <p>No hay tareas para mostrar.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Título</th>
                        <th>Estado</th>
                        <th>Prioridad</th>
                        <th>Categoría</th>
                        <th>Responsable</th>
                        <th>Fecha límite</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="t" items="${tareas}">
                        <tr>
                            <td>
                                <strong><c:out value="${t.titulo}"/></strong>
                                <c:if test="${not empty t.descripcion}">
                                    <br><small class="suave"><c:out value="${t.descripcion}"/></small>
                                </c:if>
                            </td>
                            <td><span class="etiqueta estado-${t.estado}"><c:out value="${t.estado.etiqueta}"/></span></td>
                            <td><span class="etiqueta prio-${t.prioridad}"><c:out value="${t.prioridad.etiqueta}"/></span></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty t.categoriaNombre}"><c:out value="${t.categoriaNombre}"/></c:when>
                                    <c:otherwise><span class="suave">Sin categoría</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty t.responsableNombre}"><c:out value="${t.responsableNombre}"/></c:when>
                                    <c:otherwise><span class="suave">Sin asignar</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty t.fechaLimite}">
                                        <span class="${t.vencida ? 'vencida' : ''}">
                                            <c:out value="${t.fechaLimite}"/><c:if test="${t.vencida}"> (vencida)</c:if>
                                        </span>
                                    </c:when>
                                    <c:otherwise><span class="suave">-</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="fila-acciones">
                                    <a class="btn btn-sm" href="${ctx}/tareas/${t.id}/editar">Editar</a>
                                    <c:if test="${t.creadorId == sessionScope.usuarioId}">
                                        <form method="post" action="${ctx}/tareas/${t.id}/eliminar"
                                              onsubmit="return confirm('¿Eliminar esta tarea?');">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                                            <button type="submit" class="btn btn-sm btn-rojo">Eliminar</button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>