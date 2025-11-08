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

    // -- Email Sending Methods (Use these only! No SMTP/JavaMailSender needed) --

    // Send verification email
    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        String subject = "Verify Your Email - Turf Booking";
        String htmlContent = getVerificationEmailHtml(verificationUrl);
        sendEmail(toEmail, subject, htmlContent);
    }

    // Send test email
    public void sendTestEmail(String toEmail) {
        String subject = "✅ Test Email from Turf Booking";
        String htmlContent = "<p>This is a <strong>test email</strong> from your Turf Booking application using Resend API!</p>";
        sendEmail(toEmail, subject, htmlContent);
    }

    // Send password reset email
    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        String subject = "Reset Your Password - Turf Booking";
        String htmlContent = getPasswordResetHtml(resetUrl);
        sendEmail(toEmail, subject, htmlContent);
    }

    // -- Internal helper for sending all emails via Resend API --
    private void sendEmail(String toEmail, String subject, String htmlContent) {
        try {
            logApiKeyStatus();
            if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
                System.out.println("❌ CRITICAL: RESEND API KEY IS MISSING OR EMPTY");
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

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request, 
                    String.class
            );

            System.out.println("STATUS: " + response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("ERROR: " + response.getBody());
                throw new RuntimeException("Failed to send email (Resend API error): " + response.getBody());
            }
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (Exception e) {
            System.out.println("💥 EMAIL ERROR: " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    // -- Email template helpers --
    private String getVerificationEmailHtml(String verificationUrl) {
        return """
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
    }

    private String getPasswordResetHtml(String resetUrl) {
        return """
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
    }

    // Helper to log API key visibility (for debugging, not production)
    private void logApiKeyStatus() {
        System.out.println("🔑 RESEND API KEY PRESENT: " + (resendApiKey != null));
        if (resendApiKey != null) {
            System.out.println("🔑 Length: " + resendApiKey.length());
        }
    }
}
