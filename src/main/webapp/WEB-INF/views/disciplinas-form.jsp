<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>${empty disciplina.id ? 'Nova' : 'Editar'} Disciplina — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>
  <main class="container">
    <h1>${empty disciplina.id ? 'Nova' : 'Editar'} Disciplina</h1>

    <form method="post" action="${ctx}/admin/disciplinas" class="card" style="max-width:640px;">
      <input type="hidden" name="id" value="${disciplina.id}"/>

      <div class="row"><label>Curso ID</label>
        <input type="number" name="cursoId" value="${disciplina.cursoId}" required/></div>

      <div class="row"><label>Nome</label>
        <input type="text" name="nome" value="${disciplina.nome}" required/></div>

      <div class="actions">
        <button class="btn" type="submit">Salvar</button>
        <a class="btn ghost" href="${ctx}/admin/disciplinas">Cancelar</a>
      </div>
    </form>
          <%@ include file="_footer.jspf" %>
  </main>
</body>
</html>
