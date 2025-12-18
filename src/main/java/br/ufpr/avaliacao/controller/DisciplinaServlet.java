/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Disciplina;
import br.ufpr.avaliacao.repository.ConnectionFactory;
import br.ufpr.avaliacao.repository.DisciplinaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/disciplinas"})
public class DisciplinaServlet extends HttpServlet {

    private DisciplinaDAO dao;

    @Override
    public void init() { dao = new DisciplinaDAO(new ConnectionFactory()); }

    private Integer parseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";

        switch (acao) {
            case "novo" -> {
                req.setAttribute("disciplina", new Disciplina());
                req.getRequestDispatcher("/WEB-INF/views/disciplinas-form.jsp").forward(req, resp);
            }
            case "editar" -> {
                Integer id = parseInt(req.getParameter("id"));
                req.setAttribute("disciplina", (id == null ? new Disciplina() : dao.findById(id)));
                req.getRequestDispatcher("/WEB-INF/views/disciplinas-form.jsp").forward(req, resp);
            }
            case "excluir" -> {
                Integer id = parseInt(req.getParameter("id"));
                if (id != null) dao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/disciplinas");
            }
            default -> {
                List<Disciplina> list = dao.findAll();
                req.setAttribute("disciplinas", list);
                req.getRequestDispatcher("/WEB-INF/views/disciplinas-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer id       = parseInt(req.getParameter("id"));
        Integer cursoId  = parseInt(req.getParameter("cursoId"));
        String  nome     = req.getParameter("nome");

        Disciplina d = new Disciplina();
        d.setId(id);
        d.setCursoId(cursoId);
        d.setNome(nome);

        try {
            if (d.getId() == null) dao.insert(d);
            else dao.update(d);
            resp.sendRedirect(req.getContextPath() + "/admin/disciplinas");
        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof SQLException sqle && "23503".equals(sqle.getSQLState())) {
                req.setAttribute("erro", "Curso inexistente (id=" + cursoId + "). Informe um curso válido.");
                req.setAttribute("disciplina", d);
                req.getRequestDispatcher("/WEB-INF/views/disciplinas-form.jsp").forward(req, resp);
            } else {
                throw ex;
            }
        }
    }
}
