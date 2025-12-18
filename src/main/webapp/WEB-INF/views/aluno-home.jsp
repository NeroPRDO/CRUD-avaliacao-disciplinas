<%-- 
    Document   : aluno-home
    Created on : 1 de out. de 2025, 11:44:18
    Author     : Pedro, Gabi
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>AVA — Aluno</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>

  <main class="container" style="max-width:1000px;margin:24px auto;">
    <h2 style="margin-bottom:14px;">Minhas Avaliações</h2>

    <c:set var="ctx" value="${pageContext.request.contextPath}" />

    <c:choose>
      <c:when test="${empty formularios}">
        <div class="card">Nenhum formulário disponível no momento.</div>
      </c:when>
      <c:otherwise>
        <table class="table">
          <thead>
            <tr>
              <th style="min-width:360px;">Formulário</th>
              <th>Instruções</th>
              <th style="width:150px;">Ação</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="f" items="${formularios}">
              <tr>
                <td>
                  <strong><c:out value="${f.titulo}"/></strong>
                </td>
                <td>
                  <span class="muted"><c:out value="${f.instrucoes}"/></span>
                </td>
                <td>
                  <a class="btn" href="${ctx}/aluno/responder?id=${f.id}">Responder</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </c:otherwise>
    </c:choose>
  </main>

  <%@ include file="_footer.jspf" %>
</body>
</html>
