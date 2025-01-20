package com.laosarl.allocation_ressources.configurations;

import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }

    @Operation(summary = "Returns a Hello World message")
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}