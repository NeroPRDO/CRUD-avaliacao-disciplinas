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

public class TurmaProfessorDAO {

    public void vincular(Integer turmaId, Long professorUsuarioId) {
        String sql = "INSERT INTO turmas_professores(turma_id, professor_id) VALUES(?,?) ON CONFLICT DO NOTHING";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            ps.setLong(2, professorUsuarioId);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void desvincular(Integer turmaId, Long professorUsuarioId) {
        String sql = "DELETE FROM turmas_professores WHERE turma_id=? AND professor_id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            ps.setLong(2, professorUsuarioId);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public List<Long> listarProfessoresDaTurma(Integer turmaId) {
        String sql = "SELECT professor_id FROM turmas_professores WHERE turma_id=?";
        List<Long> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, turmaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getLong("professor_id"));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public List<Integer> listarTurmasDoProfessor(Long professorUsuarioId) {
        String sql = "SELECT turma_id FROM turmas_professores WHERE professor_id=?";
        List<Integer> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, professorUsuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getInt("turma_id"));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }
}
