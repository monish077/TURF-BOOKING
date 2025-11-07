package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with encoded password, disabled status, and verification token.
     * Sends verification email after saving user.
     */
    @Transactional
    public User registerUser(User user) {
        log.info("🔐 Attempting to register user with email: {}", user.getEmail());
        
        // Validate input
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase())) {
            log.warn("❌ Email already registered: {}", user.getEmail());
            throw new RuntimeException("Email is already registered: " + user.getEmail());
        }

        try {
            // Set user properties
            user.setEmail(user.getEmail().trim().toLowerCase());
            user.setName(user.getName().trim());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setVerificationToken(UUID.randomUUID().toString());

            if (user.getRole() == null) {
                user.setRole(Role.USER);
            }

            // Save user
            User savedUser = userRepository.save(user);
            log.info("✅ User registered successfully: {}", savedUser.getEmail());

            // Send verification email
            try {
                emailService.sendVerificationEmail(savedUser);
                log.info("📧 Verification email sent to: {}", savedUser.getEmail());
            } catch (Exception e) {
                log.error("❌ Failed to send verification email to {}: {}", savedUser.getEmail(), e.getMessage());
                // Don't throw exception here - user is created, they can request another verification email
            }

            return savedUser;

        } catch (Exception e) {
            log.error("❌ Error during user registration for {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Confirms user email based on verification token.
     * Enables user account if token is valid.
     */
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
                    user.setVerificationToken(null); // Clear the token after verification
                    userRepository.save(user);
                    log.info("✅ Email verified successfully for: {}", user.getEmail());
                    return true;
                } else {
                    log.info("ℹ️ User already verified: {}", user.getEmail());
                    return true; // Already verified
                }
            } else {
                log.warn("❌ Invalid verification token: {}", token);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error during email verification: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates user by email and password, enforcing email verification.
     */
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
                
                // Check if email is verified
                if (!user.isEnabled()) {
                    log.warn("❌ Login attempt for unverified email: {}", email);
                    throw new RuntimeException("Please verify your email before logging in. Check your inbox for verification link.");
                }

                // Check password
                boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
                if (passwordMatches) {
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
            throw e; // Re-throw verification exceptions
        } catch (Exception e) {
            log.error("❌ Error during login for {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sends password reset email link to user.
     */
    @Transactional
    public void sendPasswordResetLink(String email) {
        log.info("🔑 Requesting password reset for email: {}", email);
        
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

                try {
                    emailService.sendResetPasswordEmail(user);
                    log.info("📧 Password reset email sent to: {}", email);
                } catch (Exception e) {
                    log.error("❌ Failed to send password reset email to {}: {}", email, e.getMessage());
                    throw new RuntimeException("Failed to send password reset email. Please try again.");
                }
            } else {
                log.warn("❌ No account found with email: {}", email);
                throw new RuntimeException("No account found with this email address.");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Error during password reset request for {}: {}", email, e.getMessage());
            throw new RuntimeException("Password reset request failed. Please try again.");
        }
    }

    /**
     * Resets user's password if valid reset token provided.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        log.info("🔑 Attempting password reset with token");
        
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
                user.setResetPasswordToken(null); // Clear the token after use
                userRepository.save(user);
                
                log.info("✅ Password reset successful for: {}", user.getEmail());
                return true;
            } else {
                log.warn("❌ Invalid or expired reset token");
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error during password reset: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Find user by email (utility method)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Find user by ID (utility method)
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Check if email exists (utility method)
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    /**
     * Resend verification email
     */
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
                
                // Generate new verification token
                user.setVerificationToken(UUID.randomUUID().toString());
                userRepository.save(user);
                
                // Send verification email
                emailService.sendVerificationEmail(user);
                log.info("✅ Verification email resent to: {}", email);
                return true;
            } else {
                log.warn("❌ User not found for verification resend: {}", email);
                return false;
            }
        } catch (Exception e) {
            log.error("❌ Error resending verification email to {}: {}", email, e.getMessage());
            return false;
        }
    }
}