package com.example.demo.turfbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())   // Disable CSRF for testing APIs via Postman/React frontend
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // Allow all requests without auth
            );

        return http.build();
    }
}
