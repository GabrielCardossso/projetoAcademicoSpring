package com.trabalho0110.software.Entities;

import java.time.LocalDate;

import com.trabalho0110.software.Enums.Prioridade;
import com.trabalho0110.software.Enums.SituacaoAtividade;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private SituacaoAtividade situacaoAtual = SituacaoAtividade.PENDENTE;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    // "Delete" lógico: a atividade nunca é apagada, apenas inativada
    private boolean ativo = true;

    // Atividade não existe sem projeto
    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    // Responsável pela atividade
    @ManyToOne
    @JoinColumn(name = "desenvolvedor_id")
    private Desenvolvedor desenvolvedor;

    // Construtor vazio
    public Atividade() {
    }

    // Construtor
    public Atividade(String nome, String descricao, Prioridade prioridade,
                     LocalDate dataInicio, LocalDate dataFim, Projeto projeto) {
        this.nome = nome;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.projeto = projeto;
    }

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SituacaoAtividade getSituacaoAtual() {
        return situacaoAtual;
    }

    public void setSituacaoAtual(SituacaoAtividade situacaoAtual) {
        this.situacaoAtual = situacaoAtual;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Desenvolvedor getDesenvolvedor() {
        return desenvolvedor;
    }

    public void setDesenvolvedor(Desenvolvedor desenvolvedor) {
        this.desenvolvedor = desenvolvedor;
    }

    @Override
    public String toString() {
        return "Dados da Atividade de ID " + id
                + ":\nNome: " + nome
                + "\nDescrição: " + descricao
                + "\nSituação: " + situacaoAtual
                + "\nPrioridade: " + prioridade
                + "\nInício: " + dataInicio
                + "\nFim: " + dataFim
                + "\nAtiva: " + (ativo ? "sim" : "não")
                + "\nProjeto: " + (projeto == null ? "-" : projeto.getNome())
                + "\nResponsável: " + (desenvolvedor == null ? "sem responsável" : desenvolvedor.getNome());
    }
}
