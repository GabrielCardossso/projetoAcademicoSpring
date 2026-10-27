package com.trabalho0110.software.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trabalho0110.software.Entities.Usuario;

// Consulta gestores e desenvolvedores juntos (os dois herdam de Usuario)
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    // O e-mail é o login. Se houver repetidos de antes do login existir, vale o mais antigo.
    Optional<Usuario> findFirstByEmailOrderByIdAsc(String email);

    boolean existsByEmail(String email);

    List<Usuario> findBySenhaIsNull();
}
