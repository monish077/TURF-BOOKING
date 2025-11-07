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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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

    // ✅ CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins - add your frontend URLs
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://turf-booking-seven.vercel.app",
            "https://turf-booking-frontend.vercel.app",
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        
        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "X-Requested-With", 
            "Cache-Control", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Enable CORS using our custom configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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
                    "/api/test/**",  // ✅ ADDED THIS LINE - TEST ENDPOINTS ARE NOW PUBLIC
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
                    "/*.html",           // ← ADD THIS
                    "/**.html",
                    "/error"
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
        return new BCryptPasswordEncoder(12); // Increased strength for better security
    }

    // ✅ AuthenticationManager bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}