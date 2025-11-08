package com.example.demo.turfbooking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key:}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Send verification email
    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        String subject = "Verify Your Email - Turf Booking";
        String htmlContent = getVerificationEmailHtml(verificationUrl);
        sendEmail(toEmail, subject, htmlContent);
    }

    // Send password reset email
    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        String subject = "Reset Your Password - Turf Booking";
        String htmlContent = getPasswordResetHtml(resetUrl);
        sendEmail(toEmail, subject, htmlContent);
    }

    // Send test email
    public void sendTestEmail(String toEmail) {
        String subject = "✅ Test Email from Turf Booking";
        String htmlContent = "<p>This is a <strong>test email</strong> from your Turf Booking application using Resend API!</p>";
        sendEmail(toEmail, subject, htmlContent);
    }

    // Generic method to send email through Resend API
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
                throw new RuntimeException("Resend API key is not configured");
            }

            String apiUrl = "https://api.resend.com/emails";

            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("from", "Turf Booking <onboarding@resend.dev>");
            emailRequest.put("to", toEmail);
            emailRequest.put("subject", subject);
            emailRequest.put("html", htmlContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + resendApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Resend API error: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String getVerificationEmailHtml(String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                <h2>Welcome to Turf Booking!</h2>
                <p>Please verify your email by clicking the link below:</p>
                <p><a href="%s">Verify Email</a></p>
                <p>If you cannot click, copy paste this URL: %s</p>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);
    }

    private String getPasswordResetHtml(String resetUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                <h2>Password Reset Request</h2>
                <p>Reset your password by clicking below:</p>
                <p><a href="%s">Reset Password</a></p>
                <p>If you cannot click, copy paste this URL: %s</p>
                </body>
                </html>
                """.formatted(resetUrl, resetUrl);
    }
}
