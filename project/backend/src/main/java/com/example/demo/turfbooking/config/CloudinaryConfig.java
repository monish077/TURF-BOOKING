package com.example.demo.turfbooking.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        logger.info("Reading CLOUDINARY_URL environment variable...");

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            logger.error("CLOUDINARY_URL environment variable is not set or empty!");
            throw new IllegalStateException("CLOUDINARY_URL environment variable is not set");
        }

        logger.info("CLOUDINARY_URL found and being used to configure Cloudinary");
        return new Cloudinary(cloudinaryUrl);
    }
}
