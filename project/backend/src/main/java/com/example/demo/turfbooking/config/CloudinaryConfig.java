package com.example.demo.turfbooking.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new IllegalStateException("CLOUDINARY_URL environment variable is not set");
        }
        return new Cloudinary(cloudinaryUrl);
    }
}
