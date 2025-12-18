/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.util.SessionUtils;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.ConnectionFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/turmas/professores"})
public class TurmaProfessorServlet extends HttpServlet {

    public static class ProfRow {
        private long id;
        private String nome;
        private String login;
        private String registro;
        private String departamento;
        public ProfRow(long id, String nome, String login, String registro, String departamento) {
            this.id = id; this.nome = nome; this.login = login; this.registro = registro; this.departamento = departamento;
        }
        public long getId() { return id; }
        public String getNome() { return nome; }
        public String getLogin() { return login; }
        public String getRegistro() { return registro; }
        public String getDepartamento() { return departamento; }
    }

    public static class TurmaInfo {
        private int id;
        private String codigo;
        private String anoSemestre;
        private String disciplina;
        public TurmaInfo(int id, String codigo, String anoSemestre, String disciplina) {
            this.id = id; this.codigo = codigo; this.anoSemestre = anoSemestre; this.disciplina = disciplina;
        }
        public int getId() { return id; }
        public String getCodigo() { return codigo; }
        public String getAnoSemestre() { return anoSemestre; }
        public String getDisciplina() { return disciplina; }
    }

    private final ConnectionFactory cf = new ConnectionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = SessionUtils.getUsuarioLogado(req.getSession());
        if (u == null || !(u.isAdmin() || u.isCoordenador())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN); return;
        }

        Integer turmaId = parseInt(req.getParameter("turmaId"));
        if (turmaId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/turmas");
            return;
        }

        TurmaInfo turma = loadTurma(turmaId);
        if (turma == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/turmas");
            return;
        }

        List<ProfRow> vinculados = loadVinculados(turmaId);
        List<ProfRow> disponiveis = loadDisponiveis(turmaId);

        req.setAttribute("turma", turma);
        req.setAttribute("vinculados", vinculados);
        req.setAttribute("disponiveis", disponiveis);

        req.getRequestDispatcher("/WEB-INF/views/turmas-professores-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Usuario u = SessionUtils.getUsuarioLogado(req.getSession());
        if (u == null || !(u.isAdmin() || u.isCoordenador())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN); return;
        }

        String action = req.getParameter("action");
        Integer turmaId = parseInt(req.getParameter("turmaId"));

        if (turmaId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/turmas");
            return;
        }

        if ("add".equals(action)) {
            String[] profs = req.getParameterValues("professorId");
            if (profs != null) {
                try (Connection c = cf.getConnection();
                     PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO turmas_professores (turma_id, professor_id) VALUES (?, ?) ON CONFLICT (turma_id, professor_id) DO NOTHING")) {
                    for (String s : profs) {
                        Long professorId = parseLong(s);
                        if (professorId != null) {
                            ps.setInt(1, turmaId);
                            ps.setLong(2, professorId);
                            ps.addBatch();
                        }
                    }
                    ps.executeBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if ("remove".equals(action)) {
            Long professorId = parseLong(req.getParameter("professorId"));
            if (professorId != null) {
                try (Connection c = cf.getConnection();
                     PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM turmas_professores WHERE turma_id=? AND professor_id=?")) {
                    ps.setInt(1, turmaId);
                    ps.setLong(2, professorId);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/turmas/professores?turmaId=" + turmaId);
    }

    private TurmaInfo loadTurma(int turmaId) {
        String sql = """
            SELECT t.id, t.codigo, t.ano_semestre, d.nome AS disciplina
              FROM turmas t
              JOIN disciplinas d ON d.id = t.disciplina_id
             WHERE t.id = ?
        """;
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TurmaInfo(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("ano_semestre"),
                        rs.getString("disciplina")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<ProfRow> loadVinculados(int turmaId) {
        String sql = """
            SELECT u.id, u.nome, u.login, p.registro, p.departamento
              FROM turmas_professores tp
              JOIN professores p ON p.usuario_id = tp.professor_id
              JOIN usuarios u    ON u.id = p.usuario_id
             WHERE tp.turma_id = ?
             ORDER BY u.nome
        """;
        List<ProfRow> list = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProfRow(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("registro"),
                        rs.getString("departamento")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private List<ProfRow> loadDisponiveis(int turmaId) {
        String sql = """
            SELECT u.id, u.nome, u.login, p.registro, p.departamento
              FROM professores p
              JOIN usuarios u ON u.id = p.usuario_id
              JOIN usuario_perfis up ON up.usuario_id = u.id AND up.perfil = 'PROFESSOR'
             WHERE u.ativo = TRUE
               AND p.usuario_id NOT IN (SELECT professor_id FROM turmas_professores WHERE turma_id = ?)
             ORDER BY u.nome
        """;
        List<ProfRow> list = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProfRow(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("registro"),
                        rs.getString("departamento")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Integer parseInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private Long parseLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
}
