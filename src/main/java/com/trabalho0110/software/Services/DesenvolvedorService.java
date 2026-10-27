package com.trabalho0110.software.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trabalho0110.software.Entities.Desenvolvedor;
import com.trabalho0110.software.Entities.Equipe;
import com.trabalho0110.software.Entities.Projeto;
import com.trabalho0110.software.Repositories.DesenvolvedorRepository;
import com.trabalho0110.software.Repositories.EquipeRepository;
import com.trabalho0110.software.Repositories.ProjetoRepository;
import com.trabalho0110.software.Repositories.UsuarioRepository;

@Service
public class DesenvolvedorService {

    private final DesenvolvedorRepository devRepository;
    private final EquipeRepository equipeRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DesenvolvedorService(DesenvolvedorRepository devRepository, EquipeRepository equipeRepository,
                                ProjetoRepository projetoRepository, UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.devRepository = devRepository;
        this.equipeRepository = equipeRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String salvar(String nome, String cpf, String email, String senha) {

        if (vazio(nome) || vazio(cpf) || vazio(email) || vazio(senha)) {
            return "Nome, CPF, e-mail e senha são obrigatórios!";
        }

        // O e-mail é o login, então não pode se repetir entre gestores e desenvolvedores
        if (usuarioRepository.existsByEmail(email)) {
            return "Já existe um usuário com este e-mail!";
        }

        Desenvolvedor dev = new Desenvolvedor(nome, cpf, email);
        dev.setSenha(passwordEncoder.encode(senha));

        devRepository.save(dev);

        return "Desenvolvedor salvo com sucesso!";
    }

    // Coloca o desenvolvedor em uma equipe (necessário para ele assumir atividades dos projetos dela)
    public String atribuirEquipe(Integer devId, Integer equipeId) {

        Optional<Desenvolvedor> dev = devRepository.findById(devId);
        if (dev.isEmpty()) {
            return "Desenvolvedor não encontrado!";
        }

        Optional<Equipe> equipe = equipeRepository.findById(equipeId);
        if (equipe.isEmpty()) {
            return "Equipe não encontrada!";
        }

        dev.get().setEquipe(equipe.get());

        devRepository.save(dev.get());

        return "Desenvolvedor atribuído à equipe com sucesso!";
    }

    // PUT dev (atribuirProjeto): o dev só pode ser atribuído a projeto ativo da própria equipe
    public String atribuirProjeto(Integer devId, Integer projetoId) {

        Optional<Desenvolvedor> dev = devRepository.findById(devId);
        if (dev.isEmpty()) {
            return "Desenvolvedor não encontrado!";
        }

        Optional<Projeto> projeto = projetoRepository.findById(projetoId);
        if (projeto.isEmpty()) {
            return "Projeto não encontrado!";
        }

        if (!projeto.get().isAtivo()) {
            return "O projeto está inativo!";
        }

        Equipe equipeProjeto = projeto.get().getEquipe();
        if (equipeProjeto != null
                && (dev.get().getEquipe() == null || !dev.get().getEquipe().getId().equals(equipeProjeto.getId()))) {
            return "O desenvolvedor não pertence à equipe responsável pelo projeto!";
        }

        dev.get().setProjetoAtribuido(projeto.get());

        devRepository.save(dev.get());

        return "Projeto atribuído ao desenvolvedor " + dev.get().getNome() + " com sucesso!";
    }

    public String mostrar(Integer id) {

        Optional<Desenvolvedor> dev = devRepository.findById(id);

        if (dev.isEmpty()) {
            return "Desenvolvedor não encontrado!";
        }

        return dev.get().toString();
    }

    public List<Desenvolvedor> listar() {
        return devRepository.findAll();
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
