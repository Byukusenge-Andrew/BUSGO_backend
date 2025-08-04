package com.multi.mis.busgo_backend.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Properties;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/company/login",
                                "/api/auth/admin/login",
                                "/api/auth/reset-password",
                                "/api/auth/request-password-reset",
                                  "/api/schedules/search",
                                "/api/test/public",
                                "/api/test",
                                "/actuator/**",
                                "/api/schedules",
                                "/api/companies/login",
                                "/error" // Add /error to permitAll
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Allow users to read schedules and routes for booking, but only ADMIN/COMPANY can modify
                        .requestMatchers(HttpMethod.GET, "/api/schedules/**").hasAnyRole("USER", "ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.POST, "/api/schedules/**").hasAnyRole("ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasAnyRole("ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasAnyRole("ADMIN", "COMPANY")
                        // Allow users to read routes for booking
                        .requestMatchers(HttpMethod.GET, "/api/routes/**").hasAnyRole("USER", "ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.POST, "/api/routes/**").hasAnyRole("ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.PUT, "/api/routes/**").hasAnyRole("ADMIN", "COMPANY")
                        .requestMatchers(HttpMethod.DELETE, "/api/routes/**").hasAnyRole("ADMIN", "COMPANY")
                        // Other company management endpoints
                        .requestMatchers("/api/companies/**", "/api/buses/**", "/api/bus-locations/**").hasAnyRole("ADMIN", "COMPANY")
                        .requestMatchers("/api/users/**", "/api/bookings/**", "/api/tickets/**", "/api/payments/**").hasAnyRole("USER", "ADMIN", "COMPANY")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("andrebyukusenge9@gmail.com");
   // Replace with your Gmail App Password

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
