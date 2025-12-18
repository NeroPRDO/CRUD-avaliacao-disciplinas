<%-- 
    Document   : cursos-list
    Created on : 1 de out. de 2025, 09:12:44
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<c:set var="cPath" value="${pageContext.request.contextPath}" />

<main class="container">
  <h1>Cursos</h1>

  <div class="row" style="margin-bottom:12px;">
    <a class="btn" href="${cPath}/admin/cursos?acao=novo">Novo Curso</a>
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
          <th>Currículo</th>
          <th style="width:200px;">Ações</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="c" items="${cursos}">
          <tr>
            <td>${c.id}</td>
            <td>${c.nome}</td>
            <td>${c.curriculo}</td>
            <td>
              <a class="btn ghost" href="${cPath}/admin/cursos?acao=editar&id=${c.id}">Editar</a>
              <a class="btn danger"
                 href="${cPath}/admin/cursos?acao=excluir&id=${c.id}"
                 onclick="return confirm('Excluir o curso #${c.id} (${c.nome})?');">
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
