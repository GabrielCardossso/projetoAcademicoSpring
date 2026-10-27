package com.trabalho0110.software.Entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    @OneToOne
    @JoinColumn(name = "gestor_id")
    private GestorEquipe gestor;

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Projeto> listaDeProjetos = new ArrayList<>();

    @OneToMany(mappedBy = "equipe")
    @JsonIgnore
    private List<Desenvolvedor> listaDeDesenvolvedores = new ArrayList<>();

    // Construtor vazio
    public Equipe() {
    }

    // Construtor
    public Equipe(String nome) {
        this.nome = nome;
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

    public GestorEquipe getGestor() {
        return gestor;
    }

    public void setGestor(GestorEquipe gestor) {
        this.gestor = gestor;
    }

    public List<Projeto> getListaDeProjetos() {
        return listaDeProjetos;
    }

    public void setListaDeProjetos(List<Projeto> listaDeProjetos) {
        this.listaDeProjetos = listaDeProjetos;
    }

    public List<Desenvolvedor> getListaDeDesenvolvedores() {
        return listaDeDesenvolvedores;
    }

    public void setListaDeDesenvolvedores(List<Desenvolvedor> listaDeDesenvolvedores) {
        this.listaDeDesenvolvedores = listaDeDesenvolvedores;
    }

    @Override
    public String toString() {
        return "Dados da Equipe de ID " + id
                + ":\nNome: " + nome
                + "\nGestor: " + (gestor == null ? "sem gestor" : gestor.getNome())
                + "\nDesenvolvedores: " + listaDeDesenvolvedores.size()
                + "\nProjetos: " + listaDeProjetos.size();
    }
}
