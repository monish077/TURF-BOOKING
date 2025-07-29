package com.example.demo.turfbooking.service;

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
     * ✅ Load user by email for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔍 Check for null or empty email
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email must not be null or empty");
        }

        // 🔍 Fetch user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        // ❌ Block if user is not verified
        if (!user.isEnabled()) {
            throw new DisabledException("Email not verified. Please verify before login.");
        }

        // ✅ Create authority (Spring expects "ROLE_" prefix)
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // ✅ Return Spring Security's User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
