/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.*;
import br.ufpr.avaliacao.repository.*;
import br.ufpr.avaliacao.util.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = {"/aluno/responder"})
public class ResponderServlet extends HttpServlet {

    private FormularioDAO formularioDAO;
    private QuestaoDAO questaoDAO;
    private AlternativaDAO alternativaDAO;
    private AvaliacaoDAO avaliacaoDAO;

    @Override
    public void init() throws ServletException {
        formularioDAO = new FormularioDAO(); // sem args
        ConnectionFactory cf = new ConnectionFactory();
        questaoDAO     = new QuestaoDAO(cf);
        alternativaDAO = new AlternativaDAO(cf);
        avaliacaoDAO   = new AvaliacaoDAO(cf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long formularioId = parseLong(req.getParameter("id"));
        if (formularioId == null) {
            resp.sendRedirect(req.getContextPath() + "/aluno");
            return;
        }

        Formulario f = formularioDAO.findById(formularioId);
        if (f == null) {
            req.setAttribute("erro", "Formulário não encontrado.");
            req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
            return;
        }

        List<Questao> qs = questaoDAO.listByFormulario(formularioId);
        Map<Long, List<Alternativa>> mapa = new HashMap<>();
        for (Questao q : qs) {
            if (q.getTipo() != TipoQuestao.ABERTA) {
                mapa.put(q.getId(), alternativaDAO.listByQuestao(q.getId()));
            }
        }

        req.setAttribute("form", f);
        req.setAttribute("questoes", qs);
        req.setAttribute("alternativas", mapa);
        req.getRequestDispatcher("/WEB-INF/views/responder-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario u = SessionUtils.getUsuarioLogado(req.getSession());
        Long formularioId = parseLong(req.getParameter("formularioId"));
        if (formularioId == null) {
            resp.sendRedirect(req.getContextPath() + "/aluno");
            return;
        }

        Avaliacao a = new Avaliacao();
        a.setFormularioId(formularioId);
        a.setUsuarioId(u == null ? null : u.getId());
        a.setRespostas(new ArrayList<>());

        List<Questao> qs = questaoDAO.listByFormulario(formularioId);

        for (Questao q : qs) {
            String param = "q_" + q.getId();
            switch (q.getTipo()) {
                case ABERTA -> {
                    String texto = req.getParameter(param);
                    if (texto != null && !texto.isBlank()) {
                        Resposta r = new Resposta();
                        r.setQuestaoId(q.getId());
                        r.setTextoResposta(texto);
                        a.getRespostas().add(r);
                    }
                }
                case UNICA -> {
                    String v = req.getParameter(param);
                    if (v != null) {
                        Resposta r = new Resposta();
                        r.setQuestaoId(q.getId());
                        r.setAlternativaId(Long.valueOf(v));
                        a.getRespostas().add(r);
                    }
                }
                case MULTIPLA -> {
                    String[] vs = req.getParameterValues(param);
                    if (vs != null) {
                        for (String v : vs) {
                            Resposta r = new Resposta();
                            r.setQuestaoId(q.getId());
                            r.setAlternativaId(Long.valueOf(v));
                            a.getRespostas().add(r);
                        }
                    }
                }
            }
        }

        avaliacaoDAO.salvarAvaliacaoComRespostas(a);
        resp.sendRedirect(req.getContextPath() + "/aluno?ok=1");
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
}
