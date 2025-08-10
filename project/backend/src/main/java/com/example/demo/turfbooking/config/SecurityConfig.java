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

import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF since we use JWT tokens
            .csrf(csrf -> csrf.disable())

            // Enable CORS with the CorsConfigurationSource bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // Configure URL authorization
            .authorizeHttpRequests(auth -> auth
                // Allow OPTIONS requests for CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints accessible without authentication
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
                    "/api/turfs/*",      // use * instead of {id} for path variables in matcher
                    "/uploads/**",
                    "/manifest.json",
                    "/favicon.ico",
                    "/static/**"
                ).permitAll()

                // Public GET access to uploaded files
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Role based access control
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // Other turf-related APIs require authentication
                .requestMatchers("/api/turfs/**").authenticated()

                // Any other request requires authentication
                .anyRequest().authenticated()
            )

            // Use stateless session management (no HTTP sessions)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Add JWT filter before username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
