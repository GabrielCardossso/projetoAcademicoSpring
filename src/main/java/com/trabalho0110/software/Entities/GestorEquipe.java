package com.trabalho0110.software.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class GestorEquipe extends Usuario {

    // Lado inverso: a chave estrangeira fica em Equipe.gestor
    @OneToOne(mappedBy = "gestor")
    @JsonIgnore
    private Equipe equipeAtribuida;

    // Construtor vazio
    public GestorEquipe() {
    }

    // Construtor
    public GestorEquipe(String nome, String cpf, String email) {
        super(nome, cpf, email);
    }

    // Getters e Setters

    public Equipe getEquipeAtribuida() {
        return equipeAtribuida;
    }

    public void setEquipeAtribuida(Equipe equipeAtribuida) {
        this.equipeAtribuida = equipeAtribuida;
    }
}
