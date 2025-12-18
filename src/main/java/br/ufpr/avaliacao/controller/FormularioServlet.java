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
import br.ufpr.avaliacao.repository.FormularioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = "/admin/formularios")
public class FormularioServlet extends HttpServlet {

    private FormularioDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new FormularioDAO();
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private Integer parseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private boolean parseBool(String s) { return "on".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";

        switch (acao) {
            case "novo" -> {
                req.setAttribute("formulario", new Formulario());
                // listas de turmas para o seletor
                req.setAttribute("turmas", dao.listAllTurmas());
                req.setAttribute("turmasVinculadas", Set.of());
                req.getRequestDispatcher("/WEB-INF/views/formularios-form.jsp").forward(req, resp);
            }
            case "editar" -> {
                Long id = parseLong(req.getParameter("id"));
                if (id == null) {
                    resp.sendRedirect(req.getContextPath() + "/admin/formularios");
                    return;
                }
                Formulario f = dao.findById(id);
                List<Questao> qs = dao.listQuestoesByFormulario(id);

                Map<Long, List<Alternativa>> alts = new HashMap<>();
                for (Questao q : qs) {
                    alts.put(q.getId(), dao.listAlternativasByQuestao(q.getId()));
                }

                req.setAttribute("formulario", f);
                req.setAttribute("questoes", qs);
                req.setAttribute("alternativasPorQuestao", alts);

                // >>> VÍNCULOS
                req.setAttribute("turmas", dao.listAllTurmas());
                req.setAttribute("turmasVinculadas", dao.listTurmasVinculadas(id));

                req.getRequestDispatcher("/WEB-INF/views/formularios-form.jsp").forward(req, resp);
            }
            case "excluir" -> {
                Long id = parseLong(req.getParameter("id"));
                if (id != null) dao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios");
            }
            default -> {
                List<Formulario> list = dao.listAll();
                req.setAttribute("formularios", list);
                req.getRequestDispatcher("/WEB-INF/views/formularios-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null) action = "saveForm";

        switch (action) {
            // ---------- FORM ----------
            default -> { // "saveForm"
                Long id    = parseLong(req.getParameter("id"));
                String t   = req.getParameter("titulo");
                String ins = req.getParameter("instrucoes");
                boolean an = parseBool(req.getParameter("anonimo"));

                Formulario f = new Formulario();
                f.setId(id);
                f.setTitulo(t);
                f.setInstrucoes(ins);
                f.setAnonimo(an);

                f = dao.save(f);

                // salva vínculos com turmas (checkbox name="turmaIds")
                String[] turmaIds = req.getParameterValues("turmaIds");
                List<Long> vinc = new ArrayList<>();
                if (turmaIds != null) {
                    for (String s : turmaIds) {
                        Long tid = parseLong(s);
                        if (tid != null) vinc.add(tid);
                    }
                }
                dao.updateVinculosFormTurmas(f.getId(), vinc);

                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + f.getId());
            }

            // ---------- QUESTÃO ----------
            case "addQuestao" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                String enunciado  = req.getParameter("enunciado");
                String tipoStr    = req.getParameter("tipo");
                Integer ordem     = parseInt(req.getParameter("ordem"));
                boolean obrig     = parseBool(req.getParameter("obrigatoria"));

                Questao q = new Questao();
                q.setFormularioId(formularioId);
                q.setEnunciado(enunciado);
                q.setTipo(TipoQuestao.valueOf(tipoStr));
                q.setObrigatoria(obrig);
                q.setOrdem(ordem == null ? 0 : ordem);

                dao.saveQuestao(q);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }
            case "editQuestao" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                Long questaoId    = parseLong(req.getParameter("questaoId"));
                String enunciado  = req.getParameter("enunciado");
                String tipoStr    = req.getParameter("tipo");
                Integer ordem     = parseInt(req.getParameter("ordem"));
                boolean obrig     = parseBool(req.getParameter("obrigatoria"));

                Questao q = new Questao();
                q.setId(questaoId);
                q.setEnunciado(enunciado);
                q.setTipo(TipoQuestao.valueOf(tipoStr));
                q.setObrigatoria(obrig);
                q.setOrdem(ordem == null ? 0 : ordem);

                dao.updateQuestao(q);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }
            case "deleteQuestao" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                Long questaoId    = parseLong(req.getParameter("questaoId"));
                dao.deleteQuestaoIfNoResponses(questaoId);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }

            // ---------- ALTERNATIVA ----------
            case "addAlternativa" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                Long questaoId    = parseLong(req.getParameter("questaoId"));
                String texto      = req.getParameter("texto");
                Integer peso      = parseInt(req.getParameter("peso"));
                Integer ordem     = parseInt(req.getParameter("ordem"));

                Alternativa a = new Alternativa();
                a.setQuestaoId(questaoId);
                a.setTexto(texto);
                a.setPeso(peso == null ? 1 : peso);
                a.setOrdem(ordem == null ? 0 : ordem);

                dao.saveAlternativa(a);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }
            case "editAlternativa" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                Long alternativaId= parseLong(req.getParameter("alternativaId"));
                String texto      = req.getParameter("texto");
                Integer peso      = parseInt(req.getParameter("peso"));
                Integer ordem     = parseInt(req.getParameter("ordem"));

                Alternativa a = new Alternativa();
                a.setId(alternativaId);
                a.setTexto(texto);
                a.setPeso(peso == null ? 1 : peso);
                a.setOrdem(ordem == null ? 0 : ordem);

                dao.updateAlternativa(a);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }
            case "deleteAlternativa" -> {
                Long formularioId = parseLong(req.getParameter("formularioId"));
                Long alternativaId= parseLong(req.getParameter("alternativaId"));
                dao.deleteAlternativaIfNoResponses(alternativaId);
                resp.sendRedirect(req.getContextPath() + "/admin/formularios?acao=editar&id=" + formularioId);
            }
        }
    }
}
