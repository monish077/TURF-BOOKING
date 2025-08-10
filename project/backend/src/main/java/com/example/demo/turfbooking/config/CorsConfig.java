package com.example.demo.turfbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins - update with your actual frontend URLs
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://turf-booking-frontend.vercel.app",
            "https://turf-booking-an7sfm399-monishs-projects-29844c66.vercel.app",
            "http://localhost:3000"
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allowed headers - "*" allows all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (Authorization headers, cookies)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply CORS config to all API endpoints
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
