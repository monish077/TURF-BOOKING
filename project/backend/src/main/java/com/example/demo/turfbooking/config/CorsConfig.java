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

        // Add all frontend URLs you want to allow here
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://turf-booking-seven.vercel.app",   // your current frontend domain
            "https://turf-booking-frontend.vercel.app",
            "https://turf-booking-an7sfm399-monishs-projects-29844c66.vercel.app",
            "http://localhost:3000"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(Arrays.asList("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply CORS config to all paths
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
