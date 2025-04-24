package com.multi.mis.busgo_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Specify allowed origins explicitly
        config.addAllowedOrigin("http://localhost:4200"); // Frontend origin (e.g., Angular)
        config.addAllowedOrigin("http://localhost:3000"); // Add other origins if needed (e.g., for testing)
        // config.addAllowedOriginPattern("http://localhost:*"); // Use for dynamic ports, if necessary

        config.setAllowCredentials(true); // Allow cookies and credentials
        config.addAllowedHeader("*"); // Allow all headers
        config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)
        config.setMaxAge(3600L); // Cache preflight requests for 1 hour

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}