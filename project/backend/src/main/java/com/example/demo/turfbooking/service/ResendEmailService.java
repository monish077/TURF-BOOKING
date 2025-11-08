package com.example.demo.turfbooking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        try {
            System.out.println("🚀 Sending Resend verification email to: " + toEmail);

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

            // Create headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                request, 
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Resend email sent successfully to: " + toEmail);
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("❌ Resend email failed. Status: " + response.getStatusCode());
                System.out.println("Response: " + response.getBody());
                throw new RuntimeException("Resend API returned: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("❌ Resend email failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    public void sendTestEmail(String toEmail) {
        try {
            System.out.println("🚀 Sending Resend test email to: " + toEmail);

            String apiUrl = "https://api.resend.com/emails";

            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("from", "Turf Booking <onboarding@resend.dev>");
            emailRequest.put("to", toEmail);
            emailRequest.put("subject", "✅ Test Email from Turf Booking");
            emailRequest.put("html", "<p>This is a <strong>test email</strong> from your Turf Booking application using Resend API!</p>");

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

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Resend test email sent successfully to: " + toEmail);
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("❌ Resend test email failed. Status: " + response.getStatusCode());
                System.out.println("Response: " + response.getBody());
            }

        } catch (Exception e) {
            System.out.println("❌ Resend test email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        try {
            System.out.println("🚀 Sending Resend password reset email to: " + toEmail);

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

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Resend password reset email sent successfully to: " + toEmail);
            } else {
                System.out.println("❌ Resend password reset email failed. Status: " + response.getStatusCode());
                throw new RuntimeException("Resend API returned: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.out.println("❌ Resend password reset email failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }
}