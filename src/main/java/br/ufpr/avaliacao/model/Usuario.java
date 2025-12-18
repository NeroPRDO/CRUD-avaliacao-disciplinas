/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senhaHash;
    private Boolean ativo; // <- precisa de getAtivo()/setAtivo()
    private EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public EnumSet<Perfil> getPerfis() { return perfis; }
    public void setPerfis(EnumSet<Perfil> perfis) {
        this.perfis = (perfis == null ? EnumSet.noneOf(Perfil.class) : perfis);
    }

    // ===== Conveniências para EL =====
    public boolean isAluno() { return perfis.contains(Perfil.ALUNO); }
    public boolean isProfessor() { return perfis.contains(Perfil.PROFESSOR); }
    public boolean isCoordenador() { return perfis.contains(Perfil.COORDENADOR); }
    public boolean isAdmin() { return perfis.contains(Perfil.ADMIN); }

    // String amigável para mostrar na lista
    public String getPerfisDescricao() {
        if (perfis == null || perfis.isEmpty()) return "—";
        List<String> out = new ArrayList<>();
        for (Perfil p : perfis) {
            switch (p) {
                case ALUNO -> out.add("Aluno");
                case PROFESSOR -> out.add("Professor");
                case COORDENADOR -> out.add("Coordenador");
                case ADMIN -> out.add("Admin");
            }
        }
        return String.join(", ", out);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", ativo=" + ativo +
                ", perfis=" + perfis +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
