package com.trabalho0110.software.Config;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.trabalho0110.software.Entities.Atividade;
import com.trabalho0110.software.Entities.Desenvolvedor;
import com.trabalho0110.software.Entities.Equipe;
import com.trabalho0110.software.Entities.GestorEquipe;
import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Entities.Usuario;
import com.trabalho0110.software.Enums.Prioridade;
import com.trabalho0110.software.Enums.SituacaoAtividade;
import com.trabalho0110.software.Repositories.AtividadeRepository;
import com.trabalho0110.software.Repositories.DesenvolvedorRepository;
import com.trabalho0110.software.Repositories.EquipeRepository;
import com.trabalho0110.software.Repositories.GestorEquipeRepository;
import com.trabalho0110.software.Repositories.ProjetoRepository;
import com.trabalho0110.software.Repositories.UsuarioRepository;

/**
 * Carrega dados de exemplo para a demonstração, somente se o banco estiver vazio.
 * Grava direto pelos repositories, por isso consegue criar uma atividade com prazo
 * já vencido (a API não permite isso), útil para demonstrar a consulta de atrasadas.
 */
@Configuration
public class DadosIniciais {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    @Bean
    @Order(1)
    CommandLineRunner carregarDados(EquipeRepository equipeRepository,
                                    GestorEquipeRepository gestorRepository,
                                    DesenvolvedorRepository devRepository,
                                    ProjetoRepository projetoRepository,
                                    AtividadeRepository atividadeRepository) {
        return args -> {
            if (equipeRepository.count() > 0) {
                return;
            }

            LocalDate hoje = LocalDate.now();

            GestorEquipe gestor = gestorRepository.save(
                    new GestorEquipe("Gabriel", "123.456.789-10", "gabriel@gmail.com"));

            Equipe equipe = new Equipe("Equipe GT");
            equipe.setGestor(gestor);
            equipe = equipeRepository.save(equipe);

            Desenvolvedor dev1 = new Desenvolvedor("Jardel", "789.456.123-09", "jardel@gmail.com");
            dev1.setEquipe(equipe);
            dev1 = devRepository.save(dev1);

            Desenvolvedor dev2 = new Desenvolvedor("Rafael", "321.654.987-11", "rafael@gmail.com");
            dev2.setEquipe(equipe);
            dev2 = devRepository.save(dev2);

            Projeto projeto = projetoRepository.save(new Projeto(
                    "CRM - Costumer Relationship Management.",
                    "Controle de relacionamento de clientes",
                    hoje.minusDays(30), hoje.plusDays(180), equipe));

            Atividade a1 = new Atividade("Modelar banco de dados", "Criar o modelo de entidades",
                    Prioridade.ALTA, hoje.minusDays(30), hoje.minusDays(10), projeto);
            a1.setSituacaoAtual(SituacaoAtividade.CONCLUIDA);
            a1.setDesenvolvedor(dev1);

            // Atividade atrasada: prazo vencido e ainda não concluída
            Atividade a2 = new Atividade("Implementar autenticação", "Login dos usuários",
                    Prioridade.ALTA, hoje.minusDays(20), hoje.minusDays(3), projeto);
            a2.setSituacaoAtual(SituacaoAtividade.EM_ANDAMENTO);
            a2.setDesenvolvedor(dev2);

            Atividade a3 = new Atividade("Escrever documentação", "Documentar a API",
                    Prioridade.BAIXA, hoje, hoje.plusDays(40), projeto);

            atividadeRepository.save(a1);
            atividadeRepository.save(a2);
            atividadeRepository.save(a3);
        };
    }

    /**
     * Roda depois dos dados de exemplo: todo usuário ainda sem senha (os de exemplo e os
     * cadastrados antes do login existir) recebe a senha inicial, para conseguir entrar.
     * A senha vem da variável de ambiente SENHA_INICIAL; se ela não existir, uma senha
     * aleatória é gerada e mostrada no console.
     */
    @Bean
    @Order(2)
    CommandLineRunner definirSenhasIniciais(UsuarioRepository usuarioRepository,
                                            PasswordEncoder passwordEncoder,
                                            @Value("${app.senha-inicial:}") String senhaInicial) {
        return args -> {
            List<Usuario> semSenha = usuarioRepository.findBySenhaIsNull();
            if (semSenha.isEmpty()) {
                return;
            }

            String senha = senhaInicial;
            if (senha.isBlank()) {
                senha = gerarSenha();
            }

            for (Usuario usuario : semSenha) {
                usuario.setSenha(passwordEncoder.encode(senha));
                usuarioRepository.save(usuario);
                log.warn("Senha inicial definida para o usuário {}", usuario.getEmail());
            }

            if (senhaInicial.isBlank()) {
                log.warn("Senha inicial gerada (anote, ela não será exibida de novo): {}", senha);
            }
        };
    }

    private String gerarSenha() {
        String caracteres = "abcdefghjkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }
}
