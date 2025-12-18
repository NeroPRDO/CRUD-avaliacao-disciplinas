/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Curso;

import java.sql.*;
import java.util.*;

public class CursoDAO {

    private Curso map(ResultSet rs) throws Exception {
        Curso c = new Curso();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCurriculo(rs.getString("curriculo"));
        return c;
    }

    public List<Curso> listAll() {
        List<Curso> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM cursos ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public Curso findById(Integer id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM cursos WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    public Curso save(Curso x) {
        try (Connection c = ConnectionFactory.getConnection()) {
            if (x.getId() == null) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO cursos(nome,curriculo) VALUES(?,?) RETURNING id")) {
                    ps.setString(1, x.getNome());
                    ps.setString(2, x.getCurriculo());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) x.setId(rs.getInt(1));
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE cursos SET nome=?, curriculo=? WHERE id=?")) {
                    ps.setString(1, x.getNome());
                    ps.setString(2, x.getCurriculo());
                    ps.setInt(3, x.getId());
                    ps.executeUpdate();
                }
            }
            return x;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void delete(Integer id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cursos WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
