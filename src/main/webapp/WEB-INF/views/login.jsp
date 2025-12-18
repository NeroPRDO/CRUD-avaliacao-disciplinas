<%-- 
    Document   : login
    Created on : 1 de out. de 2025, 11:43:55
    Author     : Pedro, Gabi
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>Entrar — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>

  <main class="container" style="max-width:900px;margin:28px auto;">
    <h1 style="color:#e2e8f0;margin-bottom:18px;">Entrar</h1>

    <c:if test="${not empty erro}">
      <div class="card" style="background:#3f1d1d;color:#fecaca;">${erro}</div>
    </c:if>

    <form method="post" action="${ctx}/login" class="card" style="max-width:560px;">
      <div class="row">
        <label>Login</label>
        <input type="text" name="login" value="<c:out value='${loginPreenchido}'/>" required/>
      </div>
      <div class="row">
        <label>Senha</label>
        <input type="password" name="senha" required/>
      </div>
      <div class="actions">
        <button class="btn" type="submit">Entrar</button>
        <a class="btn ghost" href="${ctx}/">Cancelar</a>
      </div>
      <p style="color:#94a3b8;margin-top:12px;font-size:12px;">
        Dicas: admin/admin (ADMIN), prof/prof (PROFESSOR), aluno/aluno (ALUNO)
      </p>
    </form>
      <%@ include file="_footer.jspf" %>
  </main>
</body>
</html>
