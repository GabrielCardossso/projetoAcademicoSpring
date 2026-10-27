package com.trabalho0110.software.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trabalho0110.software.Enums.StatusProjeto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusProjeto status = StatusProjeto.EM_ANDAMENTO;

    // "Delete" lógico: o projeto nunca é apagado, apenas inativado
    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @OneToMany(mappedBy = "projeto")
    @JsonIgnore
    private List<Atividade> listaDeAtividades = new ArrayList<>();

    // Construtor vazio
    public Projeto() {
    }

    // Construtor
    public Projeto(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim, Equipe equipe) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.equipe = equipe;
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

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public List<Atividade> getListaDeAtividades() {
        return listaDeAtividades;
    }

    public void setListaDeAtividades(List<Atividade> listaDeAtividades) {
        this.listaDeAtividades = listaDeAtividades;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dados do Projeto de ID ").append(id)
                .append(":\nNome: ").append(nome)
                .append("\nDescrição: ").append(descricao)
                .append("\nInício: ").append(dataInicio)
                .append("\nFim: ").append(dataFim)
                .append("\nStatus: ").append(status)
                .append("\nAtivo: ").append(ativo ? "sim" : "não")
                .append("\nEquipe: ").append(equipe == null ? "sem equipe" : equipe.getNome())
                .append("\nAtividades:");

        boolean temAtividade = false;
        for (Atividade a : listaDeAtividades) {
            if (a.isAtivo()) {
                temAtividade = true;
                sb.append("\n - [").append(a.getId()).append("] ")
                        .append(a.getNome()).append(" (")
                        .append(a.getSituacaoAtual()).append(", prioridade ")
                        .append(a.getPrioridade()).append(")");
            }
        }
        if (!temAtividade) {
            sb.append(" nenhuma");
        }
        return sb.toString();
    }
}
