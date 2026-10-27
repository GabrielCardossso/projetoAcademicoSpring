package com.trabalho0110.software.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trabalho0110.software.Entities.Atividade;
import com.trabalho0110.software.Enums.Prioridade;
import com.trabalho0110.software.Enums.SituacaoAtividade;

public interface AtividadeRepository extends JpaRepository<Atividade, Integer>{

    List<Atividade> findByAtivoTrue();

    List<Atividade> findByProjetoIdAndAtivoTrue(Integer projetoId);

    List<Atividade> findByProjetoIdAndSituacaoAtualAndAtivoTrue(Integer projetoId, SituacaoAtividade situacao);

    List<Atividade> findByProjetoIdAndPrioridadeAndAtivoTrue(Integer projetoId, Prioridade prioridade);

    List<Atividade> findByDesenvolvedorIdAndAtivoTrue(Integer desenvolvedorId);

    long countByProjetoIdAndAtivoTrue(Integer projetoId);

    long countByProjetoIdAndSituacaoAtualAndAtivoTrue(Integer projetoId, SituacaoAtividade situacao);

    // CONSULTA ESPECÍFICA: atividades atrasadas (prazo vencido e ainda não concluídas)
    // de projetos ativos, da mais urgente (prazo mais antigo) para a menos urgente.
    @Query("select a from Atividade a "
            + "where a.ativo = true and a.projeto.ativo = true "
            + "and a.situacaoAtual <> :concluida and a.dataFim < :hoje "
            + "order by a.dataFim asc")
    List<Atividade> findAtrasadas(@Param("concluida") SituacaoAtividade concluida,
                                  @Param("hoje") LocalDate hoje);
}
