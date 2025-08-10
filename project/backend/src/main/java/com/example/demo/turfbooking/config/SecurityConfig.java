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
            // Disable CSRF for REST APIs (usually safe when using JWT)
            .csrf(csrf -> csrf.disable())

            // Enable CORS (configured globally via CorsConfig)
            .cors(cors -> {})

            // Configure URL authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow OPTIONS calls for preflight (CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints that do not require authentication
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

                // Public GET access to uploaded files
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Role-based access control
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // Other turf-related APIs require authentication
                .requestMatchers("/api/turfs/**").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Use stateless session management since JWT is used
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Add JWT authentication filter before the default username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder bean using BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose AuthenticationManager bean to be used for authentication elsewhere
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
