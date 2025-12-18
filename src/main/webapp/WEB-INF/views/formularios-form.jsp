<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8"/>
  <title>Formulário — AVA</title>
  <link rel="stylesheet" href="${ctx}/assets/css/main.css"/>
  <style>.cols{display:grid;grid-template-columns:1fr 1fr;gap:16px}</style>
</head>
<body>
  <%@ include file="/WEB-INF/views/_header.jspf" %>

  <main class="container">
    <c:set var="f" value="${formulario}"/>

    <h1>Dados do Formulário</h1>
    <form action="${ctx}/admin/formularios" method="post" class="card" style="max-width:980px;">
      <input type="hidden" name="action" value="saveForm"/>
      <input type="hidden" name="id" value="${f.id}"/>

      <div class="row"><label>Título</label>
        <input type="text" name="titulo" value="${f.titulo}" required/></div>

      <div class="row"><label>Instruções</label>
        <textarea name="instrucoes" rows="3">${f.instrucoes}</textarea></div>

      <div class="row">
        <label><input type="checkbox" name="anonimo" <c:if test="${f.anonimo}">checked</c:if> />
          Respostas anônimas</label>
      </div>

      <!-- NOVO: Vincular o formulário às turmas -->
      <div class="row">
        <h3>Vincular às turmas</h3>
        <div class="card">
          <c:forEach var="t" items="${turmas}">
            <label style="display:block;margin:4px 0;">
              <input type="checkbox" name="turmaIds" value="${t.id}"
                     <c:if test="${turmasVincMap[t.id]}">checked</c:if> />
              ${t.codigo} — ${t.anoSemestre}
            </label>
          </c:forEach>
          <c:if test="${empty turmas}">
            <div class="muted">Nenhuma turma cadastrada.</div>
          </c:if>
        </div>
      </div>

      <div class="actions" style="gap:8px;">
        <button class="btn" type="submit">Salvar</button>
        <c:if test="${f.id != null}">
          <a class="btn danger" href="${ctx}/admin/formularios?acao=excluir&id=${f.id}"
             onclick="return confirm('Excluir este formulário? Esta ação é irreversível.');">Excluir</a>
        </c:if>
        <a class="btn ghost" href="${ctx}/admin/formularios">Voltar</a>
      </div>
    </form>

    <c:if test="${f.id != null}">
      <hr/>
      <h2>Adicionar Questão</h2>
      <form action="${ctx}/admin/formularios" method="post" class="card" style="max-width:980px;">
        <input type="hidden" name="action" value="addQuestao"/>
        <input type="hidden" name="formularioId" value="${f.id}"/>

        <div class="cols">
          <div class="row"><label>Enunciado</label>
            <input type="text" name="enunciado" required/></div>

          <div class="row"><label>Tipo</label>
            <select name="tipo">
              <option value="ABERTA">Aberta</option>
              <option value="UNICA">Múltipla (resposta única)</option>
              <option value="MULTIPLA">Múltipla (várias respostas)</option>
            </select>
          </div>
        </div>

        <div class="cols">
          <div class="row"><label>Ordem</label>
            <input type="number" name="ordem" value="0"/></div>

          <div class="row">
            <label><input type="checkbox" name="obrigatoria"/> Obrigatória</label>
          </div>
        </div>

        <div class="actions">
          <button class="btn" type="submit">Adicionar Questão</button>
        </div>
      </form>

      <hr/>
      <h2>Questões do Formulário</h2>
      <c:forEach var="q" items="${questoes}">
        <div class="card">
          <!-- EDITAR QUESTÃO -->
          <form action="${ctx}/admin/formularios" method="post" class="form" style="gap:10px;">
            <input type="hidden" name="action" value="editQuestao"/>
            <input type="hidden" name="formularioId" value="${f.id}"/>
            <input type="hidden" name="questaoId" value="${q.id}"/>

            <div class="row"><label>Enunciado</label>
              <input type="text" name="enunciado" value="${q.enunciado}" required/></div>

            <div class="row"><label>Tipo</label>
              <select name="tipo">
                <option value="ABERTA"  <c:if test="${q.tipo == 'ABERTA'}">selected</c:if>>Aberta</option>
                <option value="UNICA"   <c:if test="${q.tipo == 'UNICA'}">selected</c:if>>Múltipla (resposta única)</option>
                <option value="MULTIPLA"<c:if test="${q.tipo == 'MULTIPLA'}">selected</c:if>>Múltipla (várias respostas)</option>
              </select>
            </div>

            <div class="row"><label>Ordem</label>
              <input type="number" name="ordem" value="${q.ordem}"/></div>

            <div class="row">
              <label><input type="checkbox" name="obrigatoria" <c:if test="${q.obrigatoria}">checked</c:if> /> Obrigatória</label>
            </div>

            <div class="actions" style="gap:8px;">
              <button class="btn" type="submit">Salvar Questão</button>

              <!-- EXCLUIR QUESTÃO -->
              <form action="${ctx}/admin/formularios" method="post" onsubmit="return confirm('Excluir esta questão? (Não permitido se já houver respostas)')">
                <input type="hidden" name="action" value="deleteQuestao"/>
                <input type="hidden" name="formularioId" value="${f.id}"/>
                <input type="hidden" name="questaoId" value="${q.id}"/>
                <button class="btn danger" type="submit">Excluir Questão</button>
              </form>
            </div>
          </form>

          <!-- ALTERNATIVAS -->
          <c:if test="${q.tipo ne 'ABERTA'}">
            <div class="muted" style="margin-top:8px;">Alternativas</div>

            <!-- adicionar alternativa -->
            <form action="${ctx}/admin/formularios" method="post" class="form inline" style="margin-top:8px;">
              <input type="hidden" name="action" value="addAlternativa"/>
              <input type="hidden" name="formularioId" value="${f.id}"/>
              <input type="hidden" name="questaoId" value="${q.id}"/>

              <label>Texto <input type="text" name="texto" required/></label>
              <label>Peso <input type="number" name="peso" value="1"/></label>
              <label>Ordem <input type="number" name="ordem" value="0"/></label>

              <button class="btn" type="submit">Adicionar Alternativa</button>
            </form>

            <!-- listar/editar/remover alternativas já existentes -->
            <c:forEach var="a" items="${alternativasPorQuestao[q.id]}">
              <form action="${ctx}/admin/formularios" method="post" class="card" style="margin:8px 0;">
                <input type="hidden" name="action" value="editAlternativa"/>
                <input type="hidden" name="formularioId" value="${f.id}"/>
                <input type="hidden" name="alternativaId" value="${a.id}"/>

                <div class="row"><label>Texto</label>
                  <input type="text" name="texto" value="${a.texto}" required/></div>

                <div class="row"><label>Peso</label>
                  <input type="number" name="peso" value="${a.peso}"/></div>

                <div class="row"><label>Ordem</label>
                  <input type="number" name="ordem" value="${a.ordem}"/></div>

                <div class="actions" style="gap:8px;">
                  <button class="btn" type="submit">Salvar</button>

                  <form action="${ctx}/admin/formularios" method="post" onsubmit="return confirm('Excluir alternativa? (Não permitido se já houver respostas)')">
                    <input type="hidden" name="action" value="deleteAlternativa"/>
                    <input type="hidden" name="formularioId" value="${f.id}"/>
                    <input type="hidden" name="alternativaId" value="${a.id}"/>
                    <button class="btn danger" type="submit">Excluir</button>
                  </form>
                </div>
              </form>
            </c:forEach>
          </c:if>
        </div>
      </c:forEach>
    </c:if>

    <%@ include file="_footer.jspf" %>
  </main>
</body>
</html>
