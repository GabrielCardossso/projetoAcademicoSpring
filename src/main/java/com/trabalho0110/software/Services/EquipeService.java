package com.trabalho0110.software.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trabalho0110.software.Entities.Equipe;
import com.trabalho0110.software.Entities.GestorEquipe;
import com.trabalho0110.software.Repositories.EquipeRepository;
import com.trabalho0110.software.Repositories.GestorEquipeRepository;

@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;
    private final GestorEquipeRepository gestorRepository;

    public EquipeService(EquipeRepository equipeRepository, GestorEquipeRepository gestorRepository) {
        this.equipeRepository = equipeRepository;
        this.gestorRepository = gestorRepository;
    }

    // POST equipe
    public String salvar(String nome) {

        if (nome == null || nome.isBlank()) {
            return "O nome da equipe é obrigatório!";
        }

        Equipe equipe = new Equipe(nome);

        equipeRepository.save(equipe);

        return "Equipe salva com sucesso!";
    }

    // PUT equipe (atribuirGestor)
    public String atribuirGestor(Integer equipeId, Integer gestorId) {

        Optional<Equipe> equipe = equipeRepository.findById(equipeId);
        if (equipe.isEmpty()) {
            return "Equipe não encontrada!";
        }

        Optional<GestorEquipe> gestor = gestorRepository.findById(gestorId);
        if (gestor.isEmpty()) {
            return "Gestor não encontrado!";
        }

        // Um gestor gerencia somente uma equipe
        Equipe atual = gestor.get().getEquipeAtribuida();
        if (atual != null && !atual.getId().equals(equipeId)) {
            return "Este gestor já está atribuído à equipe '" + atual.getNome() + "'!";
        }

        equipe.get().setGestor(gestor.get());

        equipeRepository.save(equipe.get());

        return "Gestor atribuído à equipe com sucesso!";
    }

    public String mostrar(Integer id) {

        Optional<Equipe> equipe = equipeRepository.findById(id);

        if (equipe.isEmpty()) {
            return "Equipe não encontrada!";
        }

        return equipe.get().toString();
    }

    public List<Equipe> listar() {
        return equipeRepository.findAll();
    }
}
