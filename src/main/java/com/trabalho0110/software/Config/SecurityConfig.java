package com.trabalho0110.software.Config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Login por sessão (cookie JSESSIONID), com o e-mail e a senha do usuário.
 *
 * Perfis (definidos pelo tipo do usuário, ver UsuarioService):
 * - GESTOR: pode tudo (cadastros, atualizações, atribuições e ativar/inativar).
 * - DESENVOLVEDOR: consulta tudo e pode assumir uma atividade e mudar a situação dela.
 */
@Configuration
public class SecurityConfig {

    // Endereços do frontend que podem chamar a API (separados por vírgula)
    @Value("${app.cors.origens}")
    private String[] origensPermitidas;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // CSRF continua ativo: o token vai no cookie XSRF-TOKEN e volta no cabeçalho X-XSRF-TOKEN
                .csrf(csrf -> csrf.spa())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/swagger-ui.html", "/swagger-ui/**", "/api-docss/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/atividade/atribuirDesenvolvedor", "/atividade/atualizarStatus/*")
                                .hasAnyRole("GESTOR", "DESENVOLVEDOR")
                        .requestMatchers(HttpMethod.POST, "/**").hasRole("GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/**").hasRole("GESTOR")
                        .anyRequest().authenticated())
                // POST /auth/login com os campos email e senha (form-urlencoded)
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                        .failureHandler((request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value())))
                // POST /auth/logout encerra a sessão
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()))
                // Sem sessão a API responde 401 (em vez de redirecionar para uma página de login)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origensPermitidas));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // Necessário para o navegador enviar o cookie de sessão
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
