package com.example.demo.turfbooking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResendEmailService {

    @Value("${resend.api.key:}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Simple initialization without @PostConstruct
    public ResendEmailService() {
        System.out.println("🔑 === RESEND SERVICE CREATED ===");
    }

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        try {
            System.out.println("🚀 === RESEND VERIFICATION EMAIL START ===");
            System.out.println("📧 Recipient: " + toEmail);
            System.out.println("🔗 Verification URL: " + verificationUrl);
            
            // Validate API key on first use
            logApiKeyStatus();
            
            if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
                System.out.println("❌ CRITICAL: RESEND API KEY IS MISSING OR EMPTY");
                throw new RuntimeException("Resend API key is not configured");
            }
            
            System.out.println("🔑 Using API Key: " + resendApiKey.substring(0, 8) + "..." + resendApiKey.substring(resendApiKey.length() - 4));

            String apiUrl = "https://api.resend.com/emails";

            // Create the email request payload
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("from", "Turf Booking <onboarding@resend.dev>");
            emailRequest.put("to", toEmail);
            emailRequest.put("subject", "Verify Your Email - Turf Booking");
            
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; }
                        .header { background: #4CAF50; color: white; padding: 10px; text-align: center; border-radius: 5px; }
                        .button { background: #4CAF50; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; display: inline-block; }
                        .footer { margin-top: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>🎉 Welcome to Turf Booking!</h2>
                        </div>
                        <p>Hello,</p>
                        <p>Thank you for registering with Turf Booking. Please verify your email address by clicking the button below:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </p>
                        <p>Or copy and paste this link in your browser:</p>
                        <p><code>%s</code></p>
                        <div class="footer">
                            <p>If you didn't create an account, please ignore this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);
            
            emailRequest.put("html", htmlContent);

            System.out.println("📧 Email payload created");
            System.out.println("From: Turf Booking <onboarding@resend.dev>");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Verify Your Email - Turf Booking");

            // Create headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            System.out.println("📤 Sending request to Resend API...");
            System.out.println("URL: " + apiUrl);

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                request, 
                String.class
            );

            System.out.println("📥 === RESEND API RESPONSE ===");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ RESEND SUCCESS - Email sent successfully to: " + toEmail);
                System.out.println("Response ID: " + response.getBody());
            } else {
                System.out.println("❌ RESEND FAILED - HTTP Status: " + response.getStatusCode());
                System.out.println("Error Response: " + response.getBody());
                throw new RuntimeException("Resend API returned error: " + response.getStatusCode() + " - " + response.getBody());
            }

            System.out.println("🏁 === RESEND VERIFICATION EMAIL END ===");

        } catch (Exception e) {
            System.out.println("💥 RESEND VERIFICATION EMAIL ERROR ===");
            System.out.println("Error Type: " + e.getClass().getSimpleName());
            System.out.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            System.out.println("💥 === RESEND VERIFICATION EMAIL ERROR END ===");
            throw new RuntimeException("Failed to send verification email via Resend: " + e.getMessage());
        }
    }

    public void sendTestEmail(String toEmail) {
        try {
            System.out.println("🚀 === RESEND TEST EMAIL START ===");
            System.out.println("📧 Test Recipient: " + toEmail);
            
            // Log API key status
            logApiKeyStatus();
            
            if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
                System.out.println("❌ RESEND API KEY NOT CONFIGURED");
                return;
            }

            String apiUrl = "https://api.resend.com/emails";

            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("from", "Turf Booking <onboarding@resend.dev>");
            emailRequest.put("to", toEmail);
            emailRequest.put("subject", "✅ Test Email from Turf Booking");
            emailRequest.put("html", "<p>This is a <strong>test email</strong> from your Turf Booking application using Resend API!</p>");

            System.out.println("📧 Test email payload created");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            System.out.println("📤 Sending test request to Resend API...");

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                request, 
                String.class
            );

            System.out.println("📥 === RESEND TEST RESPONSE ===");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ RESEND TEST SUCCESS - Test email sent to: " + toEmail);
            } else {
                System.out.println("❌ RESEND TEST FAILED - Status: " + response.getStatusCode());
                System.out.println("Error: " + response.getBody());
            }

            System.out.println("🏁 === RESEND TEST EMAIL END ===");

        } catch (Exception e) {
            System.out.println("💥 RESEND TEST EMAIL ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        try {
            System.out.println("🚀 === RESEND PASSWORD RESET EMAIL START ===");
            System.out.println("📧 Recipient: " + toEmail);
            System.out.println("🔗 Reset URL: " + resetUrl);
            
            // Log API key status
            logApiKeyStatus();
            
            if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
                System.out.println("❌ RESEND API KEY NOT CONFIGURED");
                throw new RuntimeException("Resend API key is not configured");
            }

            String apiUrl = "https://api.resend.com/emails";

            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("from", "Turf Booking <onboarding@resend.dev>");
            emailRequest.put("to", toEmail);
            emailRequest.put("subject", "Reset Your Password - Turf Booking");
            
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; }
                        .header { background: #ff6b6b; color: white; padding: 10px; text-align: center; border-radius: 5px; }
                        .button { background: #ff6b6b; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; display: inline-block; }
                        .footer { margin-top: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>🔑 Password Reset Request</h2>
                        </div>
                        <p>Hello,</p>
                        <p>We received a request to reset your password. Click the button below to reset it:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </p>
                        <p>Or copy and paste this link in your browser:</p>
                        <p><code>%s</code></p>
                        <div class="footer">
                            <p>If you didn't request this, please ignore this email.</p>
                            <p>This link will expire in 1 hour.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetUrl, resetUrl);
            
            emailRequest.put("html", htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                request, 
                String.class
            );

            System.out.println("📥 Password Reset Response: " + response.getStatusCode() + " - " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ RESEND PASSWORD RESET SUCCESS - Email sent to: " + toEmail);
            } else {
                System.out.println("❌ RESEND PASSWORD RESET FAILED - Status: " + response.getStatusCode());
                throw new RuntimeException("Resend API returned: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("💥 RESEND PASSWORD RESET ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    // Helper method to log API key status
    private void logApiKeyStatus() {
        System.out.println("🔑 === RESEND API KEY STATUS ===");
        System.out.println("API Key configured: " + (resendApiKey != null));
        if (resendApiKey != null) {
            System.out.println("API Key length: " + resendApiKey.length());
            System.out.println("API Key starts with: " + resendApiKey.substring(0, Math.min(8, resendApiKey.length())) + "...");
        } else {
            System.out.println("❌ RESEND API KEY IS NULL - Check environment variable RESEND_API_KEY");
        }
        System.out.println("🔑 === RESEND API KEY STATUS END ===");
    }
}