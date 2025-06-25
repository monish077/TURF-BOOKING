package com.example.demo.turfbooking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.service.TurfService;

@RestController
@RequestMapping("/api/turfs")
@CrossOrigin(origins = "http://localhost:3000")
public class TurfController {

    private final TurfService turfService;

    @Autowired
    public TurfController(TurfService turfService) {
        this.turfService = turfService;
    }

    @GetMapping
    public List<Turf> getAllTurfs() {
        return turfService.getAllTurfs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTurfById(@PathVariable Long id) {
        try {
            Optional<Turf> turf = turfService.getTurfById(id);
            if (turf.isPresent()) {
                return ResponseEntity.ok(turf.get());
            } else {
                return ResponseEntity.status(404).body("Turf not found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving turf: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> addTurf(@RequestBody Turf turf) {
        try {
            turfService.addTurf(turf);
            return ResponseEntity.status(201).body("Turf added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding turf: " + e.getMessage());
        }
    }

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
