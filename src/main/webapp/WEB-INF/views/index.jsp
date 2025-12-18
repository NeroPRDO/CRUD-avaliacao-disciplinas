<%-- 
    Document   : index
    Created on : 1 de out. de 2025, 09:12:06
    Author     : Pedro, Gabi
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="user" value="${sessionScope.usuarioLogado}" />

<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>AVA — Protótipo de Telas</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>

  <main class="container" style="max-width:1100px;margin:32px auto;">
    <h1 style="color:#e2e8f0;margin-bottom:18px;">AVA — Protótipo de Telas</h1>

    <!-- Visitante (não autenticado) -->
    <c:if test="${empty user}">
      <div class="card">
        <p>Você não está autenticado.</p>
        <p style="margin-top:8px;">
          <a class="btn" href="${ctx}/login">Entrar</a>
        </p>
      </div>

      <section style="margin-top:18px;">
        <div class="pill">Demonstração</div>
        <ul style="margin-top:10px;color:#94a3b8;">
          <li>Login <strong>aluno</strong>: <code>aluno/aluno</code></li>
          <li>Login <strong>professor</strong>: <code>prof/prof</code></li>
          <li>Login <strong>admin</strong>: <code>admin/admin</code></li>
        </ul>
      </section>
    </c:if>

    <!-- Usuário autenticado: atalho por perfil -->
    <c:if test="${not empty user}">
      <section class="grid" style="display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:14px;">
        <c:if test="${user.admin or user.coordenador}">
          <div class="card">
            <h3 style="margin-bottom:10px;">Painel Administrativo</h3>
            <div class="stack">
              <a class="btn" href="${ctx}/admin/usuarios">Gerenciar Usuários</a>
              <a class="btn" href="${ctx}/admin/cursos">Gerenciar Cursos</a>
              <a class="btn" href="${ctx}/admin/disciplinas">Gerenciar Disciplinas</a>
              <a class="btn" href="${ctx}/admin/turmas">Gerenciar Turmas</a>
              <a class="btn" href="${ctx}/admin/formularios">Gerenciar Formulários</a>
            </div>
          </div>
        </c:if>

        <c:if test="${user.professor}">
          <div class="card">
            <h3 style="margin-bottom:10px;">Área do Professor</h3>
            <p style="color:#94a3b8;margin-bottom:10px;">Resultados agregados e estatísticas.</p>
            <a class="btn" href="${ctx}/professor">Ir para meu painel</a>
          </div>
        </c:if>

        <c:if test="${user.aluno}">
          <div class="card">
            <h3 style="margin-bottom:10px;">Área do Aluno</h3>
            <p style="color:#94a3b8;margin-bottom:10px;">Responder avaliações disponíveis.</p>
            <a class="btn" href="${ctx}/aluno">Ver minhas avaliações</a>
          </div>
        </c:if>
      </section>
    </c:if>
  </main>

<%@ include file="_footer.jspf" %>
</body>
</html>
