package com.trabalho0110.software.Controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Enums.StatusProjeto;
import com.trabalho0110.software.Services.ProjetoService;

@RestController
@RequestMapping("/projeto")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    // Datas no formato yyyy-MM-dd. equipeId é opcional.
    @PostMapping("/salvar")
    public String salvar(@RequestParam String nome,
                         @RequestParam String descricao,
                         @RequestParam LocalDate dataInicio,
                         @RequestParam LocalDate dataFim,
                         @RequestParam(required = false) Integer equipeId) {

        return projetoService.salvar(nome, descricao, dataInicio, dataFim, equipeId);
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Integer id) {
        return projetoService.mostrar(id);
    }

    @GetMapping("/listar")
    public List<Projeto> listar() {
        return projetoService.listar();
    }

    @GetMapping("/listarPorStatus/{status}")
    public List<Projeto> listarPorStatus(@PathVariable StatusProjeto status) {
        return projetoService.listarPorStatus(status);
    }

    @GetMapping("/buscarPorNome")
    public List<Projeto> listarPorNome(@RequestParam String nome) {
        return projetoService.listarPorNome(nome);
    }

    @GetMapping("/listarPorEquipe/{equipeId}")
    public List<Projeto> listarPorEquipe(@PathVariable Integer equipeId) {
        return projetoService.listarPorEquipe(equipeId);
    }

    // Consulta específica: projetos que ainda têm atividades não concluídas
    @GetMapping("/comPendencias")
    public List<Projeto> listarComPendencias() {
        return projetoService.listarComPendencias();
    }

    // Consulta específica: progresso do projeto (concluídas / total)
    @GetMapping("/progresso/{id}")
    public String progresso(@PathVariable Integer id) {
        return projetoService.progresso(id);
    }

    @PutMapping("/atribuirEquipe")
    public String atribuirEquipe(@RequestParam Integer projetoId, @RequestParam Integer equipeId) {

        return projetoService.atribuirEquipe(projetoId, equipeId);
    }

    @PutMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id,
                            @RequestParam String nome,
                            @RequestParam String descricao,
                            @RequestParam LocalDate dataInicio,
                            @RequestParam LocalDate dataFim) {

        return projetoService.atualizar(id, nome, descricao, dataInicio, dataFim);
    }

    @PutMapping("/atualizarStatus/{id}")
    public String atualizarStatus(@PathVariable Integer id, @RequestParam StatusProjeto status) {

        return projetoService.atualizarStatus(id, status);
    }

    // Alterna o projeto entre ativo e inativo (nunca exclui)
    @PutMapping("/setStatus/{id}")
    public String setStatus(@PathVariable Integer id) {
        return projetoService.setStatus(id);
    }
}
