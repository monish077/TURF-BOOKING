package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.jwt.JwtUtil;
import com.example.demo.turfbooking.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    /**
     * 📝 Registers a new user with encoded password, disabled status, and verification token.
     * Sends a verification email after saving.
     */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("❌ Email is already registered: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        User savedUser = userRepository.save(user);
        System.out.println("✅ Registered new user: " + savedUser.getEmail());

        emailService.sendVerificationEmail(savedUser);
        return savedUser;
    }

    /**
     * 📧 Confirms the user email based on the verification token.
     * Enables the user account if the token is valid.
     */
    @Transactional
    public boolean confirmEmail(String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    if (!user.isEnabled()) {
                        user.setEnabled(true);
                        user.setVerificationToken(null);
                        userRepository.save(user);
                        System.out.println("✅ Email verified for user: " + user.getEmail());
                    } else {
                        System.out.println("⚠️ User already verified: " + user.getEmail());
                    }
                    return true;
                })
                .orElseGet(() -> {
                    System.out.println("❌ Invalid verification token: " + token);
                    return false;
                });
    }

    /**
     * 🔐 Authenticates user by email and password.
     * Throws an error if email not verified.
     */
    public Optional<User> loginUser(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> {
                    if (!user.isEnabled()) {
                        throw new RuntimeException("❌ Email not verified: " + email);
                    }
                    return passwordEncoder.matches(rawPassword, user.getPassword());
                });
    }

    /**
     * 🔑 Sends password reset link by generating a reset token and emailing it.
     */
    @Transactional
    public void sendPasswordResetLink(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            user.setResetPasswordToken(UUID.randomUUID().toString());
            userRepository.save(user);
            System.out.println("📧 Sending password reset link to: " + email);
            emailService.sendResetPasswordEmail(user);
        }, () -> {
            throw new RuntimeException("❌ No account found with that email: " + email);
        });
    }

    /**
     * 🔄 Resets the password given a valid reset token and new password.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        return userRepository.findByResetPasswordToken(token)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetPasswordToken(null);
                    userRepository.save(user);
                    System.out.println("✅ Password reset for: " + user.getEmail());
                    return true;
                })
                .orElse(false);
    }
}
