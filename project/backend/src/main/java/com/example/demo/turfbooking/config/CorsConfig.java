package com.example.demo.turfbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // List of allowed origins for easier maintenance
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://turf-booking-seven.vercel.app",   
        "https://turf-booking-frontend.vercel.app", 
        "https://turf-booking-*.vercel.app",
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://localhost:5174",
        "http://127.0.0.1:5174"
    );

    // List of allowed methods
    private static final List<String> ALLOWED_METHODS = Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    );

    // List of allowed headers - EXPANDED
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
        "Authorization", 
        "Cache-Control", 
        "Content-Type",
        "Content-Length",
        "Accept",
        "Accept-Encoding",
        "Accept-Language",
        "X-Requested-With",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "X-API-Key",
        "X-Request-ID",
        "X-CSRF-Token",
        "User-Agent",
        "Referer",
        "Sec-Fetch-Mode",
        "Sec-Fetch-Site",
        "Sec-Fetch-Dest"
    );

    // List of exposed headers
    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
        "Authorization",
        "authorization",
        "Content-Type",
        "Content-Disposition",
        "X-Total-Count",
        "X-Request-ID",
        "X-CSRF-Token",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "Access-Control-Expose-Headers"
    );

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allowed Frontend URLs
        config.setAllowedOriginPatterns(ALLOWED_ORIGINS);

        // ✅ Allowed HTTP Methods - IMPORTANT: Include OPTIONS
        config.setAllowedMethods(ALLOWED_METHODS);

        // ✅ Allowed Headers - EXPANDED for preflight
        config.setAllowedHeaders(ALLOWED_HEADERS);

        // ✅ Exposed Headers
        config.setExposedHeaders(EXPOSED_HEADERS);

        // ✅ Allow credentials
        config.setAllowCredentials(true);

        // ✅ Preflight cache (1 hour)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Apply CORS to all endpoints
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                .allowedHeaders("*")  // ← CHANGE TO WILDCARD
                .exposedHeaders(EXPOSED_HEADERS.toArray(new String[0]))
                .allowCredentials(true)
                .maxAge(3600L);
    }
}