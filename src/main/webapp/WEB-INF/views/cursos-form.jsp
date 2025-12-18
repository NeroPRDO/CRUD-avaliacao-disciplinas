<%-- 
    Document   : cursos-form
    Created on : 1 de out. de 2025, 09:12:54
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="cPath" value="${pageContext.request.contextPath}" />
<c:set var="isEdit" value="${curso != null && curso.id != null}" />

<main class="container">
  <h1>${isEdit ? "Editar Curso" : "Novo Curso"}</h1>

  <div class="card">
    <form action="${cPath}/admin/cursos" method="post" class="form">
      <input type="hidden" name="id" value="${curso.id}" />

      <div class="row">
        <label>Nome</label>
        <input type="text" name="nome" value="${curso.nome}" required />
      </div>

      <div class="row">
        <label>Currículo</label>
        <input type="text" name="curriculo" value="${curso.curriculo}" />
      </div>

      <div class="row">
        <button type="submit" class="btn">${isEdit ? "Salvar alterações" : "Criar curso"}</button>
        <a class="btn ghost" href="${cPath}/admin/cursos">Cancelar</a>
      </div>
    </form>
  </div>
      <%@ include file="_footer.jspf" %>
</main>
