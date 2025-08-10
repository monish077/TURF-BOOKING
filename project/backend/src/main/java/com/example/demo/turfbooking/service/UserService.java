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
     * Registers a new user with encoded password, disabled status and a verification token.
     * Sends a verification email after saving.
     */
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(savedUser);
        return savedUser;
    }

    /**
     * Confirms the user email based on the verification token.
     * Enables the user account if token is valid.
     */
    public boolean confirmEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isEnabled()) {
                user.setEnabled(true);
                user.setVerificationToken(null);
                userRepository.save(user);
                System.out.println("✅ Email verified for user: " + user.getEmail());
            } else {
                System.out.println("⚠️ Already verified: " + user.getEmail());
            }
            return true;
        }
        System.out.println("❌ Invalid verification token: " + token);
        return false;
    }

    /**
     * Authenticates user by email and password.
     * Throws if email not verified.
     */
    public Optional<User> loginUser(String email, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (!user.isEnabled()) {
                throw new RuntimeException("Email not verified.");
            }

            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Sends password reset link by generating a reset token and emailing it.
     */
    public void sendPasswordResetLink(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setResetPasswordToken(UUID.randomUUID().toString());
            userRepository.save(user);
            emailService.sendResetPasswordEmail(user);
        } else {
            throw new RuntimeException("No account found with that email.");
        }
    }

    /**
     * Resets the password given a valid reset token and new password.
     */
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> optionalUser = userRepository.findByResetPasswordToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetPasswordToken(null);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}
