package com.example.demo.turfbooking.config;

import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Bean
    public Cloudinary cloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        logger.info("Reading CLOUDINARY_URL environment variable...");

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            logger.warn("⚠️ CLOUDINARY_URL environment variable is not set or empty!");
            logger.info("🔄 Using configured Cloudinary credentials...");
            
            // Use your actual Cloudinary credentials
            return createCloudinaryWithCredentials();
        }

        logger.info("✅ CLOUDINARY_URL found and being used to configure Cloudinary");
        return new Cloudinary(cloudinaryUrl);
    }

    private Cloudinary createCloudinaryWithCredentials() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dwrbkpnir");
        config.put("api_key", "995136623584273");
        config.put("api_secret", "REBgwjgmmDnfoPTSOfujBhDz1sc");
        
        Cloudinary cloudinary = new Cloudinary(config);
        logger.info("✅ Cloudinary configured successfully with credentials for cloud: dwrbkpnir");
        return cloudinary;
    }
}