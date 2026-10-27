package com.trabalho0110.software.Services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.trabalho0110.software.Entities.Atividade;
import com.trabalho0110.software.Entities.Desenvolvedor;
import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Enums.Prioridade;
import com.trabalho0110.software.Enums.SituacaoAtividade;
import com.trabalho0110.software.Enums.StatusProjeto;
import com.trabalho0110.software.Repositories.AtividadeRepository;
import com.trabalho0110.software.Repositories.DesenvolvedorRepository;
import com.trabalho0110.software.Repositories.ProjetoRepository;

@Service
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final ProjetoRepository projetoRepository;
    private final DesenvolvedorRepository devRepository;

    public AtividadeService(AtividadeRepository atividadeRepository,
                            ProjetoRepository projetoRepository,
                            DesenvolvedorRepository devRepository) {
        this.atividadeRepository = atividadeRepository;
        this.projetoRepository = projetoRepository;
        this.devRepository = devRepository;
    }

    // POST atividade (obrigatoriamente vinculada a um projeto; infos obrigatórias)
    public String salvar(Integer projetoId, String nome, String descricao, Prioridade prioridade,
                         LocalDate dataInicio, LocalDate dataFim) {

        if (projetoId == null) {
            return "A atividade deve estar vinculada a um projeto!";
        }

        Optional<Projeto> projetoBD = projetoRepository.findById(projetoId);
        if (projetoBD.isEmpty()) {
            return "Projeto não encontrado!";
        }
        Projeto projeto = projetoBD.get();

        String erro = validarProjetoEditavel(projeto);
        if (erro == null) {
            erro = validar(nome, descricao, prioridade, dataInicio, dataFim, projeto);
        }
        if (erro != null) {
            return erro;
        }

        Atividade atividade = new Atividade(nome, descricao, prioridade, dataInicio, dataFim, projeto);

        atividadeRepository.save(atividade);

        return "Atividade salva com sucesso!";
    }

    // PUT atividade (atribuirDesenvolvedor) — também serve para o dev assumir a atividade para ele
    public String atribuirDesenvolvedor(Integer atividadeId, Integer devId) {

        Optional<Atividade> atividadeBD = atividadeRepository.findById(atividadeId);
        if (atividadeBD.isEmpty()) {
            return "Atividade não encontrada!";
        }
        Atividade atividade = atividadeBD.get();

        String erro = validarAtividadeEditavel(atividade);
        if (erro != null) {
            return erro;
        }

        Optional<Desenvolvedor> devBD = devRepository.findById(devId);
        if (devBD.isEmpty()) {
            return "Desenvolvedor não encontrado!";
        }
        Desenvolvedor dev = devBD.get();

        // Se o projeto pertence a uma equipe, o desenvolvedor precisa ser dessa equipe
        Projeto projeto = atividade.getProjeto();
        if (projeto.getEquipe() != null
                && (dev.getEquipe() == null || !dev.getEquipe().getId().equals(projeto.getEquipe().getId()))) {
            return "O desenvolvedor não pertence à equipe responsável pelo projeto!";
        }

        atividade.setDesenvolvedor(dev);
        dev.setProjetoAtribuido(projeto);

        devRepository.save(dev);
        atividadeRepository.save(atividade);

        return "Atividade atribuída ao desenvolvedor " + dev.getNome() + " com sucesso!";
    }

    // PUT atividade (status)
    public String atualizarStatus(Integer id, SituacaoAtividade novaSituacao) {

        Optional<Atividade> atividadeBD = atividadeRepository.findById(id);
        if (atividadeBD.isEmpty()) {
            return "Atividade não encontrada!";
        }
        Atividade atividade = atividadeBD.get();

        String erro = validarAtividadeEditavel(atividade);
        if (erro != null) {
            return erro;
        }

        atividade.setSituacaoAtual(novaSituacao);

        atividadeRepository.save(atividade);

        return "Situação da atividade atualizada para " + novaSituacao + "!";
    }

    // PUT atividade (infos)
    public String atualizar(Integer id, String nome, String descricao, Prioridade prioridade,
                            LocalDate dataInicio, LocalDate dataFim) {

        Optional<Atividade> atividadeBD = atividadeRepository.findById(id);
        if (atividadeBD.isEmpty()) {
            return "Atividade não encontrada!";
        }
        Atividade atividade = atividadeBD.get();

        String erro = validarAtividadeEditavel(atividade);
        if (erro == null) {
            erro = validar(nome, descricao, prioridade, dataInicio, dataFim, atividade.getProjeto());
        }
        if (erro != null) {
            return erro;
        }

        atividade.setNome(nome);
        atividade.setDescricao(descricao);
        atividade.setPrioridade(prioridade);
        atividade.setDataInicio(dataInicio);
        atividade.setDataFim(dataFim);

        atividadeRepository.save(atividade);

        return "Atividade atualizada com sucesso!";
    }

    // GET atividade (exibir apenas uma, com detalhes)
    public String mostrar(Integer id) {

        Optional<Atividade> atividade = atividadeRepository.findById(id);

        if (atividade.isEmpty()) {
            return "Atividade não encontrada!";
        }

        return atividade.get().toString();
    }

    // GET todas as atividades ativas
    public List<Atividade> listar() {
        return atividadeRepository.findByAtivoTrue();
    }

    // GET atividades de um projeto
    public List<Atividade> listarPorProjeto(Integer projetoId) {
        return atividadeRepository.findByProjetoIdAndAtivoTrue(projetoId);
    }

    // GET atividades de um projeto filtrando por situação (pendentes, concluídas...)
    public List<Atividade> listarPorProjetoESituacao(Integer projetoId, SituacaoAtividade situacao) {
        return atividadeRepository.findByProjetoIdAndSituacaoAtualAndAtivoTrue(projetoId, situacao);
    }

    // GET atividades de um projeto filtrando por prioridade
    public List<Atividade> listarPorProjetoEPrioridade(Integer projetoId, Prioridade prioridade) {
        return atividadeRepository.findByProjetoIdAndPrioridadeAndAtivoTrue(projetoId, prioridade);
    }

    // GET atividades sob responsabilidade de um desenvolvedor
    public List<Atividade> listarPorDesenvolvedor(Integer devId) {
        return atividadeRepository.findByDesenvolvedorIdAndAtivoTrue(devId);
    }

    // CONSULTA ESPECÍFICA: atividades atrasadas (prazo vencido e não concluídas),
    // da maior prioridade para a menor e, em empate, do prazo mais antigo.
    public List<Atividade> listarAtrasadas() {
        return atividadeRepository.findAtrasadas(SituacaoAtividade.CONCLUIDA, LocalDate.now()).stream()
                .sorted(Comparator
                        .comparing(Atividade::getPrioridade, Comparator.reverseOrder())
                        .thenComparing(Atividade::getDataFim))
                .toList();
    }

    // PUT setStatus atividade: alterna entre ativa e inativa a cada chamada
    public String setStatus(Integer id) {

        Optional<Atividade> atividadeBD = atividadeRepository.findById(id);
        if (atividadeBD.isEmpty()) {
            return "Atividade não encontrada!";
        }
        Atividade atividade = atividadeBD.get();

        // Em qualquer sentido, o projeto precisa estar ativo e não concluído
        String erro = validarProjetoEditavel(atividade.getProjeto());
        if (erro != null) {
            return erro;
        }

        if (atividade.isAtivo()) {

            if (atividade.getSituacaoAtual() == SituacaoAtividade.EM_ANDAMENTO) {
                return "Não é possível inativar uma atividade em andamento!";
            }

            atividade.setAtivo(false);
            atividadeRepository.save(atividade);

            return "Atividade inativada com sucesso!";
        }

        atividade.setAtivo(true);
        atividadeRepository.save(atividade);

        return "Atividade reativada com sucesso!";
    }

    // ---------- auxiliares (retornam a mensagem de erro ou null) ----------

    private String validarProjetoEditavel(Projeto projeto) {
        if (!projeto.isAtivo()) {
            return "O projeto está inativo!";
        }
        if (projeto.getStatus() == StatusProjeto.CONCLUIDO) {
            return "O projeto está concluído e não aceita mudanças nas atividades!";
        }
        return null;
    }

    private String validarAtividadeEditavel(Atividade atividade) {
        if (!atividade.isAtivo()) {
            return "A atividade está inativa!";
        }
        return validarProjetoEditavel(atividade.getProjeto());
    }

    private String validar(String nome, String descricao, Prioridade prioridade,
                           LocalDate dataInicio, LocalDate dataFim, Projeto projeto) {

        if (nome == null || nome.isBlank()) {
            return "O nome da atividade é obrigatório!";
        }
        if (descricao == null || descricao.isBlank()) {
            return "A descrição da atividade é obrigatória!";
        }
        if (prioridade == null) {
            return "A prioridade da atividade é obrigatória!";
        }
        if (dataInicio == null || dataFim == null) {
            return "As datas de início e fim são obrigatórias!";
        }
        if (dataFim.isBefore(dataInicio)) {
            return "A data de fim não pode ser anterior à data de início!";
        }
        if (dataFim.isBefore(LocalDate.now())) {
            return "A data de fim não pode estar no passado!";
        }
        // A atividade precisa caber no prazo do projeto
        if (dataInicio.isBefore(projeto.getDataInicio()) || dataFim.isAfter(projeto.getDataFim())) {
            return "As datas da atividade devem estar dentro do período do projeto ("
                    + projeto.getDataInicio() + " a " + projeto.getDataFim() + ")!";
        }
        return null;
    }
}
