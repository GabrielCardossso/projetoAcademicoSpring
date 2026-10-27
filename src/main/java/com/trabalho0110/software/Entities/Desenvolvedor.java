package com.trabalho0110.software.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Desenvolvedor extends Usuario {

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "projeto_atribuido_id")
    private Projeto projetoAtribuido;

    // Construtor vazio
    public Desenvolvedor() {
    }

    // Construtor
    public Desenvolvedor(String nome, String cpf, String email) {
        super(nome, cpf, email);
    }

    // Getters e Setters

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Projeto getProjetoAtribuido() {
        return projetoAtribuido;
    }

    public void setProjetoAtribuido(Projeto projetoAtribuido) {
        this.projetoAtribuido = projetoAtribuido;
    }
}
