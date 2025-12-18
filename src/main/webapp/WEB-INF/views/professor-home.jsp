<%-- 
    Document   : professor-home
    Created on : 1 de out. de 2025, 13:45:53
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>AVA — Professor</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>

  <main class="container" style="max-width:1100px;margin:24px auto;">
    <h2 style="margin-bottom:14px;">Painel do Professor</h2>

    <c:if test="${empty forms}">
      <div class="card">Nenhum formulário cadastrado.</div>
    </c:if>

    <c:if test="${not empty forms}">
      <form method="get" action="${pageContext.request.contextPath}/professor" class="form inline" style="grid-template-columns:1fr auto;">
        <label>
          <span>Formulário</span>
          <select name="formId">
            <c:forEach var="f" items="${forms}">
              <option value="${f.id}" <c:if test="${f.id == formId}">selected</c:if>>
                ${f.titulo}
              </option>
            </c:forEach>
          </select>
        </label>
        <button class="btn" type="submit">Carregar</button>
      </form>

      <c:if test="${not empty questoes}">
        <section style="margin-top:16px;">
          <c:forEach var="q" items="${questoes}">
            <div class="card">
              <div class="space-between">
                <strong>Q${q.ordem}.</strong>
                <span class="muted">
                  <c:choose>
                    <c:when test="${q.tipo.name() == 'ABERTA'}">Aberta</c:when>
                    <c:when test="${q.tipo.name() == 'UNICA'}">Única</c:when>
                    <c:otherwise>Múltipla</c:otherwise>
                  </c:choose>
                  <c:if test="${q.obrigatoria}"> • obrigatória</c:if>
                </span>
              </div>
              <div style="margin:6px 0 10px 0;">${q.enunciado}</div>

              <c:choose>
                <c:when test="${q.tipo.name() == 'ABERTA'}">
                  <c:set var="texts" value="${respostasAbertas[q.id]}"/>
                  <c:if test="${empty texts}">
                    <div class="pill pill-warn">Nenhuma resposta aberta ainda.</div>
                  </c:if>
                  <c:if test="${not empty texts}">
                    <table class="table">
                      <thead>
                        <tr>
                          <th style="width:60px;">#</th>
                          <th>Resposta</th>
                        </tr>
                      </thead>
                      <tbody>
                        <c:forEach var="t" items="${texts}" varStatus="s">
                          <tr>
                            <td>${s.index + 1}</td>
                            <td><c:out value="${t}"/></td>
                          </tr>
                        </c:forEach>
                      </tbody>
                    </table>
                  </c:if>
                </c:when>
                <c:otherwise>
                  <table class="table">
                    <thead>
                      <tr>
                        <th>Alternativa</th>
                        <th style="width:120px;">Votos</th>
                        <th style="width:140px;">Percentual</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="item" items="${distQuestoes[q.id]}">
                        <tr>
                          <td>${item.alternativa}</td>
                          <td>${item.votos}</td>
                          <td><fmt:formatNumber value="${item.percentual}" type="number" maxFractionDigits="1"/>%</td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </c:otherwise>
              </c:choose>
            </div>
          </c:forEach>
        </section>
      </c:if>
    </c:if>
  </main>

  <%@ include file="_footer.jspf" %>

</body>
</html>
