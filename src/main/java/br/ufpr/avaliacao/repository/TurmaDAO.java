/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Turma;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurmaDAO {

    private final ConnectionFactory cf;

    public TurmaDAO(ConnectionFactory cf) {
        this.cf = cf;
    }

    public List<Turma> findAll() {
        String sql = "SELECT id, disciplina_id, codigo, ano_semestre FROM turmas ORDER BY id";
        List<Turma> out = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Turma t = new Turma();
                t.setId(rs.getObject("id") == null ? null : Long.valueOf(rs.getInt("id"))); // INT -> Long
                t.setDisciplinaId(rs.getObject("disciplina_id") == null ? null : Long.valueOf(rs.getInt("disciplina_id")));
                t.setCodigo(rs.getString("codigo"));
                t.setAnoSemestre(rs.getString("ano_semestre"));
                out.add(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public Turma findById(Long id) {
        String sql = "SELECT id, disciplina_id, codigo, ano_semestre FROM turmas WHERE id = ?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (id == null) return null;
            ps.setInt(1, id.intValue()); // Long -> INT
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Turma t = new Turma();
                    t.setId(rs.getObject("id") == null ? null : Long.valueOf(rs.getInt("id")));
                    t.setDisciplinaId(rs.getObject("disciplina_id") == null ? null : Long.valueOf(rs.getInt("disciplina_id")));
                    t.setCodigo(rs.getString("codigo"));
                    t.setAnoSemestre(rs.getString("ano_semestre"));
                    return t;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void insert(Turma t) {
        String sql = "INSERT INTO turmas (disciplina_id, codigo, ano_semestre) VALUES (?, ?, ?)";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (t.getDisciplinaId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, t.getDisciplinaId().intValue()); // Long -> INT
            ps.setString(2, t.getCodigo());
            ps.setString(3, t.getAnoSemestre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(Long.valueOf(rs.getInt(1))); // key INT -> Long
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Turma t) {
        String sql = "UPDATE turmas SET disciplina_id = ?, codigo = ?, ano_semestre = ? WHERE id = ?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (t.getDisciplinaId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, t.getDisciplinaId().intValue());
            ps.setString(2, t.getCodigo());
            ps.setString(3, t.getAnoSemestre());
            ps.setInt(4, t.getId().intValue()); // Long -> INT
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM turmas WHERE id = ?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id.intValue()); // Long -> INT
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // vinculações
    public void addProfessor(Long turmaId, Long professorUsuarioId) {
        String sql = "INSERT INTO turmas_professores (turma_id, professor_id) VALUES (?, ?)";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId.intValue());
            ps.setLong(2, professorUsuarioId); 
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAluno(Long turmaId, Long alunoUsuarioId) {
        String sql = "INSERT INTO turmas_alunos (turma_id, aluno_id) VALUES (?, ?)";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId.intValue());
            ps.setLong(2, alunoUsuarioId); 
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
