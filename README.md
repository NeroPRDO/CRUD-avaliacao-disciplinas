# AVA — Sistema de Avaliação de Disciplinas (UFPR · LPOO-II)

Aplicação web (Servlet/JSP + JDBC + PostgreSQL) para **gerenciar cursos/disciplinas/turmas** e **aplicar formulários de avaliação** com perfis **Admin/Coordenador/Professor/Aluno**.

---

## Funcionalidades

- **Autenticação** e controle por perfis (tabela `usuario_perfis`).
- CRUD administrativo:
  - **Usuários** (e perfis)
  - **Cursos**, **Disciplinas**, **Turmas**
  - **Vínculo** de alunos ↔ turmas e professores ↔ turmas
  - **Formulários**, questões e alternativas
  - **Vínculo** de formulários ↔ turmas
- Área do **Aluno**:
  - Listar formulários disponíveis (com base nas turmas matriculadas)
  - Responder avaliação (respostas em `respostas`)
- Área do **Professor**:
  - Visualizar distribuição de respostas das questões fechadas
- **Auditoria (Logs)** via tabela `auditoria_logs` (opcional via triggers)

---

## Stack / Tecnologias

- **Java 17**
- **Maven** (empacota `war`)
- **Jakarta Servlet/JSP + JSTL**
- **PostgreSQL** (JDBC)

---

## Como rodar localmente

### 1) Banco de dados

1. Crie um banco (ex.: `avaliacao`):
   ```sql
   CREATE DATABASE avaliacao;
   ```

2. Execute o script principal:
   - `db/schema.sql` (DDL + seeds)

3. (Opcional) Auditoria automática:
   - `db/trigger.sql` (cria trigger de auditoria em tabelas-chave)

> Observação: o trigger usa `current_setting('app.user_id', true)` e `current_setting('app.ip', true)` quando existirem.
> Se a aplicação não setar esses valores por conexão, os logs ainda podem ser criados, porém com `usuario_id`/`ip` nulos.

### 2) Configurar conexão

Edite:
- `src/main/resources/db.properties`

Exemplo (já vem no projeto):
```properties
db.url=jdbc:postgresql://localhost:5432/avaliacao
db.user=postgres
db.password=postgres
```

### 3) Build do WAR

Na raiz do projeto (`avaliacao-web/`):
```bash
mvn clean package
```

Gera:
- `target/avaliacao-web.war`

### 4) Deploy no Tomcat

Copie o WAR para:
- `$CATALINA_HOME/webapps/`

Inicie o Tomcat e acesse (depende do seu contexto):
- `http://localhost:8080/avaliacao-web/`

No NetBeans, você pode estar usando outro host/porta (ex.: `http://localhost:8282/`).

---

## Usuários de demonstração (seeds)

Criados pelo `db/schema.sql`:

- **Admin**: `admin / admin`
- **Professor**: `professor / prof`
- **Aluno**: `aluno / aluno`

> Projeto acadêmico: a coluna `senha_hash` está usando valor simples (não recomendado em produção).

---

## Onde o formulário é criado

O fluxo padrão é:

- **Endpoint**: `GET/POST /admin/formularios` (`FormularioServlet`)
- **Persistência**: `FormularioDAO.save(Formulario f)`
  - quando `f.getId() == null` → faz `INSERT` e retorna o `id`
  - quando `f.getId() != null` → faz `UPDATE`

---

## Modelagem (visão geral)

Entidades principais:

- `usuarios` + `usuario_perfis`
- `cursos` → `disciplinas` → `turmas`
- `alunos` / `professores` (tabelas auxiliares vinculadas ao `usuarios.id`)
- `turmas_alunos` e `turmas_professores` (N:N)
- `formularios` → `questoes` → `alternativas`
- `formularios_turmas` (N:N) para disponibilizar o formulário por turma
- `avaliacoes` e `respostas` para armazenamento das respostas
- `auditoria_logs` (opcional) para trilha de auditoria

---

## Estrutura do projeto

- `src/main/java/br/ufpr/avaliacao/controller` → Servlets (rotas)
- `src/main/java/br/ufpr/avaliacao/repository` → DAOs / ConnectionFactory
- `src/main/java/br/ufpr/avaliacao/model` → Modelos
- `src/main/webapp/WEB-INF/views` → JSPs
- `db/schema.sql` → DDL + seeds
- `db/trigger.sql` → triggers de auditoria (opcional)

---

## Dica para publicar no GitHub

Crie um `.gitignore` para ignorar:
- `target/`
- `nbproject/private/`
- `.idea/`, `.vscode/`
- `*.log`

---

## Autor

- Pedro Eduardo Dall’Agnol — UFPR (TADS) — LPOO-II
