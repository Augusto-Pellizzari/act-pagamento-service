package com.actdigital.lojaonlinepagamentobe.infraestructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "Pagamento Service API",
                version     = "v1",
                description = "Gerencia confirmações e status de pagamentos"
        ),
        servers = @Server(url = "/", description = "Servidor local")
)
public class OpenApiConfig { }