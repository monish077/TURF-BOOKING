package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Role;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ Load user by email (username) for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email must not be null or empty");
        }

        // 🔍 Fetch user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        // ❌ Block unverified accounts
        if (!user.isEnabled()) {
            throw new DisabledException("Email not verified. Please verify before login.");
        }

        // ✅ Guard against null role
        Role role = user.getRole();
        if (role == null) {
            throw new UsernameNotFoundException("User role is not set for email: " + email);
        }

        // ✅ Spring expects roles prefixed with "ROLE_"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
