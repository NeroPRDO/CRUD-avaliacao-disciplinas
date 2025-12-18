<%-- 
    Document   : turmas-alunos-list
    Created on : 19 de nov. de 2025, 10:20:52
    Author     : Pedro
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="_header.jspf" %>

<h2>Matricular Alunos na Turma</h2>

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
    <h3>Matriculados</h3>
    <c:if test="${empty matriculados}">
      <div class="card">Nenhum aluno matriculado nesta turma.</div>
    </c:if>
    <c:if test="${not empty matriculados}">
      <table class="table">
        <thead><tr><th>Nome</th><th>Login</th><th>Matrícula</th><th style="width:140px;"></th></tr></thead>
        <tbody>
        <c:forEach var="a" items="${matriculados}">
          <tr>
            <td>${a.nome}</td>
            <td class="muted">${a.login}</td>
            <td class="muted">${a.matricula}</td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/turmas/alunos" onsubmit="return confirm('Remover este aluno da turma?')">
                <input type="hidden" name="action" value="remove"/>
                <input type="hidden" name="turmaId" value="${turma.id}"/>
                <input type="hidden" name="alunoId" value="${a.id}"/>
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
    <h3>Adicionar alunos</h3>
    <c:if test="${empty disponiveis}">
      <div class="card">Não há alunos disponíveis (todos já estão matriculados ou inativos).</div>
    </c:if>
    <c:if test="${not empty disponiveis}">
      <form method="post" action="${pageContext.request.contextPath}/admin/turmas/alunos">
        <input type="hidden" name="action" value="add"/>
        <input type="hidden" name="turmaId" value="${turma.id}"/>

        <div class="card">
          <p class="muted">Selecione um ou mais alunos:</p>
          <div style="max-height:340px;overflow:auto;padding:8px 0;">
            <c:forEach var="a" items="${disponiveis}">
              <label style="display:block;margin:6px 0;">
                <input type="checkbox" name="alunoId" value="${a.id}"/>
                <strong>${a.nome}</strong>
                <span class="muted">(${a.login} · ${a.matricula})</span>
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
