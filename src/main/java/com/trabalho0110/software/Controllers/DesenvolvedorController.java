package com.trabalho0110.software.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trabalho0110.software.Entities.Desenvolvedor;
import com.trabalho0110.software.Services.DesenvolvedorService;

@RestController
@RequestMapping("/dev")
public class DesenvolvedorController {

    private final DesenvolvedorService devService;

    public DesenvolvedorController(DesenvolvedorService devService) {
        this.devService = devService;
    }

    // O e-mail e a senha são usados no login
    @PostMapping("/salvar")
    public String salvar(@RequestParam String nome, @RequestParam String cpf, @RequestParam String email,
                         @RequestParam String senha) {

        return devService.salvar(nome, cpf, email, senha);
    }

    @PutMapping("/atribuirEquipe")
    public String atribuirEquipe(@RequestParam Integer devId, @RequestParam Integer equipeId) {

        return devService.atribuirEquipe(devId, equipeId);
    }

    @PutMapping("/atribuirProjeto")
    public String atribuirProjeto(@RequestParam Integer devId, @RequestParam Integer projetoId) {

        return devService.atribuirProjeto(devId, projetoId);
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Integer id) {
        return devService.mostrar(id);
    }

    @GetMapping("/listar")
    public List<Desenvolvedor> listar() {
        return devService.listar();
    }
}
