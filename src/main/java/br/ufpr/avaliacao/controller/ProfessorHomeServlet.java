/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.*;
import br.ufpr.avaliacao.repository.*;
import br.ufpr.avaliacao.util.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = {"/professor"})
public class ProfessorHomeServlet extends HttpServlet {

    private FormularioDAO formularioDAO;
    private QuestaoDAO questaoDAO;
    private AlternativaDAO alternativaDAO;
    private AvaliacaoDAO avaliacaoDAO;

    @Override
    public void init() throws ServletException {
        formularioDAO = new FormularioDAO();

        ConnectionFactory cf = new ConnectionFactory();
        questaoDAO     = new QuestaoDAO(cf);
        alternativaDAO = new AlternativaDAO(cf);
        avaliacaoDAO   = new AvaliacaoDAO(cf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario prof = SessionUtils.getUsuarioLogado(req.getSession());
        if (prof == null || !prof.isProfessor()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Formulários vinculados às turmas do professor
        List<Formulario> forms = formularioDAO.listByProfessor(prof.getId());
        req.setAttribute("forms", forms);

        if (forms.isEmpty()) {
            req.getRequestDispatcher("/WEB-INF/views/professor-home.jsp").forward(req, resp);
            return;
        }

        Long paramFormId = parseLong(req.getParameter("formId"));
        boolean belongs = false;
        if (paramFormId != null) {
            for (Formulario f : forms) {
                if (Objects.equals(f.getId(), paramFormId)) { belongs = true; break; }
            }
        }
        Long selectedFormId = (paramFormId == null || !belongs)
                ? forms.get(forms.size() - 1).getId()
                : paramFormId;

        // Carrega questões e respostas
        List<Questao> qs = questaoDAO.listByFormulario(selectedFormId);
        Map<Long, List<Alternativa>> alts = new HashMap<>();
        Map<Long, List<Resposta>> respostasPorQuestao = new HashMap<>();

        for (Questao q : qs) {
            if (q.getTipo() != TipoQuestao.ABERTA) {
                alts.put(q.getId(), alternativaDAO.listByQuestao(q.getId()));
            }
            List<Resposta> rs = avaliacaoDAO.listRespostasByQuestao(q.getId());
            respostasPorQuestao.put(q.getId(), rs);
        }

        // Distribuição (fechadas) e lista de textos (abertas)
        Map<Long, List<Map<String, Object>>> distQuestoes = new HashMap<>();
        Map<Long, List<String>> respostasAbertas = new HashMap<>();

        for (Questao q : qs) {
            if (q.getTipo() == TipoQuestao.ABERTA) {
                List<Resposta> listResp = respostasPorQuestao.getOrDefault(q.getId(), List.of());
                List<String> textos = new ArrayList<>();
                for (Resposta r : listResp) {
                    String t = r.getTextoResposta();
                    if (t != null && !t.isBlank()) textos.add(t);
                }
                respostasAbertas.put(q.getId(), textos);
                continue;
            }

            List<Alternativa> listAlt = alts.getOrDefault(q.getId(), List.of());
            List<Resposta> listResp   = respostasPorQuestao.getOrDefault(q.getId(), List.of());

            Map<Long, Long> cont = new LinkedHashMap<>();
            for (Alternativa a : listAlt) cont.put(a.getId(), 0L);
            for (Resposta r : listResp) {
                if (r.getAlternativaId() != null) {
                    cont.computeIfPresent(r.getAlternativaId(), (k, v) -> v + 1);
                }
            }
            long total = cont.values().stream().mapToLong(x -> x).sum();

            List<Map<String, Object>> itens = new ArrayList<>();
            for (Alternativa a : listAlt) {
                long votos = cont.getOrDefault(a.getId(), 0L);
                double pct = (total == 0) ? 0.0 : (votos * 100.0 / total);
                Map<String, Object> m = new HashMap<>();
                m.put("alternativa", a.getTexto());
                m.put("votos", votos);
                m.put("percentual", pct);
                itens.add(m);
            }
            distQuestoes.put(q.getId(), itens);
        }

        req.setAttribute("formId", selectedFormId);
        req.setAttribute("questoes", qs);
        req.setAttribute("alternativas", alts);
        req.setAttribute("distQuestoes", distQuestoes);
        req.setAttribute("respostasAbertas", respostasAbertas); // <-- novo

        req.getRequestDispatcher("/WEB-INF/views/professor-home.jsp").forward(req, resp);
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
}
