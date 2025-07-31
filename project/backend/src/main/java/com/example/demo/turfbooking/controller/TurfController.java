package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.UserRepository;
import com.example.demo.turfbooking.service.TurfService;
import com.example.demo.turfbooking.jwt.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/turfs")
@CrossOrigin(origins = "http://localhost:3000")
public class TurfController {

    private final TurfService turfService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public TurfController(TurfService turfService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.turfService = turfService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ❌ Admin only - not used in frontend
    @GetMapping
    public List<Turf> getAllTurfs() {
        return turfService.getAllTurfs();
    }

    // ✅ PUBLIC: Slot page — returns all turfs
    @GetMapping("/public")
    public ResponseEntity<List<Turf>> getAllPublicTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    // ✅ ADMIN: Get turfs by admin (JWT)
    @GetMapping("/admin")
    public ResponseEntity<?> getTurfsByAdmin(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);

            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            List<Turf> adminTurfs = turfService.getTurfsByAdmin(admin);
            return ResponseEntity.ok(adminTurfs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving admin turfs: " + e.getMessage());
        }
    }

    // ✅ Get single turf by ID — must come AFTER /public, /admin
    @GetMapping("/{id}")
    public ResponseEntity<?> getTurfById(@PathVariable Long id) {
        try {
            Optional<Turf> turf = turfService.getTurfById(id);
            return turf.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body("Turf not found with ID: " + id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving turf: " + e.getMessage());
        }
    }

    // ✅ Add turf (ADMIN only)
    @PostMapping
    public ResponseEntity<String> addTurf(@RequestBody Turf turf, @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);

            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            turf.setAdmin(admin);
            turfService.addTurf(turf);
            return ResponseEntity.status(201).body("Turf added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding turf: " + e.getMessage());
        }
    }

    // ✅ Update turf by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTurf(@PathVariable Long id, @RequestBody Turf turf) {
        try {
            Turf updated = turfService.updateTurf(id, turf);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(404).body("Turf not found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error updating turf: " + e.getMessage());
        }
    }

    // ✅ Delete turf
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTurf(@PathVariable Long id) {
        try {
            boolean deleted = turfService.deleteTurf(id);
            if (deleted) {
                return ResponseEntity.ok("Turf deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Turf not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting turf: " + e.getMessage());
        }
    }

    // ✅ DB test endpoint
    @GetMapping("/test-db")
    public ResponseEntity<?> testDb() {
        try {
            return ResponseEntity.ok(turfService.getAllTurfs());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error testing DB: " + e.getMessage());
        }
    }
}
