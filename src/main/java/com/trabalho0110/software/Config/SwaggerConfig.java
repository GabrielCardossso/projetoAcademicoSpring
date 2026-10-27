package com.trabalho0110.software.Config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

/**
 * Mostra o login e o logout no Swagger. Os dois são tratados pelo Spring Security (SecurityConfig),
 * e não por um Controller, por isso precisam ser descritos aqui. Só muda a documentação.
 * Depois do login pelo Swagger, o navegador guarda o cookie da sessão e as próximas chamadas
 * já vão com as permissões do usuário.
 */
@Configuration
public class SwaggerConfig {

    // Mesmo grupo do GET /auth/me (AuthController)
    private static final List<String> TAG = List.of("auth-controller");

    @Bean
    OpenApiCustomizer endpointsDeAutenticacao() {
        return openApi -> {
            ObjectSchema credenciais = new ObjectSchema();
            credenciais.addProperty("email", new StringSchema());
            credenciais.addProperty("senha", new StringSchema().format("password"));
            credenciais.setRequired(List.of("email", "senha"));

            Operation login = new Operation()
                    .tags(TAG)
                    .requestBody(new RequestBody()
                            .required(true)
                            .content(new Content().addMediaType("application/x-www-form-urlencoded",
                                    new MediaType().schema(credenciais))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Login feito"))
                            .addApiResponse("401", new ApiResponse().description("E-mail ou senha inválidos")));

            Operation logout = new Operation()
                    .tags(TAG)
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Sessão encerrada")));

            openApi.path("/auth/login", new PathItem().post(login));
            openApi.path("/auth/logout", new PathItem().post(logout));
        };
    }

    // Texto exibido no topo do Swagger, abaixo do título, com o significado dos códigos HTTP
    @Bean
    OpenApiCustomizer descricaoDosCodigos() {
        return openApi -> {
            if (openApi.getInfo() == null) {
                openApi.setInfo(new Info());
            }
            openApi.getInfo().setDescription("""
                    | Código | Significado |
                    |---|---|
                    | **200** | Requisição processada. Nos cadastros, atualizações e consultas por ID, leia a mensagem: \
                    uma regra de negócio pode ter recusado a operação (ex.: "Projeto não encontrado!"). |
                    | **400** | Pedido mal formado: campo obrigatório faltando, data, enum ou ID em formato errado. |
                    | **401** | Não autenticado: e-mail ou senha errados no login, ou chamada sem login. |
                    | **403** | Sem permissão: logado, mas o perfil não pode fazer isso (ex.: desenvolvedor cadastrando). \
                    Também ocorre sem o token CSRF. |
                    | **404** | Endpoint não existe (caminho errado). |
                    | **405** | O caminho existe, mas não aceita esse método HTTP. |
                    | **500** | Erro inesperado no servidor. |
                    """);
        };
    }
}
