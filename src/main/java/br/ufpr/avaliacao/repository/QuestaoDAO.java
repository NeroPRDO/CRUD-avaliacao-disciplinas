/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Questao;
import br.ufpr.avaliacao.model.TipoQuestao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestaoDAO {
    private final ConnectionFactory cf;

    public QuestaoDAO(ConnectionFactory cf){ this.cf = cf; }

    public List<Questao> listByFormulario(Long formularioId){
        String sql = """
          SELECT id, formulario_id, enunciado, tipo, obrigatoria, ordem
          FROM questoes
          WHERE formulario_id=?
          ORDER BY ordem, id
        """;
        List<Questao> out = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, formularioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    Questao q = new Questao();
                    q.setId(rs.getLong("id"));
                    q.setFormularioId(rs.getLong("formulario_id"));
                    q.setEnunciado(rs.getString("enunciado"));
                    q.setTipo(TipoQuestao.valueOf(rs.getString("tipo")));
                    q.setObrigatoria(rs.getBoolean("obrigatoria"));
                    q.setOrdem(rs.getInt("ordem"));
                    out.add(q);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
