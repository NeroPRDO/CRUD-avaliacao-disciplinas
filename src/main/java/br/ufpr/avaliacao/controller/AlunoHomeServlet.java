/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Formulario;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.FormularioDAO;
import br.ufpr.avaliacao.util.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/aluno")
public class AlunoHomeServlet extends HttpServlet {

    private final FormularioDAO formDAO = new FormularioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario logado = SessionUtils.getUsuarioLogado(req.getSession());
        if (logado == null || !logado.isAluno()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Formularios vinculados às turmas em que este aluno (usuarios.id) está matriculado
        List<Formulario> forms = formDAO.listByAluno(logado.getId());

        req.setAttribute("formularios", forms);

        req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
    }
}
