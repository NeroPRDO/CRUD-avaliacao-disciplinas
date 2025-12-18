/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurmaAlunoDAO {

    public void matricular(Integer turmaId, Long alunoUsuarioId) {
        String sql = "INSERT INTO turmas_alunos(turma_id, aluno_id) VALUES(?,?) ON CONFLICT DO NOTHING";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            ps.setLong(2, alunoUsuarioId);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void desmatricular(Integer turmaId, Long alunoUsuarioId) {
        String sql = "DELETE FROM turmas_alunos WHERE turma_id=? AND aluno_id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            ps.setLong(2, alunoUsuarioId);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public List<Long> listarAlunosDaTurma(Integer turmaId) {
        String sql = "SELECT aluno_id FROM turmas_alunos WHERE turma_id=?";
        List<Long> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getLong("aluno_id"));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public List<Integer> listarTurmasDoAluno(Long alunoUsuarioId) {
        String sql = "SELECT turma_id FROM turmas_alunos WHERE aluno_id=?";
        List<Integer> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, alunoUsuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getInt("turma_id"));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }
}
