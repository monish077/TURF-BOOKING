package com.example.demo.turfbooking.filter;

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

    // Endpoints exempt from JWT check
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/users/login",
            "/api/users/register",
            "/api/users/verify",
            "/api/users/email-verified",
            "/api/users/forgot-password",
            "/api/users/reset-password",
            "/api/users/test-mail",
            "/api/turfs/public",
            "/api/auth/refresh"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String requestPath = request.getRequestURI();

            // Skip OPTIONS requests
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Skip JWT check for exempted endpoints
            if (EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            String email = null;
            String jwt = null;

            // Extract JWT token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                try {
                    email = jwtUtil.extractEmail(jwt);
                } catch (Exception e) {
                    logger.warn("JWT parsing error: " + e.getMessage(), e);
                }
            }

            // If email extracted and no auth set, validate and set authentication
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

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
            // Proceed without blocking request
            //monish
        }

        filterChain.doFilter(request, response);
    }
}
