/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 *
 * @author Pedro
 *
 */

import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class UsuarioDAO {

    private final ConnectionFactory cf; 

    public UsuarioDAO() {
        this.cf = null;
    }

    public UsuarioDAO(ConnectionFactory cf) {
        this.cf = cf;
    }

    private Connection conn() throws SQLException {
        if (cf != null) return cf.getConnection();
        return ConnectionFactory.getConnection();
    }

    // ------------------ Mappers ------------------
    private Usuario mapUsuario(ResultSet rs) throws Exception {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    }

    private EnumSet<Perfil> loadPerfis(Connection c, Long usuarioId) throws Exception {
        EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);
        String sql = "SELECT perfil FROM usuario_perfis WHERE usuario_id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String p = rs.getString("perfil");
                    try { perfis.add(Perfil.valueOf(p)); } catch (Exception ignore) {}
                }
            }
        }
        return perfis;
    }

    // ------------------ LISTAR ------------------
    public List<Usuario> listAll() {
        String sql = "SELECT id, nome, email, login, senha_hash, ativo FROM usuarios ORDER BY id";
        List<Usuario> list = new ArrayList<>();
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = mapUsuario(rs);
                u.setPerfis(loadPerfis(c, u.getId()));
                list.add(u);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    // ------------------ BUSCAR POR ID ------------------
    public Usuario findById(Long id) {
        String sql = "SELECT id, nome, email, login, senha_hash, ativo FROM usuarios WHERE id=?";
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapUsuario(rs);
                    u.setPerfis(loadPerfis(c, u.getId()));
                    return u;
                }
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    // ------------------ AUTENTICAÇÃO ------------------
    public Usuario findByLoginESenha(String login, String senha) {
        String sql = """
            SELECT id, nome, email, login, senha_hash, ativo
              FROM usuarios
             WHERE login=? AND senha_hash=? AND ativo=TRUE
            """;
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapUsuario(rs);
                    u.setPerfis(loadPerfis(c, u.getId()));
                    return u;
                }
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    /** Alias compatível com código antigo. */
    public Usuario findByLoginSenha(String login, String senha) {
        return findByLoginESenha(login, senha);
    }

    // ------------------ SALVAR (INSERT/UPDATE + PERFIS + ESPECIALIZAÇÕES) ------------------

    public Usuario save(Usuario u) {
        String ins = "INSERT INTO usuarios(nome,email,login,senha_hash,ativo) VALUES(?,?,?,?,?) RETURNING id";
        String upd = "UPDATE usuarios SET nome=?,email=?,login=?,senha_hash=?,ativo=? WHERE id=?";
        try (Connection c = conn()) {
            c.setAutoCommit(false);
            try {
                if (u.getId() == null) {
                    try (PreparedStatement ps = c.prepareStatement(ins)) {
                        ps.setString(1, u.getNome());
                        ps.setString(2, u.getEmail());
                        ps.setString(3, u.getLogin());
                        ps.setString(4, u.getSenhaHash());
                        ps.setBoolean(5, Boolean.TRUE.equals(u.getAtivo()));
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) u.setId(rs.getLong(1));
                        }
                    }
                } else {
                    try (PreparedStatement ps = c.prepareStatement(upd)) {
                        ps.setString(1, u.getNome());
                        ps.setString(2, u.getEmail());
                        ps.setString(3, u.getLogin());
                        ps.setString(4, u.getSenhaHash());
                        ps.setBoolean(5, Boolean.TRUE.equals(u.getAtivo()));
                        ps.setLong(6, u.getId());
                        ps.executeUpdate();
                    }
                    // limpa perfis antigos
                    try (PreparedStatement d = c.prepareStatement(
                            "DELETE FROM usuario_perfis WHERE usuario_id=?")) {
                        d.setLong(1, u.getId());
                        d.executeUpdate();
                    }
                }

                // grava perfis atuais
                if (u.getPerfis() != null && !u.getPerfis().isEmpty()) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO usuario_perfis(usuario_id, perfil) VALUES (?,?)")) {
                        for (Perfil p : u.getPerfis()) {
                            ps.setLong(1, u.getId());
                            ps.setString(2, p.name());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                boolean isAluno     = u.getPerfis() != null && u.getPerfis().contains(Perfil.ALUNO);
                boolean isProfessor = u.getPerfis() != null && u.getPerfis().contains(Perfil.PROFESSOR);

                if (isAluno)     ensureAluno(c, u.getId(), null);     else deleteAluno(c, u.getId());
                if (isProfessor) ensureProfessor(c, u.getId(), null, null); else deleteProfessor(c, u.getId());

                c.commit();
                return u;
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ------------------ ESPECIALIZAÇÕES ------------------

    /** Cria/atualiza linha em alunos; gera matrícula fallback se vier null. */
    private void ensureAluno(Connection c, Long usuarioId, String matricula) throws SQLException {
        String fallback = "ALU-" + usuarioId; 
        String upsert = """
            INSERT INTO alunos (usuario_id, matricula)
                 VALUES (?, COALESCE(?, ?))
            ON CONFLICT (usuario_id) DO UPDATE
                    SET matricula = COALESCE(EXCLUDED.matricula, alunos.matricula)
            """;
        try (PreparedStatement ps = c.prepareStatement(upsert)) {
            ps.setLong(1, usuarioId);
            if (matricula == null || matricula.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, matricula);
            }
            ps.setString(3, fallback);
            ps.executeUpdate();
        }
    }

    private void deleteAluno(Connection c, Long usuarioId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM alunos WHERE usuario_id=?")) {
            ps.setLong(1, usuarioId);
            ps.executeUpdate();
        }
    }

    /** Cria/atualiza linha em professores; gera registro fallback se vier null. */
    private void ensureProfessor(Connection c, Long usuarioId, String registro, String departamento) throws SQLException {
        String fallback = "PROF-" + usuarioId;
        String upsert = """
            INSERT INTO professores (usuario_id, registro, departamento)
                 VALUES (?, COALESCE(?, ?), COALESCE(?, ''))
            ON CONFLICT (usuario_id) DO UPDATE
                    SET registro = COALESCE(EXCLUDED.registro, professores.registro),
                        departamento = COALESCE(EXCLUDED.departamento, professores.departamento)
            """;
        try (PreparedStatement ps = c.prepareStatement(upsert)) {
            ps.setLong(1, usuarioId);
            if (registro == null || registro.isBlank()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, registro);
            }
            ps.setString(3, fallback);
            if (departamento == null) ps.setNull(4, Types.VARCHAR); else ps.setString(4, departamento);
            ps.executeUpdate();
        }
    }

    private void deleteProfessor(Connection c, Long usuarioId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM professores WHERE usuario_id=?")) {
            ps.setLong(1, usuarioId);
            ps.executeUpdate();
        }
    }

    // ------------------ EXCLUIR ------------------
    public void delete(Long id) {
        try (Connection c = conn();
             PreparedStatement ps = c.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
