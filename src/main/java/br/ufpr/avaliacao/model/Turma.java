/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro
 */

public class Turma {
    private Long id;
    private Long disciplinaId;   // FK para Disciplina
    private String codigo;       // ex.: "ED1-A"
    private String anoSemestre;  // ex.: "2025-1"
    private Integer numeroVagas; // opcional no protótipo
    private String status;       // opcional (ex.: ATIVA/INATIVA)

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDisciplinaId() { return disciplinaId; }
    public void setDisciplinaId(Long disciplinaId) { this.disciplinaId = disciplinaId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getAnoSemestre() { return anoSemestre; }
    public void setAnoSemestre(String anoSemestre) { this.anoSemestre = anoSemestre; }

    public Integer getNumeroVagas() { return numeroVagas; }
    public void setNumeroVagas(Integer numeroVagas) { this.numeroVagas = numeroVagas; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
