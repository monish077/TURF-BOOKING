package com.example.demo.turfbooking.config;

import com.example.demo.turfbooking.jwt.JwtAuthenticationFilter;
import com.example.demo.turfbooking.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Enable CORS using global config

            .authorizeHttpRequests(auth -> auth
                // Allow preflight OPTIONS requests to pass without auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints (registration, login, verification, etc)
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/api/users/verify",
                    "/api/users/email-verified",
                    "/api/users/forgot-password",
                    "/api/users/reset-password",
                    "/api/users/test-mail",
                    "/test/**",
                    "/api/turfs/public",
                    "/api/turfs/{id}",
                    "/uploads/**"
                ).permitAll()

                // Public GET access to uploaded static files
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Role-based access control
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // All other turf endpoints require authentication
                .requestMatchers("/api/turfs/**").authenticated()

                // Catch all others require authentication
                .anyRequest().authenticated()
            )

            // Stateless session management (no sessions, JWT based)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Register JWT filter before username/password authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
