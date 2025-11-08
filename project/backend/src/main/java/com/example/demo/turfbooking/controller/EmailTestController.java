package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.service.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {
    "https://turf-booking-3dehj06rl-monishs-projects-29844c66.vercel.app",
    "http://localhost:3000"
}, allowCredentials = "true")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        System.out.println("✅ EmailTestController loaded. Ready to send emails.");
    }

    // Test email endpoint
    @GetMapping("/send")
    public String sendTestEmail() {
        try {
            emailService.sendTestEmail("monidhoni0007@gmail.com");
            return "✅ Test Email Sent Successfully! Check your inbox.";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send email: " + e.getMessage();
        }
    }

    // Send verification email with query param `email` and `verificationUrl`
    @PostMapping("/sendVerification")
    public String sendVerificationEmail(
            @RequestParam String email,
            @RequestParam String verificationUrl) {
        try {
            emailService.sendVerificationEmail(email, verificationUrl);
            return "✅ Verification Email Sent Successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send verification email: " + e.getMessage();
        }
    }

    // Send password reset email with params `email` and `resetUrl`
    @PostMapping("/sendPasswordReset")
    public String sendPasswordResetEmail(
            @RequestParam String email,
            @RequestParam String resetUrl) {
        try {
            emailService.sendPasswordResetEmail(email, resetUrl);
            return "✅ Password Reset Email Sent Successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send password reset email: " + e.getMessage();
        }
    }
}
