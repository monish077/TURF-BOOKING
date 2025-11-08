package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.repository.UserRepository;
import com.example.demo.turfbooking.service.EmailService;
import com.example.demo.turfbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService; // ✅ Added missing injection

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @GetMapping("/connection")
    public String testConnection() {
        try {
            long count = userRepository.count();
            return "✅ MongoDB connected successfully! Total users: " + count;
        } catch (Exception e) {
            return "❌ MongoDB connection failed: " + e.getMessage();
        }
    }

    @GetMapping("/create-user")
    public String createTestUser() {
        try {
            User user = new User();
            user.setEmail("test" + System.currentTimeMillis() + "@test.com");
            user.setPassword("temp123");
            user.setName("Test User");
            user.setEnabled(true);
            user.setRole(Role.USER);

            User saved = userRepository.save(user);

            return "✅ User created successfully!<br>" +
                    "ID: " + saved.getId() + "<br>" +
                    "Email: " + user.getEmail() + "<br>" +
                    "Password: temp123<br>" +
                    "Enabled: " + user.isEnabled() + "<br>" +
                    "Total users: " + userRepository.count();

        } catch (Exception e) {
            return "❌ Failed to create user: " + e.getMessage();
        }
    }

    @GetMapping("/create-proper-user")
    public String createProperUser() {
        try {
            User user = new User();
            String email = "proper@test.com";
            String rawPassword = "password123";

            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setName("Proper User");
            user.setEnabled(true);
            user.setRole(Role.USER);

            userRepository.save(user);

            return "✅ Proper user created!<br>" +
                    "Email: " + email + "<br>" +
                    "Password: " + rawPassword + "<br>" +
                    "Status: Enabled & Password Encoded ✅";

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/enable-user")
    public String enableUser() {
        try {
            Optional<User> userOpt = userRepository.findByEmail("user@example.com");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEnabled(true);
                userRepository.save(user);

                return "✅ User enabled!<br>Email: user@example.com<br>Password: password123";
            } else {
                return "❌ User user@example.com not found";
            }

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/check-users")
    public String checkUsers() {
        try {
            StringBuilder result = new StringBuilder("📊 All Users:<br><br>");
            userRepository.findAll().forEach(user -> {
                result.append("Email: ").append(user.getEmail())
                        .append("<br>Enabled: ").append(user.isEnabled())
                        .append("<br>Role: ").append(user.getRole())
                        .append("<br>Verification Token: ").append(user.getVerificationToken())
                        .append("<br>---<br>");
            });
            return result.toString();
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    // ========== Verification Debug ==========

    @GetMapping("/test-verification")
    public String testVerification() {
        try {
            String testEmail = "verifytest" + System.currentTimeMillis() + "@test.com";
            String token = UUID.randomUUID().toString();

            User testUser = new User();
            testUser.setName("Verification Test User");
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setEnabled(false);
            testUser.setVerificationToken(token);
            testUser.setRole(Role.USER);

            userRepository.save(testUser);

            return "✅ Verification user created!<br>" +
                    "Email: " + testEmail + "<br>Token: " + token;
        } catch (Exception e) {
            return "❌ Verification setup failed: " + e.getMessage();
        }
    }

    @GetMapping("/verify-manual")
    public String verifyManual(@RequestParam String token) {
        try {
            boolean verified = userService.confirmEmail(token);
            if (verified) {
                return "✅ Manual verification SUCCESS!<br>Token: " + token;
            } else {
                return "❌ Manual verification FAILED!<br>Token: " + token;
            }
        } catch (Exception e) {
            return "❌ Manual verification error: " + e.getMessage();
        }
    }

    @GetMapping("/check-token")
    public String checkToken(@RequestParam String token) {
        try {
            Optional<User> userOpt = userRepository.findByVerificationToken(token);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return "✅ Token found!<br>User: " + user.getEmail() +
                        "<br>Enabled: " + user.isEnabled();
            } else {
                return "❌ Token not found in database!";
            }
        } catch (Exception e) {
            return "❌ Error checking token: " + e.getMessage();
        }
    }

    // ========== Email Testing ==========

    @GetMapping("/test-email")
    public String testEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("monidhoni0007@gmail.com");
            message.setSubject("Test Email from Turf Booking");
            message.setText("This is a test email from your Spring Boot application.");
            message.setFrom("monidhoni0007@gmail.com");

            mailSender.send(message);
            return "✅ Test email sent to monidhoni0007@gmail.com!";
        } catch (Exception e) {
            return "❌ Email failed: " + e.getMessage();
        }
    }

    @GetMapping("/test-html-email")
    public String testHtmlEmail() {
        try {
            User testUser = new User();
            testUser.setEmail("monidhoni0007@gmail.com");
            testUser.setName("Test User");
            testUser.setVerificationToken(UUID.randomUUID().toString());

            emailService.sendVerificationEmail(testUser);
            return "✅ HTML test email sent to monidhoni0007@gmail.com!";
        } catch (Exception e) {
            return "❌ HTML email test failed: " + e.getMessage();
        }
    }

    @PostMapping("/debug-register")
    public String debugRegister(@RequestBody Map<String, String> userData) {
        try {
            String name = userData.get("name");
            String email = userData.get("email");
            String password = userData.get("password");

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setRole(Role.USER);

            userRepository.save(user);
            emailService.sendVerificationEmail(user);

            return "✅ Debug registration successful for " + email;
        } catch (Exception e) {
            return "❌ Debug registration failed: " + e.getMessage();
        }
    }

    @GetMapping("/info")
    public String appInfo() {
        return "🏏 Turf Booking Backend Test Controller - All systems ready.";
    }
}
