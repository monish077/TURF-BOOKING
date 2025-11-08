package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.service.ResendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email") // More specific route for all email actions
public class EmailTestController {

    @Autowired
    private ResendEmailService resendEmailService;

    /**
     * Simple test email endpoint
     * Usage: GET /api/email/test-email
     */
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        String recipient = "monidhoni0007@gmail.com";
        try {
            resendEmailService.sendTestEmail(recipient);
            return ResponseEntity.ok("✅ Test email sent successfully to " + recipient + "! Check your inbox.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Email test failed: " + e.getMessage());
        }
    }

    /**
     * Test email with custom recipient
     * Usage: GET /api/email/test-email-custom?email=your@email.com
     */
    @GetMapping("/test-email-custom")
    public ResponseEntity<String> testEmailCustom(@RequestParam String email) {
        try {
            resendEmailService.sendTestEmail(email);
            return ResponseEntity.ok("✅ Test email sent successfully to " + email + "! Check your inbox.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Email test failed: " + e.getMessage());
        }
    }

    /**
     * Resend verification email
     * Usage: POST /api/email/resend-verification?email=...&verificationUrl=...
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(
            @RequestParam String email,
            @RequestParam String verificationUrl
    ) {
        try {
            resendEmailService.sendVerificationEmail(email, verificationUrl);
            return ResponseEntity.ok("✅ Verification email sent successfully to " + email + "!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Verification email failed: " + e.getMessage());
        }
    }

    /**
     * Send password reset email
     * Usage: POST /api/email/send-password-reset?email=...&resetUrl=...
     */
    @PostMapping("/send-password-reset")
    public ResponseEntity<String> sendPasswordResetEmail(
            @RequestParam String email,
            @RequestParam String resetUrl
    ) {
        try {
            resendEmailService.sendPasswordResetEmail(email, resetUrl);
            return ResponseEntity.ok("✅ Password reset email sent successfully to " + email + "!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Password reset email failed: " + e.getMessage());
        }
    }
}
