package com.trabalho0110.software.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Enums.SituacaoAtividade;
import com.trabalho0110.software.Enums.StatusProjeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Integer>{

    List<Projeto> findByAtivoTrue();

    List<Projeto> findByAtivoTrueAndStatus(StatusProjeto status);

    List<Projeto> findByAtivoTrueAndNomeContainingIgnoreCase(String nome);

    List<Projeto> findByAtivoTrueAndEquipeId(Integer equipeId);

    // Projetos ativos que ainda têm ao menos uma atividade ativa não concluída
    @Query("select distinct p from Projeto p join p.listaDeAtividades a "
            + "where p.ativo = true and a.ativo = true and a.situacaoAtual <> :concluida")
    List<Projeto> findComAtividadesNaoConcluidas(@Param("concluida") SituacaoAtividade concluida);
}
