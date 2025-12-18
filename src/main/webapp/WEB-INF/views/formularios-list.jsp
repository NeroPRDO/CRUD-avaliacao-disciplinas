<%-- 
    Document   : formularios-list
    Created on : 1 de out. de 2025, 09:13:05
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>Formulários — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>
  <main class="container">
    <div class="row space-between">
      <h1>Formulários</h1>
      <a class="btn" href="${ctx}/admin/formularios?acao=novo">Novo Formulário</a>
    </div>

    <div class="card">
      <table class="table">
        <thead>
        <tr><th>ID</th><th>Título</th><th>Anônimo</th><th>Ações</th></tr>
        </thead>
        <tbody>
        <c:forEach var="f" items="${formularios}">
          <tr>
            <td>${f.id}</td>
            <td><c:out value="${f.titulo}"/></td>
            <td><c:out value="${f.anonimo}"/></td>
            <td class="actions">
              <a class="btn ghost" href="${ctx}/admin/formularios?acao=editar&id=${f.id}">Editar</a>
              <a class="btn danger" href="${ctx}/admin/formularios?acao=excluir&id=${f.id}"
                 onclick="return confirm('Excluir formulário #${f.id}?');">Excluir</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <%@ include file="_footer.jspf" %>
  </main>
</body>
</html>
