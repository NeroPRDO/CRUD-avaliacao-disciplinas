/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.EnumSet;

@WebServlet(urlPatterns = "/admin/usuarios")
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";

        switch (acao) {
            case "novo" -> {
                req.setAttribute("usuario", new Usuario());
                req.getRequestDispatcher("/WEB-INF/views/usuarios-form.jsp").forward(req, resp);
            }
            case "editar" -> {
                Long id = Long.valueOf(req.getParameter("id"));
                req.setAttribute("usuario", dao.findById(id));
                req.getRequestDispatcher("/WEB-INF/views/usuarios-form.jsp").forward(req, resp);
            }
            case "excluir" -> {
                Long id = Long.valueOf(req.getParameter("id"));
                dao.delete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
            }
            default -> {
                req.setAttribute("usuarios", dao.listAll());
                req.getRequestDispatcher("/WEB-INF/views/usuarios-list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Salvar (insert/update)
        String id = req.getParameter("id");

        Usuario u = new Usuario();
        if (id != null && !id.isBlank()) u.setId(Long.valueOf(id));
        u.setNome(req.getParameter("nome"));
        u.setEmail(req.getParameter("email"));
        u.setLogin(req.getParameter("login"));
        u.setSenhaHash(req.getParameter("senha"));
        u.setAtivo("on".equals(req.getParameter("ativo")));

        EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);
        if (req.getParameter("perfilAluno") != null) perfis.add(Perfil.ALUNO);
        if (req.getParameter("perfilProfessor") != null) perfis.add(Perfil.PROFESSOR);
        if (req.getParameter("perfilCoordenador") != null) perfis.add(Perfil.COORDENADOR);
        if (req.getParameter("perfilAdmin") != null) perfis.add(Perfil.ADMIN);
        u.setPerfis(perfis);

        dao.save(u); // DAO cuida de alunos/professores
        resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
    }
}
