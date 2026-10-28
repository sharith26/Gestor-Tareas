<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- [NUEVO] Conteos para el resumen --%>
<c:set var="nPend" value="0"/>
<c:set var="nProg" value="0"/>
<c:set var="nTerm" value="0"/>
<c:set var="nVenc" value="0"/>
<c:forEach var="t" items="${tareas}">
    <c:choose>
        <c:when test="${t.estado.name() == 'PENDIENTE'}"><c:set var="nPend" value="${nPend + 1}"/></c:when>
        <c:when test="${t.estado.name() == 'EN_PROGRESO'}"><c:set var="nProg" value="${nProg + 1}"/></c:when>
        <c:when test="${t.estado.name() == 'TERMINADA'}"><c:set var="nTerm" value="${nTerm + 1}"/></c:when>
    </c:choose>
    <c:if test="${t.vencida}"><c:set var="nVenc" value="${nVenc + 1}"/></c:if>
</c:forEach>
<c:set var="nTotal" value="${nPend + nProg + nTerm}"/>
<fmt:formatNumber var="pct" value="${nTotal > 0 ? nTerm * 100 / nTotal : 0}" maxFractionDigits="0" groupingUsed="false"/>
<c:set var="hayFiltros" value="${not empty param.q or not empty param.estado or not empty param.categoria or not empty param.responsable or param.vencidas == '1'}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis tareas - Gestor de tareas</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
    <%-- [NUEVO] Estilos del resumen (van aquí para no depender de estilos.css) --%>
    <style>
        .resumen-panel { padding: 22px; }
        .resumen { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
        .dato { display: flex; align-items: center; gap: 14px; padding: 14px 16px; border-radius: 14px; }
        .dato-ico { display: grid; place-items: center; flex: none; width: 42px; height: 42px;
                    border-radius: 12px; background: rgba(255, 255, 255, .75); }
        .dato .num { display: block; font-size: 1.7rem; font-weight: 800; line-height: 1.1; }
        .dato .txt { display: block; font-size: .85rem; font-weight: 600; }
        .dato.pendiente     { background: #fff3da; color: #8a5a00; }
        .dato.progreso-dato { background: #e3ecff; color: #234bb5; }
        .dato.terminada     { background: #e6f5ea; color: #1f6b35; }
        .dato.vencida-dato  { background: #fdecec; color: #a12626; }
        .avance { margin-top: 20px; padding-top: 18px; border-top: 1px solid #e6e9f1; }
        .avance-texto { display: flex; justify-content: space-between; align-items: baseline;
                        margin-bottom: 10px; font-size: .92rem; }
        .barra-avance { height: 10px; overflow: hidden; background: #e9edf5; border-radius: 999px; }
        .barra-avance-relleno { height: 100%; background: linear-gradient(90deg, #3b6cf6, #5b8cff);
                                border-radius: 999px; }
        @media (max-width: 860px) { .resumen { grid-template-columns: repeat(2, 1fr); } }
        @media (max-width: 480px) { .resumen { grid-template-columns: 1fr; } }
    </style>
</head>
<body class="app">
<div class="contenedor">

    <div class="barra">
        <h1>Tareas de <c:out value="${sessionScope.usuarioNombre}"/></h1>
        <div class="fila-acciones">
            <a class="btn" href="${ctx}/tareas/nueva">
                <svg class="ico" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>
                Nueva tarea
            </a>
            <a class="btn btn-gris" href="${ctx}/categorias">
                <svg class="ico" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Z"/></svg>
                Categorías
            </a>
                        <form method="post" action="${ctx}/logout">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                <button type="submit" class="btn btn-gris">
                    <svg class="ico" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>
                    Cerrar sesión
                </button>
            </form>
        </div>
    </div>

    <c:if test="${param.msg == 'creada'}"><p class="ok">Tarea creada.</p></c:if>
    <c:if test="${param.msg == 'actualizada'}"><p class="ok">Tarea actualizada.</p></c:if>
    <c:if test="${param.msg == 'eliminada'}"><p class="ok">Tarea eliminada.</p></c:if>
    <c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>

    <%-- [NUEVO] Resumen y barra de avance --%>
    <c:if test="${nTotal > 0}">
        <div class="panel resumen-panel">
            <div class="resumen">
                <div class="dato pendiente">
                    <span class="dato-ico"><svg class="ico" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></span>
                    <div><span class="num">${nPend}</span><span class="txt">Pendientes</span></div>
                </div>
                <div class="dato progreso-dato">
                    <span class="dato-ico"><svg class="ico" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 12a9 9 0 1 1-3-6.7"/><path d="M21 4v5h-5"/></svg></span>
                    <div><span class="num">${nProg}</span><span class="txt">En progreso</span></div>
                </div>
                <div class="dato terminada">
                    <span class="dato-ico"><svg class="ico" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.5 2.5L16 9.5"/></svg></span>
                    <div><span class="num">${nTerm}</span><span class="txt">Terminadas</span></div>
                </div>
                <div class="dato vencida-dato">
                    <span class="dato-ico"><svg class="ico" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 3l10 18H2z"/><path d="M12 10v4M12 17.5v.01"/></svg></span>
                    <div><span class="num">${nVenc}</span><span class="txt">Vencidas</span></div>
                </div>
            </div>
            <div class="avance">
                <div class="avance-texto"><strong>Avance</strong><span class="suave">${pct}% completado</span></div>
                <div class="barra-avance" role="progressbar" aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100">
                    <div class="barra-avance-relleno" style="width:${pct}%"></div>
                </div>
            </div>
        </div>
    </c:if>

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
                <%-- [NUEVO] Estado vacío (reemplaza el <p>No hay tareas para mostrar.</p>) --%>
                <div class="vacio">
                    <svg class="vacio-ico" width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#3b6cf6" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 4h6v3H9z"/><path d="M9 12h6M9 16h4"/></svg>
                    <c:choose>
                        <c:when test="${hayFiltros}">
                            <h2>No encontramos tareas con esos filtros</h2>
                            <p class="suave">Prueba con otra búsqueda o quita los filtros.</p>
                            <a class="btn btn-gris" href="${ctx}/tareas">Limpiar filtros</a>
                        </c:when>
                        <c:otherwise>
                            <h2>Aún no tienes tareas</h2>
                            <p class="suave">Crea la primera para empezar a organizarte.</p>
                            <a class="btn" href="${ctx}/tareas/nueva">Crear tarea</a>
                        </c:otherwise>
                    </c:choose>
                </div>
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
                                    <a class="btn btn-sm" href="${ctx}/tareas/${t.id}/editar">
                                        <svg class="ico" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/></svg>
                                        Editar
                                    </a>
                                    <c:if test="${t.creadorId == sessionScope.usuarioId}">
                                        <form method="post" action="${ctx}/tareas/${t.id}/eliminar"
                                              onsubmit="return confirm('¿Eliminar esta tarea?');">
        <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                                            <button type="submit" class="btn btn-sm btn-rojo">
                                                <svg class="ico" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/></svg>
                                                Eliminar
                                            </button>
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
