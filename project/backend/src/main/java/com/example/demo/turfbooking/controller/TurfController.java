package com.example.demo.turfbooking.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.UserRepository;
import com.example.demo.turfbooking.service.TurfService;
import com.example.demo.turfbooking.jwt.JwtUtil;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/turfs")
@CrossOrigin(origins = {
        "https://turf-booking-frontend.vercel.app",
        "https://turf-booking-an7sfm399-monishs-projects-29844c66.vercel.app",
        "http://localhost:3000"
}, allowCredentials = "true")
public class TurfController {

    private static final Logger logger = LoggerFactory.getLogger(TurfController.class);

    private final TurfService turfService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Cloudinary cloudinary;

    @Autowired
    public TurfController(TurfService turfService, UserRepository userRepository, JwtUtil jwtUtil, Cloudinary cloudinary) {
        this.turfService = turfService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.cloudinary = cloudinary;
    }

    // =================== Public Endpoints ===================

    @GetMapping
    public List<Turf> getAllTurfs() {
        return turfService.getAllTurfs();
    }

    @GetMapping("/public")
    public ResponseEntity<List<Turf>> getAllPublicTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    // =================== Protected Endpoints ===================

    @GetMapping("/admin")
    public ResponseEntity<?> getTurfsByAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User admin = validateAndGetUser(authHeader);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token or user");
            }

            List<Turf> turfs = turfService.getTurfsByAdmin(admin);
            return ResponseEntity.ok(turfs != null ? turfs : Collections.emptyList());

        } catch (Exception e) {
            logger.error("Unexpected error retrieving admin turfs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving admin turfs: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTurfById(@PathVariable Long id) {
        try {
            return turfService.getTurfById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Turf not found with ID: " + id));
        } catch (Exception e) {
            logger.error("Error fetching turf by id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching turf: " + e.getMessage());
        }
    }

    @PostMapping("/add-with-image")
    public ResponseEntity<?> addTurfWithImage(@RequestParam("name") String name,
                                              @RequestParam("location") String location,
                                              @RequestParam("price") Double price,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "facilities", required = false) String facilities,
                                              @RequestParam(value = "availableSlots", required = false) String availableSlots,
                                              @RequestParam("image") MultipartFile mainImage,
                                              @RequestParam(value = "images", required = false) List<MultipartFile> otherImages,
                                              @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User admin = validateAndGetUser(authHeader);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token or user");
            }

            List<String> imageUrls = new ArrayList<>();
            if (mainImage != null && !mainImage.isEmpty()) {
                imageUrls.add(uploadToCloudinary(mainImage));
            }
            if (otherImages != null) {
                for (MultipartFile image : otherImages) {
                    if (image != null && !image.isEmpty()) {
                        imageUrls.add(uploadToCloudinary(image));
                    }
                }
            }

            Turf turf = new Turf();
            turf.setName(name);
            turf.setLocation(location);
            turf.setPricePerHour(price);
            turf.setDescription(description);
            turf.setFacilities(facilities);
            turf.setAvailableSlots(availableSlots);
            turf.setImageUrls(imageUrls);
            turf.setAdmin(admin);

            Turf savedTurf = turfService.addTurf(turf);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTurf);

        } catch (Exception e) {
            logger.error("Error adding turf with images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding turf with images: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadTurfImages(@PathVariable Long id,
                                              @RequestParam("images") List<MultipartFile> images) {
        try {
            List<String> urls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        urls.add(uploadToCloudinary(image));
                    }
                }
            }
            Turf updatedTurf = turfService.addImagesToTurf(id, urls);
            return ResponseEntity.ok(updatedTurf);
        } catch (Exception e) {
            logger.error("Image upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Image upload failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTurf(@PathVariable Long id, @RequestBody Turf turf) {
        try {
            Turf updated = turfService.updateTurf(id, turf);
            return updated != null
                    ? ResponseEntity.ok(updated)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turf not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error updating turf", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating turf: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTurf(@PathVariable Long id) {
        try {
            return turfService.deleteTurf(id)
                    ? ResponseEntity.ok("Turf deleted successfully.")
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turf not found.");
        } catch (Exception e) {
            logger.error("Error deleting turf", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting turf: " + e.getMessage());
        }
    }

    @GetMapping("/test-db")
    public ResponseEntity<?> testDb() {
        try {
            return ResponseEntity.ok(turfService.getAllTurfs());
        } catch (Exception e) {
            logger.error("Error testing DB connection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error testing DB: " + e.getMessage());
        }
    }

    // =================== Helper Methods ===================

    private User validateAndGetUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            return null;
        }

        String jwt = authHeader.substring(7);
        try {
            String email = jwtUtil.extractEmail(jwt);
            if (email == null || email.isBlank()) {
                logger.error("JWT token did not contain a valid email");
                return null;
            }
            return userRepository.findByEmail(email).orElse(null);

        } catch (JwtException e) {
            logger.warn("Invalid or expired JWT: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error processing token", e);
            return null;
        }
    }

    private String uploadToCloudinary(MultipartFile file) throws Exception {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
