/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro
 */

import java.io.Serializable;

public class PesoNivel implements Serializable {

    public enum Nivel { CURSO, DISCIPLINA, TURMA, PROFESSOR }

    private Long id;
    private Nivel nivel;
    private Long referenciaId; // id do curso, disciplina, turma ou professor (usuarioId)
    private Long questaoId;
    private int peso; 

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Nivel getNivel() { return nivel; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public Long getQuestaoId() { return questaoId; }
    public void setQuestaoId(Long questaoId) { this.questaoId = questaoId; }

    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
}
