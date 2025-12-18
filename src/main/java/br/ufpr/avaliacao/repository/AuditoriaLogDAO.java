/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 * @author Pedro
 */

import br.ufpr.avaliacao.model.AuditoriaLog;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaLogDAO {

    private AuditoriaLog map(ResultSet rs) throws Exception {
        AuditoriaLog a = new AuditoriaLog();
        a.setId(rs.getLong("id"));
        a.setMomento(rs.getTimestamp("momento")); // alias de data_hora

        long uid = rs.getLong("usuario_id");
        a.setUsuarioId(rs.wasNull() ? null : uid);

        a.setUsuarioNome(rs.getString("usuario_nome"));
        a.setAcao(rs.getString("acao"));
        a.setTabela(rs.getString("tabela"));      // alias de recurso
        a.setRegistroId(null);                    // não existe na sua tabela
        a.setDetalhes(rs.getString("detalhes"));
        a.setIp(rs.getString("ip"));
        return a;
    }

    /**
     * Busca com filtros. Parâmetro "tabela" filtra a coluna RECURSO.
     * "texto" busca em detalhes/recurso/ip/usuario_id.
     */
    public List<AuditoriaLog> search(
            LocalDate de, LocalDate ate,
            Long usuarioId, String acao, String tabela,
            String texto, int limit) {

        List<AuditoriaLog> out = new ArrayList<>();
        if (limit <= 0) limit = 500;

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT l.id,
                   l.data_hora AS momento,
                   l.usuario_id AS usuario_id,
                   u.nome       AS usuario_nome,
                   l.acao       AS acao,
                   l.recurso    AS tabela,
                   l.detalhes   AS detalhes,
                   l.ip         AS ip
              FROM auditoria_logs l
              LEFT JOIN usuarios u ON u.id = l.usuario_id
             WHERE 1=1
            """);

        ArrayList<Object> params = new ArrayList<>();

        if (de != null) {
            sql.append(" AND l.data_hora >= ?::date ");
            params.add(Date.valueOf(de));
        }
        if (ate != null) {
            sql.append(" AND l.data_hora < (?::date + INTERVAL '1 day') ");
            params.add(Date.valueOf(ate));
        }
        if (usuarioId != null) {
            sql.append(" AND l.usuario_id = ? ");
            params.add(usuarioId);
        }
        if (acao != null && !acao.isBlank()) {
            sql.append(" AND l.acao ILIKE ? ");
            params.add("%" + acao.trim() + "%");
        }
        if (tabela != null && !tabela.isBlank()) {      // mapeia para RECURSO
            sql.append(" AND l.recurso ILIKE ? ");
            params.add("%" + tabela.trim() + "%");
        }
        if (texto != null && !texto.isBlank()) {
            String like = "%" + texto.trim() + "%";
            sql.append("""
                AND (
                     l.detalhes ILIKE ?
                  OR l.recurso  ILIKE ?
                  OR l.ip       ILIKE ?
                  OR CAST(l.usuario_id AS TEXT) ILIKE ?
                )
                """);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY l.data_hora DESC NULLS LAST, l.id DESC ");
        sql.append(" LIMIT ? ");
        params.add(limit);

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
