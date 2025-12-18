/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Avaliacao;
import br.ufpr.avaliacao.model.Resposta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {

    private final ConnectionFactory cf;

    public AvaliacaoDAO(ConnectionFactory cf) {
        this.cf = cf;
    }

    public Avaliacao salvarAvaliacaoComRespostas(Avaliacao a) {
        String insAval = "INSERT INTO avaliacoes (formulario_id, usuario_id) VALUES (?, ?) RETURNING id";
        String insResp = "INSERT INTO respostas (avaliacao_id, questao_id, texto, alternativa_id) VALUES (?, ?, ?, ?)";

        try (Connection c = cf.getConnection()) {
            boolean old = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                // avaliação
                try (PreparedStatement ps = c.prepareStatement(insAval);
                     ResultSet rs = executeInsertAval(ps, a)) {
                    if (rs.next()) a.setId(rs.getLong(1));
                }

                // respostas
                if (a.getRespostas() != null && !a.getRespostas().isEmpty()) {
                    try (PreparedStatement ps = c.prepareStatement(insResp)) {
                        for (Resposta r : a.getRespostas()) {
                            r.setAvaliacaoId(a.getId());
                            ps.setLong(1, r.getAvaliacaoId());
                            ps.setLong(2, r.getQuestaoId());
                            if (r.getTextoResposta() == null) ps.setNull(3, Types.LONGVARCHAR); else ps.setString(3, r.getTextoResposta());
                            if (r.getAlternativaId() == null) ps.setNull(4, Types.BIGINT); else ps.setLong(4, r.getAlternativaId());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(old);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return a;
    }

    private ResultSet executeInsertAval(PreparedStatement ps, Avaliacao a) throws SQLException {
        ps.setLong(1, a.getFormularioId());
        if (a.getUsuarioId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, a.getUsuarioId());
        return ps.executeQuery();
    }

    // --- consultas auxiliares usadas nos relatórios do professor ---
    public List<Resposta> listRespostasByQuestao(Long questaoId) {
        String sql = "SELECT id, avaliacao_id, questao_id, texto, alternativa_id FROM respostas WHERE questao_id = ?";
        List<Resposta> out = new ArrayList<>();
        try (Connection c = cf.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resposta r = new Resposta();
                    r.setId(rs.getLong("id"));
                    r.setAvaliacaoId(rs.getLong("avaliacao_id"));
                    r.setQuestaoId(rs.getLong("questao_id"));
                    r.setTextoResposta(rs.getString("texto"));
                    Long alt = (Long) rs.getObject("alternativa_id");
                    r.setAlternativaId(alt);
                    out.add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
