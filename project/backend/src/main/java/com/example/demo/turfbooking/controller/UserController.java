package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.service.EmailService;
import com.example.demo.turfbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "https://turf-booking-frontend.vercel.app",
        "http://localhost:3000"
})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(201).body(Map.of(
                    "email", newUser.getEmail(),
                    "message", "Registration successful. Please verify your email."
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify email with token and redirect.
     */
    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        boolean verified = userService.confirmEmail(token);

        if (verified) {
            // Redirect to login page directly
            response.sendRedirect("https://turf-booking-seven.vercel.app/login");
        } else {
            // Redirect to error page with query parameter
            response.sendRedirect("https://turf-booking-seven.vercel.app/email-verified?status=error");
        }
    }

    /**
     * Login user.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return userService.loginUser(email, password)
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "role", user.getRole(),
                            "email", user.getEmail()
                    ));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials or email not verified.")));
    }

    /**
     * Forgot password.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            userService.sendPasswordResetLink(email);
            return ResponseEntity.ok(Map.of("message", "Reset link sent to your email."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset password.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        boolean result = userService.resetPassword(token, newPassword);
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Password reset successful."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token."));
        }
    }

    /**
     * Test email.
     */
    @GetMapping("/test-mail")
    public ResponseEntity<?> sendTestMail() {
        try {
            emailService.sendEmail("monidhoni0007@gmail.com", "Test Email", "This is a test email.");
            return ResponseEntity.ok("Test email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send test email: " + e.getMessage());
        }
    }
}
