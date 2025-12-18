/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 *
 * @author Pedro, Gabi
 */

public class Resposta {
    private Long id;             // respostas.id (BIGSERIAL)
    private Long avaliacaoId;    // FK -> avaliacoes.id
    private Long questaoId;      // FK -> questoes.id
    private String textoResposta; // para ABERTA
    private Long alternativaId;   // para UNICA/MULTIPLA (1 linha por alternativa)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAvaliacaoId() { return avaliacaoId; }
    public void setAvaliacaoId(Long avaliacaoId) { this.avaliacaoId = avaliacaoId; }

    public Long getQuestaoId() { return questaoId; }
    public void setQuestaoId(Long questaoId) { this.questaoId = questaoId; }

    public String getTextoResposta() { return textoResposta; }
    public void setTextoResposta(String textoResposta) { this.textoResposta = textoResposta; }

    public Long getAlternativaId() { return alternativaId; }
    public void setAlternativaId(Long alternativaId) { this.alternativaId = alternativaId; }
}
