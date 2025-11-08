package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ResendEmailService resendEmailService; // Using Resend API email service
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url:https://turf-booking-pp67.onrender.com}")
    private String baseUrl;

    @Transactional
    public User registerUser(User user) {
        log.info("🔐 Attempting to register user with email: {}", user.getEmail());

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
            log.warn("❌ Email already registered: {}", user.getEmail());
            throw new RuntimeException("Email is already registered: " + user.getEmail());
        }

        try {
            user.setEmail(user.getEmail().trim().toLowerCase());
            user.setName(user.getName().trim());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setVerificationToken(UUID.randomUUID().toString());

            if (user.getRole() == null) {
                user.setRole(Role.USER);
            }

            User savedUser = userRepository.save(user);
            log.info("✅ User registered successfully: {}", savedUser.getEmail());

            String verificationUrl = baseUrl + "/api/users/verify?token=" + savedUser.getVerificationToken();

            // Attempt email via Resend API first
            try {
                resendEmailService.sendVerificationEmail(savedUser.getEmail(), verificationUrl);
                log.info("📧 Resend verification email sent to: {}", savedUser.getEmail());
            } catch (Exception e) {
                log.error("❌ Failed Resend email to {}: {}", savedUser.getEmail(), e.getMessage());
                // Fallback to SMTP email
                try {
                    emailService.sendVerificationEmail(savedUser.getEmail(), verificationUrl);
                    log.info("📧 Fallback SMTP email sent to: {}", savedUser.getEmail());
                } catch (Exception smtpEx) {
                    log.error("❌ Both Resend and SMTP email failed for {}: {}", savedUser.getEmail(), smtpEx.getMessage());
                }
            }
            return savedUser;

        } catch (Exception e) {
            log.error("❌ Error during user registration for {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    @Transactional
    public boolean confirmEmail(String token) {
        log.info("🔍 Attempting email verification with token: {}", token);

        if (token == null || token.trim().isEmpty()) {
            log.warn("❌ Missing or empty verification token");
            return false;
        }

        try {
            Optional<User> userOpt = userRepository.findByVerificationToken(token.trim());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!user.isEnabled()) {
                    user.setEnabled(true);
                    user.setVerificationToken(null);
                    userRepository.save(user);
                    log.info("✅ Email verified successfully for: {}", user.getEmail());
                } else {
                    log.info("ℹ️ User already verified: {}", user.getEmail());
                }
                return true;
            } else {
                log.warn("❌ Invalid verification token: {}", token);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error during email verification: {}", e.getMessage());
            return false;
        }
    }

    public Optional<User> loginUser(String email, String rawPassword) {
        log.info("🔐 Attempting login for email: {}", email);

        if (email == null || rawPassword == null) {
            log.warn("❌ Login attempt with null email or password");
            return Optional.empty();
        }

        try {
            Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!user.isEnabled()) {
                    log.warn("❌ Login for unverified email: {}", email);
                    throw new RuntimeException("Please verify your email before logging in.");
                }

                if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                    log.info("✅ Login successful for: {}", email);
                    return Optional.of(user);
                } else {
                    log.warn("❌ Incorrect password for: {}", email);
                }
            } else {
                log.warn("❌ User not found for email: {}", email);
            }
            return Optional.empty();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error during login for {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    public void sendPasswordResetLink(String email) {
        log.info("🔑 Password reset requested for email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        try {
            Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setResetPasswordToken(UUID.randomUUID().toString());
                userRepository.save(user);
                log.info("✅ Password reset token generated for: {}", email);

                String resetUrl = baseUrl + "/reset-password?token=" + user.getResetPasswordToken();

                try {
                    resendEmailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
                    log.info("📧 Resend password reset email sent to: {}", email);
                } catch (Exception e) {
                    log.error("❌ Resend password reset email failed for {}: {}", email, e.getMessage());
                    try {
                        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
                        log.info("📧 SMTP password reset email sent to: {}", email);
                    } catch (Exception smtpEx) {
                        log.error("❌ Both Resend and SMTP password reset email failed for {}: {}", email, smtpEx.getMessage());
                        throw new RuntimeException("Password reset email failed. Please try again.");
                    }
                }

            } else {
                log.warn("❌ No account found with email: {}", email);
                throw new RuntimeException("No account found with this email address.");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Password reset error for {}: {}", email, e.getMessage());
            throw new RuntimeException("Password reset request failed. Please try again.");
        }
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        log.info("🔑 Password reset attempt with token");

        if (token == null || token.trim().isEmpty()) {
            log.warn("❌ Missing reset token");
            return false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            log.warn("❌ New password is required");
            return false;
        }

        try {
            Optional<User> userOpt = userRepository.findByResetPasswordToken(token.trim());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode(newPassword.trim()));
                user.setResetPasswordToken(null);
                userRepository.save(user);
                log.info("✅ Password reset successful for: {}", user.getEmail());
                return true;
            } else {
                log.warn("❌ Invalid or expired reset token");
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Password reset error: {}", e.getMessage());
            return false;
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Transactional
    public boolean resendVerificationEmail(String email) {
        log.info("📧 Resending verification email to: {}", email);

        try {
            Optional<User> userOpt = userRepository.findByEmail(email.trim().toLowerCase());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.isEnabled()) {
                    log.info("ℹ️ User already verified: {}", email);
                    return true;
                }
                user.setVerificationToken(UUID.randomUUID().toString());
                userRepository.save(user);

                String verificationUrl = baseUrl + "/api/users/verify?token=" + user.getVerificationToken();
                resendEmailService.sendVerificationEmail(user.getEmail(), verificationUrl);
                log.info("✅ Resend verification email resent to: {}", email);
                return true;
            } else {
                log.warn("❌ User not found for verification resend: {}", email);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Verification resend error for {}: {}", email, e.getMessage());
            return false;
        }
    }
}
