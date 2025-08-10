package com.example.demo.turfbooking.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Alias method for compatibility with controller
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey secretKey;

    // Initialize the secret key after bean construction
    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); // Uses HS512 algorithm
    }

    // Generate a JWT token containing email as subject and role as claim
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Extract all claims from the JWT token
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired JWT token", e);
        }
    }

    // Extract the email (subject) from the JWT token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract the user role from the JWT token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validate the token by matching email and checking expiration
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
