package com.example.demo.turfbooking.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.UserRepository;
import com.example.demo.turfbooking.service.TurfService;
import com.example.demo.turfbooking.jwt.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/turfs")
@CrossOrigin(origins = "http://localhost:3000")
public class TurfController {

    private final TurfService turfService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    public TurfController(TurfService turfService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.turfService = turfService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Turf> getAllTurfs() {
        return turfService.getAllTurfs();
    }

    @GetMapping("/public")
    public ResponseEntity<List<Turf>> getAllPublicTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

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

    // ✅ Add turf with image
    @PostMapping("/add-with-image")
    public ResponseEntity<?> addTurfWithImage(@RequestParam("name") String name,
                                              @RequestParam("location") String location,
                                              @RequestParam("price") Double price,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "facilities", required = false) String facilities,
                                              @RequestParam(value = "availableSlots", required = false) String availableSlots,
                                              @RequestParam("image") MultipartFile image,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);

            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            // Create and save Turf
            Turf turf = new Turf();
            turf.setName(name);
            turf.setLocation(location);
            turf.setPricePerHour(price);
            turf.setDescription(description);
            turf.setFacilities(facilities);
            turf.setAvailableSlots(availableSlots);
            turf.setImageUrls(List.of(imageUrl)); // ✅ FIXED HERE
            turf.setAdmin(admin);

            Turf savedTurf = turfService.addTurf(turf);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTurf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error adding turf with image: " + e.getMessage());
        }
    }

    // ✅ Upload additional images
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadTurfImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        try {
            List<String> urls = turfService.uploadImagesForTurf(id, images);
            return ResponseEntity.ok().body("Uploaded successfully: " + urls.size() + " images.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }

    // ✅ Update turf
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

    // ✅ DB Test
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
