/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public class Formulario implements Serializable {
    private Long id;
    private String titulo;
    private boolean anonimo;
    private String instrucoes;
    private LocalDateTime inicioColeta;
    private LocalDateTime fimColeta;

    private Long processoId;

    private Set<String> perfisDestino = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public boolean isAnonimo() { return anonimo; }
    public void setAnonimo(boolean anonimo) { this.anonimo = anonimo; }

    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }

    public LocalDateTime getInicioColeta() { return inicioColeta; }
    public void setInicioColeta(LocalDateTime inicioColeta) { this.inicioColeta = inicioColeta; }

    public LocalDateTime getFimColeta() { return fimColeta; }
    public void setFimColeta(LocalDateTime fimColeta) { this.fimColeta = fimColeta; }

    public Long getProcessoId() { return processoId; }
    public void setProcessoId(Long processoId) { this.processoId = processoId; }

    public Set<String> getPerfisDestino() { return perfisDestino; }
    public void setPerfisDestino(Set<String> perfisDestino) {
        this.perfisDestino = (perfisDestino == null) ? new LinkedHashSet<>() : perfisDestino;
    }
}
