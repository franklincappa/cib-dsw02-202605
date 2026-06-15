package com.ventastech.catalogo.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
                .title("Catálogo Service API")
                .description("Catálogo de productos - VentasTech")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Team Desarrollo Backend")
                        .email("team-dev@ventastech.com"))
                        .license(new License()
                                .name("VentasTech Interno")
                                .url("https://ventastech.com/license")))
            .servers(List.of(
                    new Server().url("http://localhost:8081").description("Desarrollo Local"),
                    new Server().url("http://localhost:8087").description("Desarrollo Local v2"),
                    new Server().url("http://localhost:8096").description("Ambiente QA"),
                    new Server().url("http://catalogo-service:8081").description("Ambiente Producción Azure")
            ));
    }
}
