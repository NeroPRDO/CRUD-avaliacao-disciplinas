/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Disciplina;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaDAO {

    private final ConnectionFactory cf;

    public DisciplinaDAO(ConnectionFactory cf) {
        this.cf = cf;
    }

    // CREATE
    public Disciplina insert(Disciplina d) {
        final String sql =
            "INSERT INTO disciplinas (curso_id, nome) VALUES (?, ?) RETURNING id";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (d.getCursoId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, d.getCursoId());
            ps.setString(2, d.getNome());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) d.setId(rs.getInt(1)); // id = INT
            }
            return d;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATE
    public void update(Disciplina d) {
        final String sql =
            "UPDATE disciplinas SET curso_id=?, nome=? WHERE id=?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (d.getCursoId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, d.getCursoId());
            ps.setString(2, d.getNome());
            ps.setInt(3, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    public void delete(Integer id) {
        final String sql = "DELETE FROM disciplinas WHERE id=?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // FIND BY ID
    public Disciplina findById(Integer id) {
        final String sql =
            "SELECT id, curso_id, nome FROM disciplinas WHERE id=?";
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Disciplina d = new Disciplina();
                    d.setId(rs.getInt("id")); // INT -> Integer
                    Integer cursoId = (Integer) rs.getObject("curso_id"); // pode ser null
                    d.setCursoId(cursoId);
                    d.setNome(rs.getString("nome"));
                    return d;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // LIST ALL
    public List<Disciplina> findAll() {
        final String sql =
            "SELECT id, curso_id, nome FROM disciplinas ORDER BY id";
        List<Disciplina> out = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Disciplina d = new Disciplina();
                d.setId(rs.getInt("id"));
                Integer cursoId = (Integer) rs.getObject("curso_id");
                d.setCursoId(cursoId);
                d.setNome(rs.getString("nome"));
                out.add(d);
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
  