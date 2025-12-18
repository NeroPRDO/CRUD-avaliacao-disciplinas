<%-- 
    Document   : usuarios-list
    Created on : 1 de out. de 2025, 09:12:12
    Author     : Pedro, Gabi
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="cPath" value="${pageContext.request.contextPath}" />

<main class="container">
  <h1>Usuários</h1>

  <div class="row" style="margin-bottom:12px;">
    <a class="btn" href="${cPath}/admin/usuarios?acao=novo">Novo Usuário</a>
  </div>
  
  <head>
  <meta charset="UTF-8"/>
  <title>Disciplinas — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
  </head>
  
  <div class="card">
    <table class="table">
      <thead>
        <tr>
          <th style="width:70px;">ID</th>
          <th>Nome</th>
          <th>Email</th>
          <th>Login</th>
          <th>Perfis</th>
          <th style="width:200px;">Ações</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="u" items="${usuarios}">
          <tr>
            <td>${u.id}</td>
            <td>${u.nome}</td>
            <td>${u.email}</td>
            <td>${u.login}</td>
            <td>
              <c:choose>
                <c:when test="${empty u.perfis}">
                  <span class="badge">—</span>
                </c:when>
                <c:otherwise>
                  <c:forEach var="p" items="${u.perfis}" varStatus="st">
                    <span class="badge">${p}</span><c:if test="${!st.last}"> </c:if>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <a class="btn ghost" href="${cPath}/admin/usuarios?acao=editar&id=${u.id}">Editar</a>
              <a class="btn danger"
                 href="${cPath}/admin/usuarios?acao=excluir&id=${u.id}"
                 onclick="return confirm('Excluir o usuário #${u.id} (${u.login})?');">
                 Excluir
              </a>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
  <%@ include file="_footer.jspf" %>
</main>
