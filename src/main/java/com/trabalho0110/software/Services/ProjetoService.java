package com.trabalho0110.software.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trabalho0110.software.Entities.Atividade;
import com.trabalho0110.software.Entities.Equipe;
import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Enums.SituacaoAtividade;
import com.trabalho0110.software.Enums.StatusProjeto;
import com.trabalho0110.software.Repositories.EquipeRepository;
import com.trabalho0110.software.Repositories.ProjetoRepository;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final EquipeRepository equipeRepository;

    public ProjetoService(ProjetoRepository projetoRepository, EquipeRepository equipeRepository) {
        this.projetoRepository = projetoRepository;
        this.equipeRepository = equipeRepository;
    }

    // POST projeto (infos obrigatórias). equipeId é opcional.
    public String salvar(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim, Integer equipeId) {

        String erro = validar(nome, descricao, dataInicio, dataFim);
        if (erro != null) {
            return erro;
        }

        if (dataFim.isBefore(LocalDate.now())) {
            return "A data de fim não pode estar no passado!";
        }

        Equipe equipe = null;
        if (equipeId != null) {
            Optional<Equipe> equipeBD = equipeRepository.findById(equipeId);
            if (equipeBD.isEmpty()) {
                return "Equipe não encontrada!";
            }
            equipe = equipeBD.get();
        }

        Projeto projeto = new Projeto(nome, descricao, dataInicio, dataFim, equipe);

        projetoRepository.save(projeto);

        return "Projeto salvo com sucesso!";
    }

    // GET projeto (exibir apenas um, com detalhes)
    public String mostrar(Integer id) {

        Optional<Projeto> projeto = projetoRepository.findById(id);

        if (projeto.isEmpty()) {
            return "Projeto não encontrado!";
        }

        return projeto.get().toString();
    }

    // GET projeto (listar os projetos ativos: nome e status)
    public List<Projeto> listar() {
        return projetoRepository.findByAtivoTrue();
    }

    // GET projeto filtrando por status
    public List<Projeto> listarPorStatus(StatusProjeto status) {
        return projetoRepository.findByAtivoTrueAndStatus(status);
    }

    // GET projeto filtrando por nome (contém, sem diferenciar maiúsculas)
    public List<Projeto> listarPorNome(String nome) {
        return projetoRepository.findByAtivoTrueAndNomeContainingIgnoreCase(nome == null ? "" : nome);
    }

    // GET projeto filtrando por equipe
    public List<Projeto> listarPorEquipe(Integer equipeId) {
        return projetoRepository.findByAtivoTrueAndEquipeId(equipeId);
    }

    // CONSULTA ESPECÍFICA: projetos que ainda possuem atividades não concluídas
    public List<Projeto> listarComPendencias() {
        return projetoRepository.findComAtividadesNaoConcluidas(SituacaoAtividade.CONCLUIDA);
    }

    // CONSULTA ESPECÍFICA: progresso do projeto (atividades concluídas / total)
    public String progresso(Integer id) {

        Optional<Projeto> projeto = projetoRepository.findById(id);
        if (projeto.isEmpty()) {
            return "Projeto não encontrado!";
        }

        long total = projeto.get().getListaDeAtividades().stream().filter(Atividade::isAtivo).count();
        long concluidas = projeto.get().getListaDeAtividades().stream()
                .filter(Atividade::isAtivo)
                .filter(a -> a.getSituacaoAtual() == SituacaoAtividade.CONCLUIDA)
                .count();

        if (total == 0) {
            return "Projeto '" + projeto.get().getNome() + "' ainda não possui atividades.";
        }

        return "Projeto '" + projeto.get().getNome() + "': " + concluidas + " de " + total
                + " atividades concluídas (" + (concluidas * 100 / total) + "%).";
    }

    // PUT projeto (atribuirEquipe)
    public String atribuirEquipe(Integer projetoId, Integer equipeId) {

        Optional<Projeto> projeto = projetoRepository.findById(projetoId);
        if (projeto.isEmpty()) {
            return "Projeto não encontrado!";
        }
        if (!projeto.get().isAtivo()) {
            return "O projeto está inativo!";
        }

        Optional<Equipe> equipe = equipeRepository.findById(equipeId);
        if (equipe.isEmpty()) {
            return "Equipe não encontrada!";
        }

        projeto.get().setEquipe(equipe.get());

        projetoRepository.save(projeto.get());

        return "Projeto atribuído à equipe '" + equipe.get().getNome() + "' com sucesso!";
    }

    // PUT projeto (infos)
    public String atualizar(Integer id, String nome, String descricao, LocalDate dataInicio, LocalDate dataFim) {

        Optional<Projeto> projetoBD = projetoRepository.findById(id);
        if (projetoBD.isEmpty()) {
            return "Projeto não encontrado!";
        }
        Projeto projeto = projetoBD.get();

        if (!projeto.isAtivo()) {
            return "O projeto está inativo!";
        }

        String erro = validar(nome, descricao, dataInicio, dataFim);
        if (erro != null) {
            return erro;
        }

        // O novo período precisa continuar contendo as atividades já cadastradas
        for (Atividade a : projeto.getListaDeAtividades()) {
            if (a.isAtivo() && (a.getDataInicio().isBefore(dataInicio) || a.getDataFim().isAfter(dataFim))) {
                return "As datas do projeto não comportam a atividade '" + a.getNome() + "'!";
            }
        }

        projeto.setNome(nome);
        projeto.setDescricao(descricao);
        projeto.setDataInicio(dataInicio);
        projeto.setDataFim(dataFim);

        projetoRepository.save(projeto);

        return "Projeto atualizado com sucesso!";
    }

    // PUT projeto (status): só finaliza se todas as atividades estiverem concluídas
    public String atualizarStatus(Integer id, StatusProjeto novoStatus) {

        Optional<Projeto> projetoBD = projetoRepository.findById(id);
        if (projetoBD.isEmpty()) {
            return "Projeto não encontrado!";
        }
        Projeto projeto = projetoBD.get();

        if (!projeto.isAtivo()) {
            return "O projeto está inativo!";
        }

        if (novoStatus == StatusProjeto.CONCLUIDO) {

            List<Atividade> atividades = projeto.getListaDeAtividades().stream()
                    .filter(Atividade::isAtivo)
                    .toList();

            if (atividades.isEmpty()) {
                return "Não é possível concluir um projeto sem atividades!";
            }

            boolean temPendente = atividades.stream()
                    .anyMatch(a -> a.getSituacaoAtual() != SituacaoAtividade.CONCLUIDA);

            if (temPendente) {
                return "Não é possível concluir o projeto: existem atividades não concluídas!";
            }
        }

        projeto.setStatus(novoStatus);

        projetoRepository.save(projeto);

        return "Status do projeto atualizado para " + novoStatus + "!";
    }

    // PUT setStatus projeto: alterna entre ativo e inativo a cada chamada
    public String setStatus(Integer id) {

        Optional<Projeto> projetoBD = projetoRepository.findById(id);
        if (projetoBD.isEmpty()) {
            return "Projeto não encontrado!";
        }
        Projeto projeto = projetoBD.get();

        if (projeto.isAtivo()) {

            boolean emAndamento = projeto.getListaDeAtividades().stream()
                    .filter(Atividade::isAtivo)
                    .anyMatch(a -> a.getSituacaoAtual() == SituacaoAtividade.EM_ANDAMENTO);

            if (emAndamento) {
                return "Não é possível inativar o projeto: existem atividades em andamento!";
            }

            projeto.setAtivo(false);
            projetoRepository.save(projeto);

            return "Projeto inativado com sucesso!";
        }

        projeto.setAtivo(true);
        projetoRepository.save(projeto);

        return "Projeto reativado com sucesso!";
    }

    // Retorna a mensagem de erro, ou null se estiver tudo certo
    private String validar(String nome, String descricao, LocalDate dataInicio, LocalDate dataFim) {

        if (nome == null || nome.isBlank()) {
            return "O nome do projeto é obrigatório!";
        }
        if (descricao == null || descricao.isBlank()) {
            return "A descrição do projeto é obrigatória!";
        }
        if (dataInicio == null || dataFim == null) {
            return "As datas de início e fim são obrigatórias!";
        }
        if (dataFim.isBefore(dataInicio)) {
            return "A data de fim não pode ser anterior à data de início!";
        }
        return null;
    }
}
