package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        System.out.println("✅ EmailTestController initialized!");
    }

    // ✅ Basic Test Endpoint
    @GetMapping("/send")
    public String sendTestEmail() {
        emailService.sendEmail(
            "monidhoni0007@gmail.com",
            "📧 Test Email from Turf Booking System",
            "<p>This is a test email from your Spring Boot backend. If you're seeing this, your email service is working ✅</p>"
        );
        return "✅ Test Email Sent! Check your inbox.";
    }
}
