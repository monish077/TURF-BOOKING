package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    // ── CONFIGURATION ────────────────────────────────────────────────────────
    
    // Read the API key from environment variables (we will set this in Render)
    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String FRONTEND_URL  = "https://turf-booking-seven.vercel.app";
    
    // The email address you will verify in Brevo
    private static final String SENDER_EMAIL  = "monidhoni0007@gmail.com";
    private static final String SENDER_NAME   = "Mars Arena Turf Booking";

    private final RestTemplate restTemplate = new RestTemplate();

    // ══════════════════════════════════════════════════════════════════════
    //  PUBLIC METHODS
    // ══════════════════════════════════════════════════════════════════════

    public void sendVerificationEmail(User user) {
        String verifyURL = FRONTEND_URL + "/verify-email?token=" + user.getVerificationToken();

        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#2e7d32;">✅ Verify Your Email — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>Thanks for registering! Click the button below to verify your email address:</p>
              <div style="text-align:center;margin:30px 0;">
                <a href="%s"
                   style="background:#2e7d32;color:#fff;padding:14px 28px;text-decoration:none;border-radius:6px;font-size:16px;font-weight:bold;">
                  Verify My Email
                </a>
              </div>
              <p style="color:#888;font-size:13px;">If the button doesn't work, paste this link into your browser:<br>%s</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(user.getName(), verifyURL, verifyURL);

        sendHtmlEmailViaApi(user.getEmail(), user.getName(), "✅ Verify your email — Mars Arena Turf Booking", html);
    }

    public void sendResetPasswordEmail(User user) {
        String resetURL = FRONTEND_URL + "/reset-password?token=" + user.getResetPasswordToken();

        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#c62828;">🔐 Reset Your Password — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>We received a request to reset your password. Click the button below:</p>
              <div style="text-align:center;margin:30px 0;">
                <a href="%s"
                   style="background:#c62828;color:#fff;padding:14px 28px;text-decoration:none;border-radius:6px;font-size:16px;font-weight:bold;">
                  Reset Password
                </a>
              </div>
              <p style="color:#888;font-size:13px;">Link: %s</p>
              <p style="color:#888;font-size:13px;">If you didn't request a reset, you can safely ignore this email.</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(user.getName(), resetURL, resetURL);

        sendHtmlEmailViaApi(user.getEmail(), user.getName(), "🔐 Reset your password — Mars Arena", html);
    }

    public void sendBookingConfirmationEmail(
            String toEmail, String userName, String turfName, String date, String slot, String price) {
        
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#1565c0;">🎉 Booking Confirmed — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>Your turf booking is confirmed! Here are your details:</p>
              <table style="width:100%%;border-collapse:collapse;margin:16px 0;">
                <tr style="background:#e3f2fd;">
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Turf</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Date</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr style="background:#e3f2fd;">
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Slot</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Price</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">₹%s</td>
                </tr>
              </table>
              <p>📧 No further action needed. See you on the turf!</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(userName, turfName, date, slot, price);

        sendHtmlEmailViaApi(toEmail, userName, "🎉 Booking Confirmed — " + turfName, html);
    }

    public void sendEmail(String to, String subject, String content) {
        sendHtmlEmailViaApi(to, "User", subject, content);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRIVATE CORE SENDER (REST API)
    // ══════════════════════════════════════════════════════════════════════

    private void sendHtmlEmailViaApi(String toEmail, String toName, String subject, String htmlContent) {
        System.out.println("[API-EMAIL] Attempting to send email via Brevo to: " + toEmail);

        if (brevoApiKey == null || brevoApiKey.isBlank() || brevoApiKey.equals("NOT_SET")) {
            System.err.println("[API-EMAIL] ❌ BREVO_API_KEY is not set! Cannot send email.");
            throw new RuntimeException("Email API key is missing.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);
            headers.set("accept", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", SENDER_NAME, "email", SENDER_EMAIL));
            body.put("to", List.of(Map.of("email", toEmail, "name", toName)));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[API-EMAIL] ✅ Email sent successfully! Brevo response: " + response.getBody());
            } else {
                System.err.println("[API-EMAIL] ❌ Failed to send email. Status: " + response.getStatusCode());
                throw new RuntimeException("API responded with " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("[API-EMAIL] ❌ Exception while calling Brevo API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email send failed via API: " + e.getMessage(), e);
        }
    }
}
