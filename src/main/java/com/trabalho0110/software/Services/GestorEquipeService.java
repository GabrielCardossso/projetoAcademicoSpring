package com.trabalho0110.software.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trabalho0110.software.Entities.GestorEquipe;
import com.trabalho0110.software.Repositories.GestorEquipeRepository;
import com.trabalho0110.software.Repositories.UsuarioRepository;

@Service
public class GestorEquipeService {

    private final GestorEquipeRepository gestorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public GestorEquipeService(GestorEquipeRepository gestorRepository, UsuarioRepository usuarioRepository,
                               PasswordEncoder passwordEncoder) {
        this.gestorRepository = gestorRepository;
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

        GestorEquipe gestor = new GestorEquipe(nome, cpf, email);
        gestor.setSenha(passwordEncoder.encode(senha));

        gestorRepository.save(gestor);

        return "Gestor salvo com sucesso!";
    }

    public String mostrar(Integer id) {

        Optional<GestorEquipe> gestor = gestorRepository.findById(id);

        if (gestor.isEmpty()) {
            return "Gestor não encontrado!";
        }

        return gestor.get().toString();
    }

    public List<GestorEquipe> listar() {
        return gestorRepository.findAll();
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
