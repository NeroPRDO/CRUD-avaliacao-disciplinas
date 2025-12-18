/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.util.SessionUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebFilter(urlPatterns = {
        "/aluno/*", "/aluno",
        "/professor/*", "/professor",
        "/admin/*", "/admin"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Usuario user = SessionUtils.getUsuarioLogado(req.getSession());
        String path  = req.getRequestURI().substring(req.getContextPath().length());

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        boolean ok;
        if (path.startsWith("/admin")) {
            ok = user.isAdmin() || user.isCoordenador();
        } else if (path.startsWith("/professor")) {
            ok = user.isProfessor() || user.isAdmin() || user.isCoordenador();
        } else if (path.startsWith("/aluno")) {
            ok = user.isAluno() || user.isAdmin() || user.isCoordenador();
        } else {
            ok = true;
        }

        if (!ok) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
