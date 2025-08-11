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
            // ✅ Enable CORS with config from CorsConfig
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // ❌ Disable CSRF because we use JWT, not cookies for sessions
            .csrf(csrf -> csrf.disable())

            // ✅ Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints
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
                    "/api/turfs/*", // single path param
                    "/manifest.json",
                    "/favicon.ico",
                    "/static/**"
                ).permitAll()

                // Public GET for uploaded files
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Admin role endpoints
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // User role endpoints
                .requestMatchers("/api/user/**").hasAuthority("ROLE_USER")

                // Bookings require either role
                .requestMatchers("/api/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                // Other Turf APIs require authentication
                .requestMatchers("/api/turfs/**").authenticated()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // ✅ Use stateless session (JWT only)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ Add JWT authentication filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Password encoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
