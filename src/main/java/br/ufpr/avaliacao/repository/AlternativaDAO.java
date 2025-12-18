/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Alternativa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlternativaDAO {
    private final ConnectionFactory cf;

    public AlternativaDAO(ConnectionFactory cf){ this.cf = cf; }

    public List<Alternativa> listByQuestao(Long questaoId){
        String sql = """
          SELECT id, questao_id, texto, peso, ordem
          FROM alternativas
          WHERE questao_id=?
          ORDER BY ordem, id
        """;
        List<Alternativa> out = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()){
                    Alternativa a = new Alternativa();
                    a.setId(rs.getLong("id"));
                    a.setQuestaoId(rs.getLong("questao_id"));
                    a.setTexto(rs.getString("texto"));
                    a.setPeso(rs.getInt("peso"));
                    a.setOrdem(rs.getInt("ordem"));
                    out.add(a);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
