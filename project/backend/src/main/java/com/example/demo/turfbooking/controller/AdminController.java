package com.example.demo.turfbooking.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.turfbooking.entity.Admin;
import com.example.demo.turfbooking.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")  // Allow React frontend requests
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Admin Login
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        return adminService.loginAdmin(email, password)
                .map(admin -> ResponseEntity.ok(Map.of(
                    "id", admin.getId(),
                    "email", admin.getEmail(),
                    "message", "Admin login successful"
                )))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid admin credentials")));
    }

    // Admin Registration
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            Admin registeredAdmin = adminService.registerAdmin(admin);
            return ResponseEntity.status(201).body(Map.of(
                "id", registeredAdmin.getId(),
                "email", registeredAdmin.getEmail(),
                "message", "Admin registered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
