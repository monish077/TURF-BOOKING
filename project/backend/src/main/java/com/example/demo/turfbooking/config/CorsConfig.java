package com.example.demo.turfbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allowed Frontend URLs
        config.setAllowedOriginPatterns(List.of(
                "https://turf-booking-seven.vercel.app",   
                "https://turf-booking-frontend.vercel.app", 
                "https://turf-booking-an7sfm399-monishs-projects-29844c66.vercel.app",
                "http://localhost:3000"
        ));

        // ✅ Allowed HTTP Methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // ✅ Allowed Headers
        config.setAllowedHeaders(List.of(
                "Authorization", "Cache-Control", "Content-Type"
        ));

        // ✅ Allow credentials (important for JWT cookies / session)
        config.setAllowCredentials(true);

        // ✅ Reduce preflight requests
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
