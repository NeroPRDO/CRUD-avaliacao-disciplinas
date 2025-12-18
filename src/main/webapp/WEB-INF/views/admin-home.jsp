<%-- 
    Document   : admin-home
    Created on : 25 de nov. de 2025, 09:41:02
    Author     : Pedro
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>AVA — Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css"/>
</head>
<body>
  <jsp:include page="/WEB-INF/views/_header.jspf"/>

  <main class="container" style="max-width:1100px;margin:24px auto;">
    <h2 style="margin-bottom:14px;">Painel Administrativo</h2>
    <p class="muted">Atalhos para o gerenciamento do contexto avaliativo.</p>

    <section class="grid" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:14px;margin-top:16px;">
      <div class="card">
        <h3>Usuários</h3>
        <p class="muted">Alunos, professores, coordenação e administradores.</p>
        <a class="btn" href="${pageContext.request.contextPath}/admin/usuarios">Gerenciar Usuários</a>
      </div>

      <div class="card">
        <h3>Cursos</h3>
        <p class="muted">Crie e edite cursos e seus currículos.</p>
        <a class="btn" href="${pageContext.request.contextPath}/admin/cursos">Gerenciar Cursos</a>
      </div>

      <div class="card">
        <h3>Disciplinas</h3>
        <p class="muted">Unidades curriculares por curso.</p>
        <a class="btn" href="${pageContext.request.contextPath}/admin/disciplinas">Gerenciar Disciplinas</a>
      </div>

      <div class="card">
        <h3>Turmas</h3>
        <p class="muted">Vincule professores e alunos às turmas.</p>
        <a class="btn" href="${pageContext.request.contextPath}/admin/turmas">Gerenciar Turmas</a>
      </div>

      <div class="card">
        <h3>Formulários</h3>
        <p class="muted">Crie formulários e defina público-alvo/anonimato.</p>
        <a class="btn" href="${pageContext.request.contextPath}/admin/formularios">Gerenciar Formulários</a>
      </div>
    </section>
  </main>

  <%@ include file="_footer.jspf" %>
</body>
</html>
