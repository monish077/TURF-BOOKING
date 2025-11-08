package com.example.demo.turfbooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmailTestController {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Simple test email endpoint
     * Usage: GET /api/test-email
     */
    @GetMapping("/test-email")
    public String testEmail() {
        try {
            System.out.println("🚀 TEST: Attempting to send test email...");
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("monidhoni0007@gmail.com");
            message.setSubject("✅ Test Email from Turf Booking");
            message.setText("This is a test email from your Turf Booking application. If you receive this, email service is working!");
            message.setFrom("monidhoni0007@gmail.com");
            
            mailSender.send(message);
            System.out.println("✅ TEST: Email sent successfully!");
            
            return "✅ Test email sent successfully to monidhoni0007@gmail.com! Check your inbox.";
            
        } catch (Exception e) {
            System.out.println("❌ TEST: Email failed: " + e.getMessage());
            e.printStackTrace();
            return "❌ Email test failed: " + e.getMessage();
        }
    }

    /**
     * Test email with custom recipient
     * Usage: GET /api/test-email-custom?email=your@email.com
     */
    @GetMapping("/test-email-custom")
    public String testEmailCustom(@RequestParam String email) {
        try {
            System.out.println("🚀 TEST: Attempting to send test email to: " + email);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("✅ Test Email from Turf Booking");
            message.setText("This is a test email from your Turf Booking application. If you receive this, email service is working!");
            message.setFrom("monidhoni0007@gmail.com");
            
            mailSender.send(message);
            System.out.println("✅ TEST: Email sent successfully to: " + email);
            
            return "✅ Test email sent successfully to " + email + "! Check your inbox.";
            
        } catch (Exception e) {
            System.out.println("❌ TEST: Email failed: " + e.getMessage());
            e.printStackTrace();
            return "❌ Email test failed: " + e.getMessage();
        }
    }
}