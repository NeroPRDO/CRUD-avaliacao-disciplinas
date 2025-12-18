/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.model;

/**
 * @author Pedro
 */

import java.sql.Timestamp;

public class AuditoriaLog {
    private Long id;
    private Timestamp momento;     // mapeia data_hora
    private Long usuarioId;
    private String usuarioNome;    // join com usuarios (exibição)
    private String acao;
    private String tabela;         // mapeia recurso
    private Long registroId;       // (não existe na tabela -> pode ficar null)
    private String detalhes;
    private String ip;             // NOVO: corresponde à coluna ip

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Timestamp getMomento() { return momento; }
    public void setMomento(Timestamp momento) { this.momento = momento; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getTabela() { return tabela; }
    public void setTabela(String tabela) { this.tabela = tabela; }

    public Long getRegistroId() { return registroId; }
    public void setRegistroId(Long registroId) { this.registroId = registroId; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
