/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.repository;

/**
 * DAO de Formulário – CRUD, questões/alternativas e vínculos com turmas,
 * com métodos de listagem por aluno/professor robustos ao mapeamento de FK.
 */

import br.ufpr.avaliacao.model.*;

import java.sql.*;
import java.util.*;

public class FormularioDAO {

    // ---------- MAPEADORES ----------
    private Formulario mapForm(ResultSet rs) throws Exception {
        Formulario f = new Formulario();
        f.setId(rs.getLong("id"));
        f.setTitulo(rs.getString("titulo"));
        f.setInstrucoes(rs.getString("instrucoes"));
        f.setAnonimo(rs.getBoolean("is_anonimo"));
        return f;
    }

    private Questao mapQuestao(ResultSet rs) throws Exception {
        Questao q = new Questao();
        q.setId(rs.getLong("id"));
        q.setFormularioId(rs.getLong("formulario_id"));
        q.setEnunciado(rs.getString("enunciado"));
        q.setTipo(TipoQuestao.valueOf(rs.getString("tipo")));
        q.setObrigatoria(rs.getBoolean("obrigatoria"));
        q.setOrdem(rs.getInt("ordem"));
        return q;
    }

    private Alternativa mapAlt(ResultSet rs) throws Exception {
        Alternativa a = new Alternativa();
        a.setId(rs.getLong("id"));
        a.setQuestaoId(rs.getLong("questao_id"));
        a.setTexto(rs.getString("texto"));
        a.setPeso(rs.getInt("peso"));
        a.setOrdem(rs.getInt("ordem"));
        return a;
    }

    // ---------- FORMULÁRIOS ----------
    public List<Formulario> listAll() {
        List<Formulario> out = new ArrayList<>();
        String sql = "SELECT * FROM formularios ORDER BY id";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapForm(rs));
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public List<Formulario> findAll() {
        return listAll();
    }

    public Formulario findById(Long id) {
        String sql = "SELECT * FROM formularios WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapForm(rs);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    public Formulario save(Formulario f) {
        String ins = "INSERT INTO formularios(titulo,instrucoes,is_anonimo) VALUES(?,?,?) RETURNING id";
        String upd = "UPDATE formularios SET titulo=?, instrucoes=?, is_anonimo=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection()) {
            if (f.getId() == null) {
                try (PreparedStatement ps = c.prepareStatement(ins)) {
                    ps.setString(1, f.getTitulo());
                    ps.setString(2, f.getInstrucoes());
                    ps.setBoolean(3, Boolean.TRUE.equals(f.isAnonimo()));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) f.setId(rs.getLong(1));
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    ps.setString(1, f.getTitulo());
                    ps.setString(2, f.getInstrucoes());
                    ps.setBoolean(3, Boolean.TRUE.equals(f.isAnonimo()));
                    ps.setLong(4, f.getId());
                    ps.executeUpdate();
                }
            }
            return f;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void delete(Long id) {
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM formularios WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ---------- LISTAS POR PERFIL (ROBUSTAS) ----------

 public List<Formulario> listByAluno(Long alunoUsuarioId) {
        String sql = """
            SELECT DISTINCT f.*
              FROM formularios f
              JOIN formularios_turmas ft ON ft.formulario_id = f.id
              JOIN turmas t              ON t.id = ft.turma_id
              JOIN turmas_alunos ta      ON ta.turma_id = t.id
             WHERE ta.aluno_id = ?
             ORDER BY f.id
        """;
        List<Formulario> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, alunoUsuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapForm(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out;
    }

public List<Formulario> listByProfessor(Long professorUsuarioId) {
    String sql = """
        SELECT DISTINCT f.*
          FROM formularios f
          JOIN formularios_turmas ft ON ft.formulario_id = f.id
          JOIN turmas t              ON t.id = ft.turma_id
          JOIN turmas_professores tp ON tp.turma_id = t.id
         WHERE
               tp.professor_id = ?                                     -- caso grave usuarios.id
            OR EXISTS (SELECT 1 FROM professores p                      -- fallback por compatibilidade
                       WHERE p.usuario_id = tp.professor_id
                         AND p.usuario_id = ?)
         ORDER BY f.id
    """;
    List<Formulario> out = new ArrayList<>();
    try (Connection c = ConnectionFactory.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setLong(1, professorUsuarioId);
        ps.setLong(2, professorUsuarioId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapForm(rs));
        }
    } catch (Exception e) { throw new RuntimeException(e); }
    return out;
}

    // ---------- QUESTÕES ----------
    public List<Questao> listQuestoesByFormulario(Long formularioId) {
        String sql = "SELECT * FROM questoes WHERE formulario_id=? ORDER BY ordem, id";
        List<Questao> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, formularioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapQuestao(rs));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public List<Questao> findQuestoesByFormulario(Long formularioId) {
        return listQuestoesByFormulario(formularioId);
    }

    public Questao findQuestaoById(Long id) {
        String sql = "SELECT * FROM questoes WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapQuestao(rs);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    public Questao saveQuestao(Questao q) {
        String ins = "INSERT INTO questoes(formulario_id,enunciado,tipo,obrigatoria,ordem) VALUES(?,?,?,?,?) RETURNING id";
        String upd = "UPDATE questoes SET enunciado=?, tipo=?, obrigatoria=?, ordem=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection()) {
            if (q.getId() == null) {
                try (PreparedStatement ps = c.prepareStatement(ins)) {
                    ps.setLong(1, q.getFormularioId());
                    ps.setString(2, q.getEnunciado());
                    ps.setString(3, q.getTipo().name());
                    ps.setBoolean(4, Boolean.TRUE.equals(q.isObrigatoria()));
                    ps.setInt(5, q.getOrdem());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) q.setId(rs.getLong(1));
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    ps.setString(1, q.getEnunciado());
                    ps.setString(2, q.getTipo().name());
                    ps.setBoolean(3, Boolean.TRUE.equals(q.isObrigatoria()));
                    ps.setInt(4, q.getOrdem());
                    ps.setLong(5, q.getId());
                    ps.executeUpdate();
                }
            }
            return q;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void updateQuestao(Questao q) {
        String upd = "UPDATE questoes SET enunciado=?, tipo=?, obrigatoria=?, ordem=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(upd)) {
            ps.setString(1, q.getEnunciado());
            ps.setString(2, q.getTipo().name());
            ps.setBoolean(3, Boolean.TRUE.equals(q.isObrigatoria()));
            ps.setInt(4, q.getOrdem());
            ps.setLong(5, q.getId());
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public boolean deleteQuestaoIfNoResponses(Long questaoId) {
        String has = "SELECT 1 FROM respostas WHERE questao_id=? LIMIT 1";
        String del = "DELETE FROM questoes WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(has)) {
                ps.setLong(1, questaoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(del)) {
                ps.setLong(1, questaoId);
                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ---------- ALTERNATIVAS ----------
    public List<Alternativa> listAlternativasByQuestao(Long questaoId) {
        String sql = "SELECT * FROM alternativas WHERE questao_id=? ORDER BY ordem, id";
        List<Alternativa> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, questaoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapAlt(rs));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return out;
    }

    public List<Alternativa> findAlternativasByQuestao(Long questaoId) {
        return listAlternativasByQuestao(questaoId);
    }

    public Alternativa findAlternativaById(Long id) {
        String sql = "SELECT * FROM alternativas WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAlt(rs);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return null;
    }

    public Alternativa saveAlternativa(Alternativa a) {
        String ins = "INSERT INTO alternativas(questao_id,texto,peso,ordem) VALUES(?,?,?,?) RETURNING id";
        String upd = "UPDATE alternativas SET texto=?, peso=?, ordem=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection()) {
            if (a.getId() == null) {
                try (PreparedStatement ps = c.prepareStatement(ins)) {
                    ps.setLong(1, a.getQuestaoId());
                    ps.setString(2, a.getTexto());
                    ps.setInt(3, a.getPeso());
                    ps.setInt(4, a.getOrdem());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) a.setId(rs.getLong(1));
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    ps.setString(1, a.getTexto());
                    ps.setInt(2, a.getPeso());
                    ps.setInt(3, a.getOrdem());
                    ps.setLong(4, a.getId());
                    ps.executeUpdate();
                }
            }
            return a;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void updateAlternativa(Alternativa a) {
        String upd = "UPDATE alternativas SET texto=?, peso=?, ordem=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(upd)) {
            ps.setString(1, a.getTexto());
            ps.setInt(2, a.getPeso());
            ps.setInt(3, a.getOrdem());
            ps.setLong(4, a.getId());
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public boolean deleteAlternativaIfNoResponses(Long alternativaId) {
        String has = "SELECT 1 FROM respostas WHERE alternativa_id=? LIMIT 1";
        String del = "DELETE FROM alternativas WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(has)) {
                ps.setLong(1, alternativaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return false;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(del)) {
                ps.setLong(1, alternativaId);
                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ---------- VÍNCULO FORMULÁRIOS <-> TURMAS ----------
    public static class Turma {
        private Long id;
        private Long disciplinaId;
        private String codigo;
        private String anoSemestre;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getDisciplinaId() { return disciplinaId; }
        public void setDisciplinaId(Long disciplinaId) { this.disciplinaId = disciplinaId; }
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public String getAnoSemestre() { return anoSemestre; }
        public void setAnoSemestre(String anoSemestre) { this.anoSemestre = anoSemestre; }
    }

    public List<Turma> listAllTurmas() {
        String sql = "SELECT id, disciplina_id, codigo, ano_semestre FROM turmas ORDER BY codigo, id";
        List<Turma> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Turma t = new Turma();
                t.setId(rs.getLong("id"));
                t.setDisciplinaId(rs.getLong("disciplina_id"));
                t.setCodigo(rs.getString("codigo"));
                t.setAnoSemestre(rs.getString("ano_semestre"));
                list.add(t);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return list;
    }

    public Set<Long> listTurmasVinculadas(Long formularioId) {
        String sql = "SELECT turma_id FROM formularios_turmas WHERE formulario_id = ?";
        Set<Long> ids = new LinkedHashSet<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, formularioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong(1));
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return ids;
    }

    public void updateVinculosFormTurmas(Long formularioId, Collection<Long> turmaIds) {
        String del = "DELETE FROM formularios_turmas WHERE formulario_id = ?";
        String ins = "INSERT INTO formularios_turmas(formulario_id, turma_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection c = ConnectionFactory.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement d = c.prepareStatement(del)) {
                d.setLong(1, formularioId);
                d.executeUpdate();
            }
            if (turmaIds != null && !turmaIds.isEmpty()) {
                try (PreparedStatement i = c.prepareStatement(ins)) {
                    for (Long id : turmaIds) {
                        i.setLong(1, formularioId);
                        i.setLong(2, id);
                        i.addBatch();
                    }
                    i.executeBatch();
                }
            }
            c.commit();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
