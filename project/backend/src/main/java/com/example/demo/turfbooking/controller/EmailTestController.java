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
        System.out.println("✅ EmailTestController loaded. Ready for testing email service.");
    }

    /**
     * Sends a basic test email to verify SMTP configuration
     * Usage: GET /test/send
     */
    @GetMapping("/send")
    public String sendTestEmail() {
        try {
            emailService.sendEmail(
                "monidhoni0007@gmail.com", // Change this to your test email
                "📧 Test Email from Turf Booking System",
                """
                <h2 style='color:green;'>✅ Email Service Test Successful</h2>
                <p>This is a <b>test email</b> sent from your <i>Spring Boot</i> backend.</p>
                <p>If you are reading this, your SMTP configuration is working correctly!</p>
                """
            );
            return "✅ Test Email Sent Successfully! Check your inbox.";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send email: " + e.getMessage();
        }
    }

    /**
     * Sends a custom email using query parameters
     * Example: GET /test/sendCustom?to=email@example.com&subject=Hello&body=Hi+there
     */
    @GetMapping("/sendCustom")
    public String sendCustomEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        if (to == null || to.isBlank()) {
            return "❌ 'to' parameter is required";
        }
        if (subject == null || subject.isBlank()) {
            return "❌ 'subject' parameter is required";
        }
        if (body == null || body.isBlank()) {
            return "❌ 'body' parameter is required";
        }

        try {
            emailService.sendEmail(to, subject, body);
            return "✅ Email sent successfully to " + to;
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to send email: " + e.getMessage();
        }
    }
}
