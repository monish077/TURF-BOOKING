package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.repository.UserRepository;
import com.example.demo.turfbooking.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.turfbooking.service.UserService;

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
            
            // Set role
            try {
                Class<?> roleClass = Class.forName("com.example.demo.turfbooking.entity.Role");
                Object userRole = Enum.valueOf((Class<Enum>) roleClass, "USER");
                user.getClass().getMethod("setRole", roleClass).invoke(user, userRole);
            } catch (Exception e) {
                System.out.println("⚠️ Could not set role");
            }
            
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
            user.setPassword(passwordEncoder.encode(rawPassword)); // Properly encoded
            user.setName("Proper User");
            user.setEnabled(true);
            
            // Set role
            try {
                Class<?> roleClass = Class.forName("com.example.demo.turfbooking.entity.Role");
                Object userRole = Enum.valueOf((Class<Enum>) roleClass, "USER");
                user.getClass().getMethod("setRole", roleClass).invoke(user, userRole);
            } catch (Exception e) {
                System.out.println("⚠️ Could not set role");
            }
            
            User saved = userRepository.save(user);
            
            return "✅ Proper user created!<br>" +
                   "Email: " + email + "<br>" +
                   "Password: " + rawPassword + "<br>" +
                   "Status: Enabled & Password Encoded ✅<br>" +
                   "Use these credentials to login";
            
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/enable-user")
    public String enableUser() {
        try {
            // Enable user@example.com
            Optional<User> userOpt = userRepository.findByEmail("user@example.com");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setEnabled(true);
                userRepository.save(user);
                
                return "✅ User enabled!<br>" +
                       "Email: user@example.com<br>" +
                       "Password: password123<br>" +
                       "You can now login with these credentials";
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
            StringBuilder result = new StringBuilder();
            result.append("📊 All Users:<br><br>");
            
            userRepository.findAll().forEach(user -> {
                result.append("Email: ").append(user.getEmail())
                      .append("<br>Enabled: ").append(user.isEnabled())
                      .append("<br>Has Role: ").append(hasRole(user))
                      .append("<br>Verification Token: ").append(user.getVerificationToken())
                      .append("<br>---<br>");
            });
            
            return result.toString();
            
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    // ==================== VERIFICATION DEBUG ENDPOINTS ====================
    
    @GetMapping("/test-verification")
    public String testVerification() {
        try {
            // Create a test user with verification token
            User testUser = new User();
            String testEmail = "verifytest" + System.currentTimeMillis() + "@test.com";
            String token = UUID.randomUUID().toString();
            
            testUser.setName("Verification Test User");
            testUser.setEmail(testEmail);
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setEnabled(false);
            testUser.setVerificationToken(token);
            testUser.setRole(Role.USER);
            
            User savedUser = userRepository.save(testUser);
            
            String backendUrl = "http://localhost:8080/api/users/verify?token=" + token;
            String frontendUrl = "https://turf-booking-seven.vercel.app/verify-email?token=" + token;
            String manualUrl = "http://localhost:8080/api/test/verify-manual?token=" + token;
            
            return "🔧 Verification Test Setup:<br>" +
                   "User Created: " + testEmail + "<br>" +
                   "Token: " + token + "<br>" +
                   "Initial Enabled Status: " + savedUser.isEnabled() + "<br><br>" +
                   "Test URLs:<br>" +
                   "1. <a href=\"" + backendUrl + "\" target=\"_blank\">Backend Direct</a><br>" +
                   "2. <a href=\"" + frontendUrl + "\" target=\"_blank\">Frontend Page</a><br>" +
                   "3. <a href=\"" + manualUrl + "\" target=\"_blank\">Manual Test</a><br><br>" +
                   "After testing, check <a href=\"/api/test/check-token?token=" + token + "\">token status</a>";
            
        } catch (Exception e) {
            return "❌ Verification test failed: " + e.getMessage();
        }
    }
    
    @GetMapping("/verify-manual")
    public String verifyManual(@RequestParam String token) {
        try {
            System.out.println("🔧 Manual verification attempt for token: " + token);
            
            boolean verified = userService.confirmEmail(token);
            
            if (verified) {
                return "✅ Manual verification SUCCESS!<br>" +
                       "Token: " + token + "<br>" +
                       "User has been enabled and can now login.";
            } else {
                return "❌ Manual verification FAILED!<br>" +
                       "Token: " + token + "<br>" +
                       "Check backend logs for details.";
            }
            
        } catch (Exception e) {
            System.err.println("❌ Manual verification error: " + e.getMessage());
            e.printStackTrace();
            return "❌ Manual verification error: " + e.getMessage();
        }
    }
    
    @GetMapping("/check-token")
    public String checkToken(@RequestParam String token) {
        try {
            System.out.println("🔍 Checking token: " + token);
            
            Optional<User> userOpt = userRepository.findByVerificationToken(token);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return "✅ Token found!<br>" +
                       "User: " + user.getEmail() + "<br>" +
                       "Enabled: " + user.isEnabled() + "<br>" +
                       "Token: " + user.getVerificationToken() + "<br>" +
                       "Name: " + user.getName() + "<br>" +
                       "Role: " + user.getRole();
            } else {
                return "❌ Token not found in database!<br>" +
                       "Token: " + token + "<br>" +
                       "This token doesn't exist or has already been used.";
            }
            
        } catch (Exception e) {
            return "❌ Error checking token: " + e.getMessage();
        }
    }
    
    @GetMapping("/check-specific-token")
    public String checkSpecificToken() {
        try {
            String token = "d261fd7f-2fe3-4ad3-82f1-7d723b192d4b";
            System.out.println("🔍 Checking specific token: " + token);
            
            Optional<User> userOpt = userRepository.findByVerificationToken(token);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return "✅ Token found!<br>" +
                       "User: " + user.getEmail() + "<br>" +
                       "Enabled: " + user.isEnabled() + "<br>" +
                       "Token: " + user.getVerificationToken() + "<br>" +
                       "Name: " + user.getName() + "<br>" +
                       "Role: " + user.getRole() + "<br><br>" +
                       "<a href=\"/api/test/verify-manual?token=" + token + "\">Verify this user manually</a>";
            } else {
                return "❌ Token not found in database!<br>" +
                       "Token: " + token + "<br>" +
                       "This token doesn't exist or has already been used.";
            }
            
        } catch (Exception e) {
            return "❌ Error checking token: " + e.getMessage();
        }
    }
    
    // ==================== EMAIL TESTING ENDPOINTS ====================
    
    @GetMapping("/test-email")
    public String testEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("monidhoni0007@gmail.com");
            message.setSubject("Test Email from Turf Booking");
            message.setText("This is a test email from your Spring Boot application.\n\nIf you receive this, email service is working!");
            message.setFrom("monidhoni0007@gmail.com");
            
            System.out.println("📧 Attempting to send test email to: monidhoni0007@gmail.com");
            
            mailSender.send(message);
            
            System.out.println("✅ Test email sent successfully!");
            
            return "✅ Test email sent to monidhoni0007@gmail.com!<br>" +
                   "Check your inbox and spam folder.<br>" +
                   "If not received, check application logs for errors.";
            
        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
            e.printStackTrace();
            
            return "❌ Email failed: " + e.getMessage() + "<br><br>" +
                   "Check:<br>" +
                   "1. Gmail App Password is correct<br>" +
                   "2. Less secure apps is enabled<br>" +
                   "3. No 2-factor authentication issues<br>" +
                   "4. Check application logs for detailed error";
        }
    }
    
    @GetMapping("/test-html-email")
    public String testHtmlEmail() {
        try {
            // Create a test user
            User testUser = new User();
            testUser.setEmail("monidhoni0007@gmail.com"); // Send to yourself
            testUser.setName("Test User");
            testUser.setVerificationToken(UUID.randomUUID().toString());
            
            System.out.println("📧 Testing HTML email service...");
            emailService.sendVerificationEmail(testUser);
            
            return "✅ HTML test email sent to monidhoni0007@gmail.com! Check your inbox and spam folder.";
            
        } catch (Exception e) {
            System.err.println("❌ HTML email test failed: " + e.getMessage());
            e.printStackTrace();
            return "❌ HTML email test failed: " + e.getMessage();
        }
    }
    
    @GetMapping("/email-config")
    public String emailConfig() {
        try {
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
            
            StringBuilder config = new StringBuilder();
            config.append("📧 Email Configuration:\n");
            config.append("Host: ").append(mailSenderImpl.getHost()).append("\n");
            config.append("Port: ").append(mailSenderImpl.getPort()).append("\n");
            config.append("Username: ").append(mailSenderImpl.getUsername()).append("\n");
            config.append("JavaMail Properties: ").append(mailSenderImpl.getJavaMailProperties()).append("\n");
            
            System.out.println(config.toString());
            
            return "✅ Email configuration logged to console. Check backend logs.";
            
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/test-registration-email")
    public String testRegistrationEmail(@RequestBody Map<String, String> userData) {
        try {
            String name = userData.get("name");
            String email = userData.get("email");
            
            User testUser = new User();
            testUser.setName(name);
            testUser.setEmail(email);
            testUser.setVerificationToken(UUID.randomUUID().toString());
            
            System.out.println("🔧 Testing registration email flow...");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Token: " + testUser.getVerificationToken());
            
            emailService.sendVerificationEmail(testUser);
            
            return "✅ Registration email test successful! Check email: " + email;
            
        } catch (Exception e) {
            System.err.println("❌ Registration email test failed: " + e.getMessage());
            e.printStackTrace();
            return "❌ Registration email test failed: " + e.getMessage();
        }
    }
    
    @PostMapping("/debug-register")
    public String debugRegister(@RequestBody Map<String, String> userData) {
        try {
            String name = userData.get("name");
            String email = userData.get("email");
            String password = userData.get("password");
            
            System.out.println("🔧 DEBUG REGISTRATION RECEIVED:");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);
            
            // Create user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(false);
            
            // Generate verification token
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setRole(Role.USER);
            
            User saved = userRepository.save(user);
            System.out.println("✅ User saved with ID: " + saved.getId());
            
            // Send verification email
            System.out.println("📧 Attempting to send verification email...");
            emailService.sendVerificationEmail(user);
            
            return "✅ Debug registration successful! Check console logs and email.<br>" +
                   "Token: " + token + "<br>" +
                   "Verification URL: https://turf-booking-seven.vercel.app/verify-email?token=" + token;
            
        } catch (Exception e) {
            System.err.println("❌ Debug registration failed: " + e.getMessage());
            e.printStackTrace();
            return "❌ Debug registration failed: " + e.getMessage();
        }
    }
    
    @GetMapping("/fix-login")
    public String fixLogin() {
        return "🔧 To fix login:<br>" +
               "1. <a href=\"/api/test/enable-user\">Enable user@example.com</a><br>" +
               "2. <a href=\"/api/test/create-proper-user\">Create new proper user</a><br>" +
               "3. Then login with enabled credentials";
    }
    
    @GetMapping("/info")
    public String appInfo() {
        return "🏏 Turf Booking Backend<br>" +
               "Available Test Endpoints:<br>" +
               "- /api/test/connection (Check MongoDB)<br>" +
               "- /api/test/create-user (Create test user)<br>" +
               "- /api/test/create-proper-user (Create encoded user)<br>" +
               "- /api/test/enable-user (Enable user)<br>" +
               "- /api/test/check-users (View all users)<br>" +
               "- /api/test/test-verification (Test verification flow)<br>" +
               "- /api/test/check-specific-token (Check your token)<br>" +
               "- /api/test/verify-manual (Manual verification)<br>" +
               "- /api/test/test-email (Test basic email)<br>" +
               "- /api/test/test-html-email (Test HTML email)<br>" +
               "- /api/test/email-config (Check email config)<br>" +
               "- /api/test/debug-register (Debug registration)";
    }
    
    private boolean hasRole(User user) {
        try {
            Object role = user.getClass().getMethod("getRole").invoke(user);
            return role != null;
        } catch (Exception e) {
            return false;
        }
    }
}