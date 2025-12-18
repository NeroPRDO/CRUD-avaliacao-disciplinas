<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>Disciplinas — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>
  <main class="container">
    <div class="row space-between">
      <h1>Disciplinas</h1>
      <a class="btn" href="${ctx}/admin/disciplinas?acao=novo">Nova Disciplina</a>
    </div>

    <div class="card">
      <table class="table">
        <thead>
        <tr><th>ID</th><th>Curso ID</th><th>Nome</th><th>Ações</th></tr>
        </thead>
        <tbody>
        <c:forEach var="d" items="${disciplinas}">
          <tr>
            <td>${d.id}</td>
            <td>${d.cursoId}</td>
            <td><c:out value="${d.nome}"/></td>
            <td class="actions">
              <a class="btn ghost" href="${ctx}/admin/disciplinas?acao=editar&id=${d.id}">Editar</a>
              <a class="btn danger" href="${ctx}/admin/disciplinas?acao=excluir&id=${d.id}"
                 onclick="return confirm('Excluir disciplina #${d.id}?');">Excluir</a>
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
