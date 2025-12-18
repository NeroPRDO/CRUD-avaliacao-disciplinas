/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.AuditoriaLogDAO;
import br.ufpr.avaliacao.util.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = "/admin/auditoria")
public class AuditoriaLogServlet extends HttpServlet {

    private final AuditoriaLogDAO dao = new AuditoriaLogDAO();

    private LocalDate parseDate(String s) {
        try { return (s == null || s.isBlank()) ? null : LocalDate.parse(s); }
        catch (Exception e) { return null; }
    }

    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s); }
        catch (Exception e) { return null; }
    }

    private int parseInt(String s, int def) {
        try { return (s == null || s.isBlank()) ? def : Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Apenas Admin/Coord tem acesso
        Usuario u = SessionUtils.getUsuarioLogado(req.getSession());
        if (u == null || !(u.isAdmin() || u.isCoordenador())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN); return;
        }

        LocalDate de   = parseDate(req.getParameter("de"));
        LocalDate ate  = parseDate(req.getParameter("ate"));
        Long usuarioId = parseLong(req.getParameter("usuarioId"));
        String acao    = req.getParameter("acao");
        String tabela  = req.getParameter("tabela");
        String texto   = req.getParameter("q");
        int limit      = parseInt(req.getParameter("limit"), 500);

        req.setAttribute("logs",
            dao.search(de, ate, usuarioId, acao, tabela, texto, limit));

        // reenvia filtros para a view
        req.setAttribute("f_de",   de);
        req.setAttribute("f_ate",  ate);
        req.setAttribute("f_usuarioId", usuarioId);
        req.setAttribute("f_acao", acao);
        req.setAttribute("f_tabela", tabela);
        req.setAttribute("f_q", texto);
        req.setAttribute("f_limit", limit);

        req.getRequestDispatcher("/WEB-INF/views/auditoria-list.jsp").forward(req, resp);
    }
}
