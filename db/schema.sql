-- ======================================================================
-- LIMPEZA
-- ======================================================================
DROP TABLE IF EXISTS auditoria_logs         CASCADE;
DROP TABLE IF EXISTS respostas              CASCADE;
DROP TABLE IF EXISTS avaliacoes             CASCADE;
DROP TABLE IF EXISTS alternativas           CASCADE;
DROP TABLE IF EXISTS questoes               CASCADE;
DROP TABLE IF EXISTS formularios_turmas     CASCADE;
DROP TABLE IF EXISTS formularios            CASCADE;

DROP TABLE IF EXISTS turmas_professores     CASCADE;
DROP TABLE IF EXISTS turmas_alunos          CASCADE;
DROP TABLE IF EXISTS turmas                 CASCADE;
DROP TABLE IF EXISTS disciplinas            CASCADE;
DROP TABLE IF EXISTS cursos                 CASCADE;

DROP TABLE IF EXISTS professores            CASCADE;
DROP TABLE IF EXISTS alunos                 CASCADE;
DROP TABLE IF EXISTS usuario_perfis         CASCADE;
DROP TABLE IF EXISTS usuarios               CASCADE;

-- ======================================================================
-- TABELAS BÁSICAS
-- ======================================================================
CREATE TABLE usuarios (
  id         BIGSERIAL PRIMARY KEY,
  nome       VARCHAR(150) NOT NULL,
  email      VARCHAR(255) NOT NULL,
  login      VARCHAR(60)  NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  ativo      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario_perfis (
  id         BIGSERIAL PRIMARY KEY,
  usuario_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  perfil     VARCHAR(30) NOT NULL  -- ALUNO | PROFESSOR | COORDENADOR | ADMIN
);

CREATE TABLE alunos (
  usuario_id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
  matricula  VARCHAR(30) NOT NULL
);

CREATE TABLE professores (
  usuario_id   BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
  registro     VARCHAR(50),
  departamento VARCHAR(100)
);

CREATE TABLE cursos (
  id        SERIAL PRIMARY KEY,
  nome      VARCHAR(150) NOT NULL,
  curriculo TEXT
);

CREATE TABLE disciplinas (
  id        SERIAL PRIMARY KEY,
  curso_id  INT NOT NULL REFERENCES cursos(id) ON DELETE CASCADE,
  nome      VARCHAR(150) NOT NULL
);

CREATE TABLE turmas (
  id            SERIAL PRIMARY KEY,
  disciplina_id INT NOT NULL REFERENCES disciplinas(id) ON DELETE CASCADE,
  codigo        VARCHAR(40) NOT NULL,
  ano_semestre  VARCHAR(20) NOT NULL
);

CREATE TABLE turmas_professores (
  id           SERIAL PRIMARY KEY,
  turma_id     INT    NOT NULL REFERENCES turmas(id) ON DELETE CASCADE,
  professor_id BIGINT NOT NULL REFERENCES professores(usuario_id) ON DELETE CASCADE,
  UNIQUE (turma_id, professor_id)
);

CREATE TABLE turmas_alunos (
  id             SERIAL PRIMARY KEY,
  turma_id       INT    NOT NULL REFERENCES turmas(id) ON DELETE CASCADE,
  aluno_id       BIGINT NOT NULL REFERENCES alunos(usuario_id) ON DELETE CASCADE,
  data_matricula DATE DEFAULT CURRENT_DATE,
  UNIQUE (turma_id, aluno_id)
);

-- ======================================================================
-- FORMULÁRIOS / QUESTÕES / ALTERNATIVAS / AVALIAÇÕES
-- ======================================================================
CREATE TABLE formularios (
  id         BIGSERIAL PRIMARY KEY,
  titulo     VARCHAR(255) NOT NULL,
  instrucoes TEXT,
  is_anonimo BOOLEAN NOT NULL DEFAULT FALSE
);

-- NOVA (sem quebrar nada): vínculo entre formulário e turma
CREATE TABLE formularios_turmas (
  id            SERIAL PRIMARY KEY,
  formulario_id BIGINT NOT NULL REFERENCES formularios(id) ON DELETE CASCADE,
  turma_id      INT    NOT NULL REFERENCES turmas(id)      ON DELETE CASCADE,
  UNIQUE (formulario_id, turma_id)
);

CREATE TABLE questoes (
  id            BIGSERIAL PRIMARY KEY,
  formulario_id BIGINT NOT NULL REFERENCES formularios(id) ON DELETE CASCADE,
  enunciado     TEXT NOT NULL,
  tipo          VARCHAR(10) NOT NULL,              -- ABERTA | UNICA | MULTIPLA
  obrigatoria   BOOLEAN NOT NULL DEFAULT FALSE,
  ordem         INT NOT NULL DEFAULT 1
);

CREATE TABLE alternativas (
  id         BIGSERIAL PRIMARY KEY,
  questao_id BIGINT NOT NULL REFERENCES questoes(id) ON DELETE CASCADE,
  texto      VARCHAR(255) NOT NULL,
  peso       INT NOT NULL DEFAULT 0,
  ordem      INT NOT NULL DEFAULT 1
);

CREATE TABLE avaliacoes (
  id             BIGSERIAL PRIMARY KEY,
  formulario_id  BIGINT NOT NULL REFERENCES formularios(id) ON DELETE CASCADE,
  usuario_id     BIGINT     REFERENCES usuarios(id),    -- NULL se anônimo
  data_submissao TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE respostas (
  id             BIGSERIAL PRIMARY KEY,
  avaliacao_id   BIGINT NOT NULL REFERENCES avaliacoes(id) ON DELETE CASCADE,
  questao_id     BIGINT NOT NULL REFERENCES questoes(id) ON DELETE CASCADE,
  texto          TEXT,          -- para ABERTA
  alternativa_id BIGINT         -- para UNICA/MULTIPLA (uma linha por alternativa) - mantido sem FK como no seu schema
);

CREATE TABLE auditoria_logs (
  id         BIGSERIAL PRIMARY KEY,
  data_hora  TIMESTAMPTZ DEFAULT NOW(),
  usuario_id BIGINT,
  acao       VARCHAR(100),
  recurso    VARCHAR(200),
  ip         VARCHAR(60),
  detalhes   TEXT
);

-- ======================================================================
-- ÍNDICES ÚTEIS
-- ======================================================================
CREATE INDEX IF NOT EXISTS idx_usuario_perfis_usuario ON usuario_perfis(usuario_id);
CREATE INDEX IF NOT EXISTS idx_disciplina_curso       ON disciplinas(curso_id);
CREATE INDEX IF NOT EXISTS idx_turma_disciplina       ON turmas(disciplina_id);
CREATE INDEX IF NOT EXISTS idx_tp_turma               ON turmas_professores(turma_id);
CREATE INDEX IF NOT EXISTS idx_ta_turma               ON turmas_alunos(turma_id);
CREATE INDEX IF NOT EXISTS idx_questoes_form          ON questoes(formulario_id);
CREATE INDEX IF NOT EXISTS idx_alts_questao           ON alternativas(questao_id);
CREATE INDEX IF NOT EXISTS idx_avaliacoes_form        ON avaliacoes(formulario_id);
CREATE INDEX IF NOT EXISTS idx_respostas_avaliacao    ON respostas(avaliacao_id);

-- NOVOS:
CREATE INDEX IF NOT EXISTS idx_form_turmas_form       ON formularios_turmas(formulario_id);
CREATE INDEX IF NOT EXISTS idx_form_turmas_turma      ON formularios_turmas(turma_id);

-- Garante 1 resposta por usuário identificado por formulário (não afeta anônimos)
CREATE UNIQUE INDEX IF NOT EXISTS ux_avaliacoes_form_usuario
  ON avaliacoes(formulario_id, usuario_id)
  WHERE usuario_id IS NOT NULL;

-- ======================================================================
-- SEEDS (sem psql \gset) usando CTEs com RETURNING
-- ======================================================================
WITH
-- Admin
admin_u AS (
  INSERT INTO usuarios (nome,email,login,senha_hash,ativo)
  VALUES ('Admin','admin@teste.com','admin','admin',TRUE)
  RETURNING id
),
admin_p AS (
  INSERT INTO usuario_perfis (usuario_id, perfil)
  SELECT id, 'ADMIN' FROM admin_u
  UNION ALL
  SELECT id, 'COORDENADOR' FROM admin_u
  RETURNING 1
),
-- Professor
prof_u AS (
  INSERT INTO usuarios (nome,email,login,senha_hash,ativo)
  VALUES ('Prof. Demo','prof@teste.com','prof','prof',TRUE)
  RETURNING id
),
prof_p AS (
  INSERT INTO usuario_perfis (usuario_id, perfil)
  SELECT id, 'PROFESSOR' FROM prof_u
  RETURNING 1
),
prof_tbl AS (
  INSERT INTO professores (usuario_id, registro, departamento)
  SELECT id, 'REG-001', 'Dep. X' FROM prof_u
  RETURNING 1
),
-- Aluno
aluno_u AS (
  INSERT INTO usuarios (nome,email,login,senha_hash,ativo)
  VALUES ('Aluno Demo','aluno@teste.com','aluno','aluno',TRUE)
  RETURNING id
),
aluno_p AS (
  INSERT INTO usuario_perfis (usuario_id, perfil)
  SELECT id, 'ALUNO' FROM aluno_u
  RETURNING 1
),
aluno_tbl AS (
  INSERT INTO alunos (usuario_id, matricula)
  SELECT id, '2024X0001' FROM aluno_u
  RETURNING 1
),
-- Curso/Disciplina/Turma
c AS (
  INSERT INTO cursos (nome, curriculo)
  VALUES ('TADS','Currículo TADS')
  RETURNING id
),
d AS (
  INSERT INTO disciplinas (curso_id,nome)
  SELECT id, 'LPOO-II' FROM cursos WHERE nome = 'TADS'
  RETURNING id
),
t AS (
  INSERT INTO turmas (disciplina_id, codigo, ano_semestre)
  SELECT id, 'LPOO2-01', '2025-2' FROM disciplinas WHERE nome = 'LPOO-II'
  RETURNING id
),
-- vínculos turma-professor / turma-aluno
tp AS (
  INSERT INTO turmas_professores (turma_id, professor_id)
  SELECT t.id, pu.id
  FROM t, prof_u pu
  RETURNING 1
),
ta AS (
  INSERT INTO turmas_alunos (turma_id, aluno_id)
  SELECT t.id, au.id
  FROM t, aluno_u au
  RETURNING 1
),
-- Formulário/questões/alternativas
f AS (
  INSERT INTO formularios (titulo,instrucoes,is_anonimo)
  VALUES ('Avaliação Exemplo - Disciplina X','Responda com sinceridade.', FALSE)
  RETURNING id
),
-- NOVO seed: vincula o formulário à(s) turma(s)
ft AS (
  INSERT INTO formularios_turmas (formulario_id, turma_id)
  SELECT f.id, t.id
  FROM f, t
  RETURNING 1
),
q1 AS (
  INSERT INTO questoes (formulario_id,enunciado,tipo,obrigatoria,ordem)
  SELECT id, 'O que você achou da disciplina?', 'ABERTA', FALSE, 1 FROM f
  RETURNING id
),
q2 AS (
  INSERT INTO questoes (formulario_id,enunciado,tipo,obrigatoria,ordem)
  SELECT id, 'Avalie a didática do professor', 'UNICA', TRUE, 2 FROM f
  RETURNING id
),
alts AS (
  INSERT INTO alternativas (questao_id,texto,peso,ordem)
  SELECT id,'Excelente',5,1 FROM q2
  UNION ALL SELECT id,'Bom',4,2 FROM q2
  UNION ALL SELECT id,'Regular',3,3 FROM q2
  UNION ALL SELECT id,'Ruim',1,4 FROM q2
  RETURNING 1
)
SELECT 1;
