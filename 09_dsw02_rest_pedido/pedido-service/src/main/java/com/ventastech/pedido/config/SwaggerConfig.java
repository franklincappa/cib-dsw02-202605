package com.ventastech.pedido.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pedido Service API")
                        .description("Microservicio de pedidos VentasTech. " +
                                "Gestiona pedidos, estados, historial y generación de PDFs.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo Backend VentasTech")
                                .email("backend@ventastech.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Desarrollo local"),
                        new Server().url("http://pedido-service:8082").description("Docker")
                ));
    }
}