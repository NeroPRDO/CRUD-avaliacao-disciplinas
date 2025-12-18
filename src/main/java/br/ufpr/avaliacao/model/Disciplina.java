/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Disciplina {

    private Integer id;
    private Integer cursoId; 
    private String  nome;

    // ====== Getters/Setters ======
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCursoId() { return cursoId; }
    public void setCursoId(Integer cursoId) { this.cursoId = cursoId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
