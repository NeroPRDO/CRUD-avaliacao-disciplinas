<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>${empty turma.id ? 'Nova' : 'Editar'} Turma — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>
  <main class="container">
    <h1>${empty turma.id ? 'Nova' : 'Editar'} Turma</h1>

    <form method="post" action="${ctx}/admin/turmas" class="card" style="max-width:640px;">
      <input type="hidden" name="id" value="${turma.id}"/>

      <div class="row"><label>Disciplina ID</label>
        <input type="number" name="disciplinaId" value="${turma.disciplinaId}" required/></div>

      <div class="row"><label>Código</label>
        <input type="text" name="codigo" value="${turma.codigo}" required/></div>

      <div class="row"><label>Ano/Semestre</label>
        <input type="text" name="anoSemestre" value="${turma.anoSemestre}" required/></div>

      <div class="actions">
        <button class="btn" type="submit">Salvar</button>
        <a class="btn ghost" href="${ctx}/admin/turmas">Cancelar</a>
      </div>
    </form>
      <%@ include file="_footer.jspf" %>
  </main>
</body>
</html>
