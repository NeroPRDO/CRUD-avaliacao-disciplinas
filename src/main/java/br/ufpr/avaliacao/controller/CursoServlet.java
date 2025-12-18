/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Curso;
import br.ufpr.avaliacao.repository.CursoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/cursos"})
public class CursoServlet extends HttpServlet {

    private CursoDAO dao;

    @Override
    public void init() {
        dao = new CursoDAO(); 
    }

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
                req.setAttribute("curso", new Curso());
                req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
            }
            case "editar" -> {
                Integer id = parseInt(req.getParameter("id"));
                Curso c = (id == null) ? new Curso() : dao.findById(id);
                req.setAttribute("curso", c);
                req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
            }
            case "excluir" -> {
                Integer id = parseInt(req.getParameter("id"));
                if (id != null) dao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/cursos");
            }
            default -> {
                List<Curso> cursos = dao.listAll(); // << listAll()
                req.setAttribute("cursos", cursos);
                req.getRequestDispatcher("/WEB-INF/views/cursos-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer id = parseInt(req.getParameter("id"));
        String nome = req.getParameter("nome");
        String curriculo = req.getParameter("curriculo");

        Curso c = new Curso();
        c.setId(id);
        c.setNome(nome);
        c.setCurriculo(curriculo);

        dao.save(c); 

        resp.sendRedirect(req.getContextPath() + "/admin/cursos");
    }
}
