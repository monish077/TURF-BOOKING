package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.service.EmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username:NOT_SET}")
    private String mailUsername;

    @Value("${spring.mail.host:NOT_SET}")
    private String mailHost;

    @Value("${spring.mail.port:0}")
    private int mailPort;

    @PostConstruct
    public void init() {
        System.out.println("✅ EmailTestController loaded.");
        System.out.println("   SMTP Host    : " + mailHost);
        System.out.println("   SMTP Port    : " + mailPort);
        System.out.println("   SMTP Username: " + mailUsername);
    }

    /**
     * Diagnostic endpoint — shows SMTP config and tries to send a test email.
     * GET /test/send
     */
    @GetMapping("/send")
    public String sendTestEmail() {
        StringBuilder info = new StringBuilder();
        info.append("=== SMTP CONFIG ===\n");
        info.append("Host     : ").append(mailHost).append("\n");
        info.append("Port     : ").append(mailPort).append("\n");
        info.append("Username : ").append(mailUsername).append("\n\n");

        try {
            emailService.sendEmail(
                mailUsername,
                "📧 Test Email — Mars Arena Backend",
                """
                <h2 style='color:green;'>✅ SMTP is working!</h2>
                <p>If you see this, your email configuration is correct.</p>
                """
            );
            info.append("✅ Email sent successfully to: ").append(mailUsername);
            return info.toString();
        } catch (Exception e) {
            // Walk full cause chain for real error
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();

            info.append("❌ SEND FAILED\n");
            info.append("Exception : ").append(e.getClass().getName()).append("\n");
            info.append("Message   : ").append(e.getMessage()).append("\n");
            info.append("Root cause: ").append(root.getClass().getName()).append(" — ").append(root.getMessage()).append("\n");
            return info.toString();
        }
    }

    /**
     * Send to a custom address.
     * GET /test/sendCustom?to=email@example.com&subject=Hello&body=Hi+there
     */
    @GetMapping("/sendCustom")
    public String sendCustomEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        try {
            emailService.sendEmail(to, subject, body);
            return "✅ Email sent successfully to: " + to;
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            return "❌ Failed — " + root.getClass().getSimpleName() + ": " + root.getMessage();
        }
    }
}
