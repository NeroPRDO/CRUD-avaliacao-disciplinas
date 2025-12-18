<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>Turmas — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>
  <main class="container">
    <div class="row space-between">
      <h1>Turmas</h1>
      <a class="btn" href="${ctx}/admin/turmas?acao=novo">Nova Turma</a>
    </div>

    <div class="card">
      <table class="table">
        <thead>
        <tr><th>ID</th><th>Disciplina ID</th><th>Código</th><th>Ano/Semestre</th><th style="width:360px;">Ações</th></tr>
        </thead>
        <tbody>
        <c:forEach var="t" items="${turmas}">
          <tr>
            <td>${t.id}</td>
            <td>${t.disciplinaId}</td>
            <td><c:out value="${t.codigo}"/></td>
            <td><c:out value="${t.anoSemestre}"/></td>
            <td class="actions" style="display:flex;gap:8px;flex-wrap:wrap;">
              <a class="btn ghost" href="${ctx}/admin/turmas?acao=editar&id=${t.id}">Editar</a>
              <a class="btn" href="${ctx}/admin/turmas/alunos?turmaId=${t.id}">Alunos</a>
              <a class="btn" href="${ctx}/admin/turmas/professores?turmaId=${t.id}">Professores</a>
              <a class="btn danger" href="${ctx}/admin/turmas?acao=excluir&id=${t.id}"
                 onclick="return confirm('Excluir turma #${t.id}?');">Excluir</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <%@ include file="/WEB-INF/views/_footer.jspf" %>
  </main>
</body>
</html>
