package com.example.demo.turfbooking.config;

import com.example.demo.turfbooking.jwt.JwtAuthenticationFilter;
import com.example.demo.turfbooking.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
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
            // ✅ Enable CORS with our custom configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // ❌ Disable CSRF because we use JWT (stateless)
            .csrf(csrf -> csrf.disable())

            // ✅ Exception handling for authentication
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("""
                        {
                            "error": "Unauthorized",
                            "message": "Authentication required",
                            "path": "%s",
                            "timestamp": "%s"
                        }
                        """.formatted(request.getRequestURI(), java.time.LocalDateTime.now()));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("""
                        {
                            "error": "Forbidden",
                            "message": "Access denied: insufficient permissions",
                            "path": "%s",
                            "timestamp": "%s"
                        }
                        """.formatted(request.getRequestURI(), java.time.LocalDateTime.now()));
                })
            )

            // ✅ Authorization rules
            .authorizeHttpRequests(auth -> auth
                // ==================== PUBLIC ENDPOINTS ====================
                
                // Allow preflight requests without auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // API Documentation
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security"
                ).permitAll()

                // Authentication endpoints
                .requestMatchers(
                    "/api/auth/**",
                    "/api/users/register",
                    "/api/users/tregister",
                    "/api/users/login",
                    "/api/users/verify",
                    "/api/users/email-verified",
                    "/api/users/forgot-password",
                    "/api/users/reset-password",
                    "/api/users/test-mail",
                    "/api/users/test-public",
                    "/api/users/test-echo"
                ).permitAll()

                // Testing and debugging endpoints
                .requestMatchers(
                    "/test/**",
                    "/api/debug/**",
                    "/api/test/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()

                // Static resources
                .requestMatchers(
                    "/manifest.json",
                    "/favicon.ico",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/*.html",
                    "/**.html",
                    "/error",
                    "/uploads/**"
                ).permitAll()

                // Public turf operations (read-only)
                .requestMatchers(HttpMethod.GET, 
                    "/api/turfs",
                    "/api/turfs/public",
                    "/api/turfs/{id:[a-zA-Z0-9-]+}",
                    "/api/turfs/search",
                    "/api/turfs/city/{city}",
                    "/api/turfs/featured",
                    "/api/turfs/categories",
                    "/api/turfs/sports"
                ).permitAll()

                // Public turf images and media
                .requestMatchers(HttpMethod.GET, 
                    "/api/turfs/images/**",
                    "/api/turfs/media/**",
                    "/api/files/**"
                ).permitAll()

                // ==================== AUTHENTICATED ENDPOINTS ====================

                // User profile management
                .requestMatchers(
                    "/api/users/profile",
                    "/api/users/update-profile",
                    "/api/users/change-password",
                    "/api/users/my-bookings",
                    "/api/users/notifications"
                ).authenticated()

                // Bookings management
                .requestMatchers(
                    "/api/bookings/**",
                    "/api/bookings/user/**",
                    "/api/bookings/create",
                    "/api/bookings/update/**",
                    "/api/bookings/cancel/**",
                    "/api/bookings/my-bookings"
                ).authenticated()

                // Payment endpoints
                .requestMatchers(
                    "/api/payments/**",
                    "/api/payments/create-intent",
                    "/api/payments/confirm",
                    "/api/payments/history"
                ).authenticated()

                // ==================== ADMIN ENDPOINTS ====================

                // Admin turf management
                .requestMatchers(
                    "/api/turfs/admin/**",
                    "/api/turfs/create",
                    "/api/turfs/update/**",
                    "/api/turfs/delete/**",
                    "/api/turfs/approve/**",
                    "/api/turfs/reject/**"
                ).hasRole("ADMIN")

                // Admin user management
                .requestMatchers(
                    "/api/admin/**",
                    "/api/admin/users/**",
                    "/api/admin/bookings/**",
                    "/api/admin/payments/**",
                    "/api/admin/dashboard/**",
                    "/api/admin/reports/**"
                ).hasRole("ADMIN")

                // ==================== TURF MANAGER ENDPOINTS ====================

                .requestMatchers(
                    "/api/manager/turfs/**",
                    "/api/manager/bookings/**",
                    "/api/manager/payments/**"
                ).hasAnyRole("ADMIN", "MANAGER")

                // ==================== ALL OTHER ENDPOINTS ====================
                
                // All other Turf APIs require authentication
                .requestMatchers("/api/turfs/**").authenticated()

                // Any remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            // ✅ Stateless session for JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    // ✅ Password encoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // ✅ AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}