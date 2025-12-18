<%-- 
    Document   : usuarios-form
    Created on : 1 de out. de 2025, 09:12:25
    Author     : Pedro, Gabi
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="cPath" value="${pageContext.request.contextPath}" />
<c:set var="isEdit" value="${usuario != null && usuario.id != null}" />

<main class="container">
  <h1>${isEdit ? "Editar Usuário" : "Novo Usuário"}</h1>

  <div class="card">
    <form action="${cPath}/admin/usuarios" method="post" class="form">
      <input type="hidden" name="id" value="${usuario.id}" />

      <div class="row">
        <label>Nome</label>
        <input type="text" name="nome" value="${usuario.nome}" required />
      </div>

      <div class="row">
        <label>Email</label>
        <input type="email" name="email" value="${usuario.email}" required />
      </div>

      <div class="row">
        <label>Login</label>
        <input type="text" name="login" value="${usuario.login}" required />
      </div>

      <div class="row">
        <label>Senha (hash ou provisória)</label>
        <input type="text" name="senha" value="${usuario.senhaHash}" required />
      </div>

      <div class="row">
        <label class="checkbox">
          <input type="checkbox" name="ativo"
                 <c:if test="${usuario != null && (usuario.ativo == true)}">checked</c:if> />
          Ativo
        </label>
      </div>

      <fieldset class="row">
        <legend>Perfis</legend>
        <label class="checkbox">
          <input type="checkbox" name="perfilAluno"
                 <c:if test="${usuario.perfis != null && usuario.perfis.contains('ALUNO')}">checked</c:if> />
          Aluno
        </label>
        <label class="checkbox">
          <input type="checkbox" name="perfilProfessor"
                 <c:if test="${usuario.perfis != null && usuario.perfis.contains('PROFESSOR')}">checked</c:if> />
          Professor
        </label>
        <label class="checkbox">
          <input type="checkbox" name="perfilCoordenador"
                 <c:if test="${usuario.perfis != null && usuario.perfis.contains('COORDENADOR')}">checked</c:if> />
          Coordenador
        </label>
        <label class="checkbox">
          <input type="checkbox" name="perfilAdmin"
                 <c:if test="${usuario.perfis != null && usuario.perfis.contains('ADMIN')}">checked</c:if> />
          Admin
        </label>
      </fieldset>

      <div class="row">
        <button type="submit" class="btn">${isEdit ? "Salvar alterações" : "Criar usuário"}</button>
        <a class="btn ghost" href="${cPath}/admin/usuarios">Cancelar</a>
      </div>
    </form>
  </div>
      <%@ include file="_footer.jspf" %>
</main>
