package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.service.EmailService;
import com.example.demo.turfbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
        origins = {
                "https://turf-booking-frontend.vercel.app",
                "https://turf-booking-seven.vercel.app",
                "http://localhost:3000"
        },
        allowCredentials = "true"
)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.status(201).body(Map.of(
                    "email", newUser.getEmail(),
                    "message", "Registration successful. Please verify your email."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean verified = userService.confirmEmail(token);
        String acceptHeader = request.getHeader("Accept");
        boolean isApiRequest = acceptHeader != null && acceptHeader.contains("application/json");

        if (isApiRequest) {
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"" + (verified ? "success" : "error") + "\"}");
        } else {
            if (verified) {
                response.sendRedirect("https://turf-booking-seven.vercel.app/login");
            } else {
                response.sendRedirect("https://turf-booking-seven.vercel.app/email-verified?status=error");
            }
        }
    }

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            userService.sendPasswordResetLink(email);
            return ResponseEntity.ok(Map.of("message", "Reset link sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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

    @GetMapping("/test-mail")
    public ResponseEntity<?> sendTestMail() {
        try {
            emailService.sendTestEmail("monidhoni0007@gmail.com");
            return ResponseEntity.ok("Test email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send test email: " + e.getMessage());
        }
    }
}
