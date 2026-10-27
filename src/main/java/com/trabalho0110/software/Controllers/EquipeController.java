package com.trabalho0110.software.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trabalho0110.software.Entities.Equipe;
import com.trabalho0110.software.Services.EquipeService;

@RestController
@RequestMapping("/equipe")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam String nome) {

        return equipeService.salvar(nome);
    }

    @PutMapping("/atribuirGestor")
    public String atribuirGestor(@RequestParam Integer equipeId, @RequestParam Integer gestorId) {

        return equipeService.atribuirGestor(equipeId, gestorId);
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Integer id) {
        return equipeService.mostrar(id);
    }

    @GetMapping("/listar")
    public List<Equipe> listar() {
        return equipeService.listar();
    }
}
