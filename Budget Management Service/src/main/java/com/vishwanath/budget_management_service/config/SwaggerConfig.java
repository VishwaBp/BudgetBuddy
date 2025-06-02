package com.vishwanath.budget_management_service.config;



import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Budget management Service",
                version = "1.0",
                description = "API Documentation for User Service"
        ),
        servers = @Server(
                url = "http://localhost:8081",
                description = "Local Server"
        ),
        security = @SecurityRequirement(name = "bearerAuth") // Enforce security globally
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerAuth", // Security scheme name must match globally applied security definition
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",   // Bearer type (JWT-based)
                bearerFormat = "JWT",
                description = "JWT Bearer Token Authentication"
        )
})
public class SwaggerConfig {

    @Bean
    public OpenAPI customizeOpenAPI() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new io.swagger.v3.oas.models.security.SecurityScheme()
                                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .addSecurityItem(
                        new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth", Collections.emptyList())
                ); // Ensures all endpoints apply bearerAuth security
    }
}

