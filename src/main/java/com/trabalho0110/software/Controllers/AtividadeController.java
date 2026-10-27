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

import com.trabalho0110.software.Entities.Atividade;
import com.trabalho0110.software.Enums.Prioridade;
import com.trabalho0110.software.Enums.SituacaoAtividade;
import com.trabalho0110.software.Services.AtividadeService;

@RestController
@RequestMapping("/atividade")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    // Datas no formato yyyy-MM-dd. projetoId é obrigatório.
    @PostMapping("/salvar")
    public String salvar(@RequestParam Integer projetoId,
                         @RequestParam String nome,
                         @RequestParam String descricao,
                         @RequestParam Prioridade prioridade,
                         @RequestParam LocalDate dataInicio,
                         @RequestParam LocalDate dataFim) {

        return atividadeService.salvar(projetoId, nome, descricao, prioridade, dataInicio, dataFim);
    }

    // O próprio dev pode assumir a atividade passando o seu id
    @PutMapping("/atribuirDesenvolvedor")
    public String atribuirDesenvolvedor(@RequestParam Integer atividadeId, @RequestParam Integer devId) {

        return atividadeService.atribuirDesenvolvedor(atividadeId, devId);
    }

    @PutMapping("/atualizarStatus/{id}")
    public String atualizarStatus(@PathVariable Integer id, @RequestParam SituacaoAtividade situacao) {

        return atividadeService.atualizarStatus(id, situacao);
    }

    @PutMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id,
                            @RequestParam String nome,
                            @RequestParam String descricao,
                            @RequestParam Prioridade prioridade,
                            @RequestParam LocalDate dataInicio,
                            @RequestParam LocalDate dataFim) {

        return atividadeService.atualizar(id, nome, descricao, prioridade, dataInicio, dataFim);
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Integer id) {
        return atividadeService.mostrar(id);
    }

    @GetMapping("/listar")
    public List<Atividade> listar() {
        return atividadeService.listar();
    }

    // Consulta específica: atividades atrasadas, da maior prioridade para a menor
    @GetMapping("/atrasadas")
    public List<Atividade> listarAtrasadas() {
        return atividadeService.listarAtrasadas();
    }

    @GetMapping("/listarPorDesenvolvedor/{devId}")
    public List<Atividade> listarPorDesenvolvedor(@PathVariable Integer devId) {
        return atividadeService.listarPorDesenvolvedor(devId);
    }

    @GetMapping("/listarPorProjeto/{projetoId}")
    public List<Atividade> listarPorProjeto(@PathVariable Integer projetoId) {
        return atividadeService.listarPorProjeto(projetoId);
    }

    @GetMapping("/listarPorProjeto/{projetoId}/situacao/{situacao}")
    public List<Atividade> listarPorSituacao(@PathVariable Integer projetoId,
                                             @PathVariable SituacaoAtividade situacao) {
        return atividadeService.listarPorProjetoESituacao(projetoId, situacao);
    }

    @GetMapping("/listarPorProjeto/{projetoId}/prioridade/{prioridade}")
    public List<Atividade> listarPorPrioridade(@PathVariable Integer projetoId,
                                               @PathVariable Prioridade prioridade) {
        return atividadeService.listarPorProjetoEPrioridade(projetoId, prioridade);
    }

    // Alterna a atividade entre ativa e inativa (nunca exclui)
    @PutMapping("/setStatus/{id}")
    public String setStatus(@PathVariable Integer id) {
        return atividadeService.setStatus(id);
    }
}
