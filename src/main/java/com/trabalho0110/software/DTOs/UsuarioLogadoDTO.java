package com.trabalho0110.software.DTOs;

// Dados do usuário autenticado devolvidos em GET /auth/me (sem senha)
public record UsuarioLogadoDTO(Integer id, String nome, String email, String perfil) {
}
