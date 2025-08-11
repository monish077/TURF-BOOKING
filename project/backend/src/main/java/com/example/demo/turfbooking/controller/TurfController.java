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

import java.util.ArrayList;
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

            return ResponseEntity.ok(turfService.getTurfsByAdmin(admin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving admin turfs: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTurfById(@PathVariable Long id) {
        Optional<Turf> turf = turfService.getTurfById(id);
        return turf.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Turf not found with ID: " + id));
    }

    /**
     * ✅ Add turf with multiple images (handles main + additional images in one request)
     */
    @PostMapping("/add-with-image")
    public ResponseEntity<?> addTurfWithImage(@RequestParam("name") String name,
                                              @RequestParam("location") String location,
                                              @RequestParam("price") Double price,
                                              @RequestParam(value = "description", required = false) String description,
                                              @RequestParam(value = "facilities", required = false) String facilities,
                                              @RequestParam(value = "availableSlots", required = false) String availableSlots,
                                              @RequestParam("image") MultipartFile mainImage,
                                              @RequestParam(value = "images", required = false) List<MultipartFile> otherImages,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractUsername(jwt);
            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            List<String> imageUrls = new ArrayList<>();

            // Upload main image first
            if (mainImage != null && !mainImage.isEmpty()) {
                Map<String, Object> uploadResult = cloudinary.uploader()
                        .upload(mainImage.getBytes(), ObjectUtils.emptyMap());
                imageUrls.add((String) uploadResult.get("secure_url"));
            }

            // Upload other images if provided
            if (otherImages != null) {
                for (MultipartFile image : otherImages) {
                    if (image != null && !image.isEmpty()) {
                        Map<String, Object> uploadResult = cloudinary.uploader()
                                .upload(image.getBytes(), ObjectUtils.emptyMap());
                        imageUrls.add((String) uploadResult.get("secure_url"));
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

            return ResponseEntity.status(HttpStatus.CREATED).body(turfService.addTurf(turf));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding turf with images: " + e.getMessage());
        }
    }

    /**
     * ✅ Upload additional images to an existing turf
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadTurfImages(@PathVariable Long id,
                                              @RequestParam("images") List<MultipartFile> images) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    Map<String, Object> uploadResult = cloudinary.uploader()
                            .upload(image.getBytes(), ObjectUtils.emptyMap());
                    urls.add((String) uploadResult.get("secure_url"));
                }
            }
            return ResponseEntity.ok(turfService.addImagesToTurf(id, urls));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Image upload failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTurf(@PathVariable Long id, @RequestBody Turf turf) {
        try {
            Turf updated = turfService.updateTurf(id, turf);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turf not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating turf: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTurf(@PathVariable Long id) {
        try {
            if (turfService.deleteTurf(id)) {
                return ResponseEntity.ok("Turf deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turf not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting turf: " + e.getMessage());
        }
    }

    @GetMapping("/test-db")
    public ResponseEntity<?> testDb() {
        try {
            return ResponseEntity.ok(turfService.getAllTurfs());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error testing DB: " + e.getMessage());
        }
    }
}
