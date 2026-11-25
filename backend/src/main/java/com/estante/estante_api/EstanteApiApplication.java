package com.estante.estante_api;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EstanteApiApplication {

    @Bean
    WebMvcConfigurer localFrontendCors() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE")
                        .allowedHeaders("Content-Type");
            }
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(EstanteApiApplication.class, args);
	}

}
