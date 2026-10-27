package com.trabalho0110.software.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trabalho0110.software.Entities.GestorEquipe;
import com.trabalho0110.software.Services.GestorEquipeService;

@RestController
@RequestMapping("/gestor")
public class GestorEquipeController {

    private final GestorEquipeService gestorService;

    public GestorEquipeController(GestorEquipeService gestorService) {
        this.gestorService = gestorService;
    }

    // O e-mail e a senha são usados no login
    @PostMapping("/salvar")
    public String salvar(@RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
                         @RequestParam String senha) {

        return gestorService.salvar(nome, cpf, email, senha);
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Integer id) {
        return gestorService.mostrar(id);
    }

    @GetMapping("/listar")
    public List<GestorEquipe> listar() {
        return gestorService.listar();
    }
}
