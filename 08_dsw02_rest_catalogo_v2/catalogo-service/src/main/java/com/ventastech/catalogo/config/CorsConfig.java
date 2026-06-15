package com.ventastech.catalogo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",    // Angular dev
                "http://localhost:3000",    // React / Next si lo usas
                "http://localhost:8080",    // Gateway o Postman web
                "http://127.0.0.1:4200"
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Headers permitidos
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Headers expuestos al cliente
        config.setExposedHeaders(List.of(
                "Location",          // para el 201 Created
                "Authorization"
        ));

        // Permite enviar cookies / tokens en las peticiones
        config.setAllowCredentials(true);

        // Tiempo que el navegador cachea la respuesta OPTIONS
        config.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // aplica a todos los endpoints

        return new CorsFilter(source);
    }
}
