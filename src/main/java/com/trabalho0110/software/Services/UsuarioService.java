package com.trabalho0110.software.Services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trabalho0110.software.DTOs.UsuarioLogadoDTO;
import com.trabalho0110.software.Entities.GestorEquipe;
import com.trabalho0110.software.Entities.Usuario;
import com.trabalho0110.software.Repositories.UsuarioRepository;

/**
 * Ponte entre os usuários do sistema e o Spring Security.
 * O login é o e-mail; o perfil vem do tipo do usuário:
 * GestorEquipe -> ROLE_GESTOR, Desenvolvedor -> ROLE_DESENVOLVEDOR.
 */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Chamado pelo Spring Security durante o login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = buscar(email);

        if (usuario.getSenha() == null) {
            throw new UsernameNotFoundException("Usuário sem senha cadastrada!");
        }

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(perfil(usuario))
                .build();
    }

    // GET /auth/me
    public UsuarioLogadoDTO buscarLogado(String email) {

        Usuario usuario = buscar(email);

        return new UsuarioLogadoDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), perfil(usuario));
    }

    private Usuario buscar(String email) {
        return usuarioRepository.findFirstByEmailOrderByIdAsc(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }

    private String perfil(Usuario usuario) {
        return usuario instanceof GestorEquipe ? "GESTOR" : "DESENVOLVEDOR";
    }
}
