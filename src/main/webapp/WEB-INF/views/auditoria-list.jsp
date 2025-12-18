<%-- 
  Document   : auditoria-list
  Created on : 3 de dez. de 2025, 11:59:19
  Author     : Pedro
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/_header.jspf" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<main class="container" style="max-width:1100px;margin:24px auto;">
  <h1>Logs de Auditoria</h1>

  <!-- Filtros -->
  <form action="${ctx}/admin/auditoria" method="get" class="card" style="margin:16px 0; gap:8px;">
    <div class="row" style="display:grid;grid-template-columns:repeat(6,1fr);gap:10px;">
      <label>De
        <input type="date" name="de" value="${param.de}"/>
      </label>
      <label>Até
        <input type="date" name="ate" value="${param.ate}"/>
      </label>
      <label>Usuário (ID)
        <input type="number" name="usuarioId" value="${param.usuarioId}"/>
      </label>
      <label>Ação
        <input type="text" name="acao" value="${fn:escapeXml(param.acao)}"/>
      </label>
      <label>Recurso
        <input type="text" name="tabela" value="${fn:escapeXml(param.tabela)}"/>
      </label>
      <label>Texto
        <input type="text" name="texto" value="${fn:escapeXml(param.texto)}"/>
      </label>
    </div>
    <div class="actions" style="margin-top:8px;display:flex;gap:8px;">
      <label>Limite
        <input type="number" name="limit" min="1" max="5000"
               value="${empty param.limit ? 500 : param.limit}" style="width:120px;"/>
      </label>
      <button class="btn" type="submit">Buscar</button>
      <a class="btn ghost" href="${ctx}/admin/auditoria">Limpar</a>
    </div>
  </form>

  <!-- Tabela -->
  <div class="card">
    <c:choose>
      <c:when test="${empty logs}">
        <div class="muted">Nenhum registro encontrado para os filtros informados.</div>
      </c:when>
      <c:otherwise>
        <table class="table">
          <thead>
            <tr>
              <th style="width:170px;">Data/Hora</th>
              <th style="width:220px;">Usuário</th>
              <th style="width:120px;">Ação</th>
              <th>Recurso</th>
              <th style="width:120px;">IP</th>
              <th>Detalhes</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="l" items="${logs}">
              <tr>
                <td><fmt:formatDate value="${l.momento}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>
                  <c:choose>
                    <c:when test="${not empty l.usuarioNome}">
                      <c:out value="${l.usuarioNome}"/> (ID <c:out value="${l.usuarioId}"/>)
                    </c:when>
                    <c:otherwise>
                      <span class="muted">— (ID <c:out value="${l.usuarioId}"/>)</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td><c:out value="${l.acao}"/></td>
                <td><c:out value="${l.tabela}"/></td>
                <td><c:out value="${l.ip}"/></td>
                <td style="white-space:pre-wrap;"><c:out value="${l.detalhes}"/></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </div>
</main>

<%@ include file="/WEB-INF/views/_footer.jspf" %>
