/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Turma;
import br.ufpr.avaliacao.repository.ConnectionFactory;
import br.ufpr.avaliacao.repository.TurmaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/admin/turmas")
public class TurmaServlet extends HttpServlet {

    private TurmaDAO turmaDAO;

    @Override
    public void init() throws ServletException {
        turmaDAO = new TurmaDAO(new ConnectionFactory());
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";

        switch (acao) {
            case "novo" -> {
                req.setAttribute("turma", new Turma());
                req.getRequestDispatcher("/WEB-INF/views/turmas-form.jsp").forward(req, resp);
            }
            case "editar" -> {
                Long id = parseLong(req.getParameter("id"));
                if (id != null) {
                    req.setAttribute("turma", turmaDAO.findById(id));
                    req.getRequestDispatcher("/WEB-INF/views/turmas-form.jsp").forward(req, resp);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/turmas");
                }
            }
            case "excluir" -> {
                Long id = parseLong(req.getParameter("id"));
                if (id != null) turmaDAO.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/turmas");
            }
            default -> {
                List<Turma> list = turmaDAO.findAll();
                req.setAttribute("turmas", list);
                req.getRequestDispatcher("/WEB-INF/views/turmas-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id           = parseLong(req.getParameter("id"));
        Long disciplinaId = parseLong(req.getParameter("disciplinaId"));
        String codigo     = req.getParameter("codigo");
        String anoSemestre= req.getParameter("anoSemestre");

        Turma t = new Turma();
        t.setId(id);
        t.setDisciplinaId(disciplinaId);
        t.setCodigo(codigo);
        t.setAnoSemestre(anoSemestre);

        if (t.getId() == null) turmaDAO.insert(t);
        else turmaDAO.update(t);

        resp.sendRedirect(req.getContextPath() + "/admin/turmas");
    }
}
