package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);                                  // ← Require email verification
        user.setVerificationToken(UUID.randomUUID().toString()); // ← Generate verification token
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        User savedUser = userRepository.save(user);
        System.out.println("[REGISTER] New user registered: " + savedUser.getEmail() + " — awaiting email verification");

        // Send verification email
        try {
            emailService.sendVerificationEmail(savedUser);
            System.out.println("[REGISTER] Verification email sent to: " + savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("[REGISTER] Failed to send verification email to: " + savedUser.getEmail() + " — " + e.getMessage());
            // Don't fail registration if email fails; user can request resend
        }

        return savedUser;
    }

    public boolean confirmEmail(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("[VERIFY] Missing or empty token");
            return false;
        }

        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    if (!user.isEnabled()) {
                        user.setEnabled(true);
                        user.setVerificationToken(null);
                        userRepository.save(user);
                        System.out.println("[VERIFY] Email verified for: " + user.getEmail());
                    } else {
                        System.out.println("[VERIFY] User already verified: " + user.getEmail());
                    }
                    return true;
                }).orElseGet(() -> {
                    System.out.println("[VERIFY] Invalid verification token: " + token);
                    return false;
                });
    }

    public Optional<User> loginUser(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> {
                    if (!user.isEnabled()) {
                        throw new RuntimeException("Email not verified: " + email);
                    }
                    boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
                    if (!passwordMatches) {
                        System.out.println("[LOGIN] Incorrect password for: " + email);
                    }
                    return passwordMatches;
                });
    }

    public void sendPasswordResetLink(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            user.setResetPasswordToken(UUID.randomUUID().toString());
            userRepository.save(user);
            System.out.println("[RESET] Sending password reset link to: " + email);
            try {
                emailService.sendResetPasswordEmail(user);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send password reset email.");
            }
        }, () -> {
            throw new RuntimeException("No account found with that email: " + email);
        });
    }

    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            System.out.println("[RESET] Missing reset token");
            return false;
        }
        return userRepository.findByResetPasswordToken(token)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetPasswordToken(null);
                    userRepository.save(user);
                    System.out.println("[RESET] Password reset for: " + user.getEmail());
                    return true;
                }).orElse(false);
    }
}
