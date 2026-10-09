package com.springup.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springUpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Up REST API")
                        .description("Backend engine for gamified wake-up routines, minigame verification, and streak tracking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Spring Up Engineering")
                                .url("https://github.com/spring-up")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
}