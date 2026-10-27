package com.trabalho0110.software.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trabalho0110.software.DTOs.UsuarioLogadoDTO;
import com.trabalho0110.software.Services.UsuarioService;

// O login (POST /auth/login) e o logout (POST /auth/logout) são tratados pelo Spring Security (SecurityConfig)
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Usuário da sessão atual (401 se não houver login)
    @GetMapping("/me")
    public UsuarioLogadoDTO me(Authentication authentication) {
        return usuarioService.buscarLogado(authentication.getName());
    }
}
