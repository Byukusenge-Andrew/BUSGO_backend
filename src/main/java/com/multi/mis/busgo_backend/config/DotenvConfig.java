package com.multi.mis.busgo_backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Prevents failure if .env is missing
                .load();
        dotenv.entries().forEach(entry -> {
            System.out.println("Loaded: " + entry.getKey() + "=" + entry.getValue());
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}