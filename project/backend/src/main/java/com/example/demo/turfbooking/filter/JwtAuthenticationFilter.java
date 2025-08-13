package com.example.demo.turfbooking.jwt;

import com.example.demo.turfbooking.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // ✅ Endpoints that do not require JWT authentication
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/users/login",
            "/api/users/register",
            "/api/users/verify",
            "/api/users/email-verified",
            "/api/users/forgot-password",
            "/api/users/reset-password",
            "/api/users/test-mail",
            "/api/turfs/public",
            "/api/auth/refresh" // <-- allow refresh without login
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String requestPath = request.getRequestURI();

            // ✅ Skip JWT check for OPTIONS (CORS preflight) requests
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            // ✅ Skip JWT check for public endpoints
            if (EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            String email = null;
            String jwt = null;

            // ✅ Extract JWT token from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                try {
                    email = jwtUtil.extractEmail(jwt);
                } catch (Exception e) {
                    logger.warn("JWT parsing error: " + e.getMessage(), e);
                }
            }

            // ✅ If email is extracted and no authentication is set
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    // ✅ Validate token against user details
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        logger.warn("Invalid JWT token for user: " + email);
                    }
                } catch (Exception e) {
                    logger.error("Error loading user details for email: " + email, e);
                }
            }

        } catch (Exception e) {
            logger.error("JWT filter error: " + e.getMessage(), e);
            // Don't block request, just proceed without authentication
        }

        // ✅ Continue filter chain regardless
        filterChain.doFilter(request, response);
    }
}
