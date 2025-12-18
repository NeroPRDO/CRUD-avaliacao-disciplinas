<%-- 
    Document   : turmas-professores-list
    Created on : 19 de nov. de 2025, 10:19:14
    Author     : Pedro
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<h2>Atribuir Professores à Turma</h2>

<div class="card">
  <div class="row space-between">
    <div>
      <strong>Turma:</strong> ${turma.codigo}
      &nbsp;•&nbsp;<span class="muted">${turma.disciplina}</span>
      &nbsp;•&nbsp;<span class="muted">${turma.anoSemestre}</span>
    </div>
    <a class="btn ghost" href="${pageContext.request.contextPath}/admin/turmas">Voltar</a>
  </div>
</div>

<div class="grid" style="display:grid;grid-template-columns:1fr 1fr;gap:16px;">
  <section>
    <h3>Professores vinculados</h3>
    <c:if test="${empty vinculados}">
      <div class="card">Nenhum professor vinculado a esta turma.</div>
    </c:if>
    <c:if test="${not empty vinculados}">
      <table class="table">
        <thead><tr><th>Nome</th><th>Login</th><th>Registro</th><th style="width:140px;"></th></tr></thead>
        <tbody>
        <c:forEach var="p" items="${vinculados}">
          <tr>
            <td>${p.nome}</td>
            <td class="muted">${p.login}</td>
            <td class="muted">${p.registro}</td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/turmas/professores" onsubmit="return confirm('Remover este professor da turma?')">
                <input type="hidden" name="action" value="remove"/>
                <input type="hidden" name="turmaId" value="${turma.id}"/>
                <input type="hidden" name="professorId" value="${p.id}"/>
                <button class="btn danger" type="submit">Remover</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:if>
  </section>

  <section>
    <h3>Adicionar professores</h3>
    <c:if test="${empty disponiveis}">
      <div class="card">Não há professores disponíveis (todos já estão vinculados ou inativos).</div>
    </c:if>
    <c:if test="${not empty disponiveis}">
      <form method="post" action="${pageContext.request.contextPath}/admin/turmas/professores">
        <input type="hidden" name="action" value="add"/>
        <input type="hidden" name="turmaId" value="${turma.id}"/>

        <div class="card">
          <p class="muted">Selecione um ou mais professores:</p>
          <div style="max-height:340px;overflow:auto;padding:8px 0;">
            <c:forEach var="p" items="${disponiveis}">
              <label style="display:block;margin:6px 0;">
                <input type="checkbox" name="professorId" value="${p.id}"/>
                <strong>${p.nome}</strong>
                <span class="muted">(${p.login} · ${p.registro} · ${p.departamento})</span>
              </label>
            </c:forEach>
          </div>
        </div>

        <div style="margin-top:8px;">
          <button class="btn" type="submit">Adicionar selecionados</button>
        </div>
      </form>
    </c:if>
  </section>
</div>

<%@ include file="_footer.jspf" %>
