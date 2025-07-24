package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.Admin;
import com.example.demo.turfbooking.service.AdminService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")  // Allow React frontend requests
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ Admin Login
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

    // ✅ Admin Registration with Validation
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody Admin admin, BindingResult result) {
        if (result.hasErrors()) {
            // Return first validation error message
            return ResponseEntity.badRequest().body(Map.of("error", result.getAllErrors().get(0).getDefaultMessage()));
        }

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
