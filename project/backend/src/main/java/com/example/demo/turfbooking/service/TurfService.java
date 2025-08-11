package com.example.demo.turfbooking.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class TurfService {

    private final TurfRepository turfRepo;
    private final Cloudinary cloudinary;

    @Autowired
    public TurfService(TurfRepository turfRepo, Cloudinary cloudinary) {
        this.turfRepo = turfRepo;
        this.cloudinary = cloudinary;
    }

    /** -------------------------
     * CRUD Methods
     * ------------------------- */
    public List<Turf> getAllTurfs() {
        return turfRepo.findAll();
    }

    public List<Turf> getTurfsByAdmin(User admin) {
        return turfRepo.findByAdmin(admin);
    }

    public Optional<Turf> getTurfById(Long id) {
        return turfRepo.findById(id);
    }

    public Turf addTurf(Turf turf) {
        return turfRepo.save(turf);
    }

    public boolean deleteTurf(Long id) {
        if (turfRepo.existsById(id)) {
            turfRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public Turf updateTurf(Long id, Turf updatedTurf) {
        return turfRepo.findById(id).map(existingTurf -> {
            existingTurf.setName(updatedTurf.getName());
            existingTurf.setLocation(updatedTurf.getLocation());
            existingTurf.setPricePerHour(updatedTurf.getPricePerHour());
            existingTurf.setDescription(updatedTurf.getDescription());
            existingTurf.setFacilities(updatedTurf.getFacilities());
            existingTurf.setAvailableSlots(updatedTurf.getAvailableSlots());
            return turfRepo.save(existingTurf);
        }).orElse(null);
    }

    /** -------------------------
     * Image Upload Methods
     * ------------------------- */

    // Upload to Cloudinary and return URL
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty or null.");
        }
        Map<String, Object> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }

    // Upload multiple images for existing turf
    public List<String> uploadImagesForTurf(Long turfId, List<MultipartFile> images) throws IOException {
        Turf turf = turfRepo.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with ID: " + turfId));

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            uploadedUrls.add(uploadToCloudinary(image));
        }

        List<String> currentImages = turf.getImageUrls();
        if (currentImages == null) currentImages = new ArrayList<>();
        currentImages.addAll(uploadedUrls);
        turf.setImageUrls(currentImages);

        turfRepo.save(turf);
        return uploadedUrls;
    }

    // Add pre-uploaded image URLs
    public Turf addImagesToTurf(Long turfId, List<String> imageUrls) {
        Turf turf = turfRepo.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with ID: " + turfId));

        List<String> currentImages = turf.getImageUrls();
        if (currentImages == null) currentImages = new ArrayList<>();
        currentImages.addAll(imageUrls);
        turf.setImageUrls(currentImages);

        return turfRepo.save(turf);
    }

    // Create turf directly with image uploads
    public Turf createTurfWithImages(Turf turf, List<MultipartFile> images) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            uploadedUrls.add(uploadToCloudinary(image));
        }
        turf.setImageUrls(uploadedUrls);
        return turfRepo.save(turf);
    }
}
