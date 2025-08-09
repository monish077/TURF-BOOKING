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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TurfService {

    private final TurfRepository turfRepo;
    private final Cloudinary cloudinary;

    @Autowired
    public TurfService(TurfRepository turfRepo, Cloudinary cloudinary) {
        this.turfRepo = turfRepo;
        this.cloudinary = cloudinary;
    }

    // Get all turfs
    public List<Turf> getAllTurfs() {
        return turfRepo.findAll();
    }

    // Get turfs by admin
    public List<Turf> getTurfsByAdmin(User admin) {
        return turfRepo.findByAdmin(admin);
    }

    // Get turf by ID
    public Optional<Turf> getTurfById(Long id) {
        return turfRepo.findById(id);
    }

    // Add turf
    public Turf addTurf(Turf turf) {
        return turfRepo.save(turf);
    }

    // Delete turf
    public boolean deleteTurf(Long id) {
        if (turfRepo.existsById(id)) {
            turfRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // Update turf
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

    // Upload multiple images for a turf (Cloudinary)
    public List<String> uploadImagesForTurf(Long turfId, List<MultipartFile> images) throws IOException {
        Optional<Turf> optionalTurf = turfRepo.findById(turfId);
        if (!optionalTurf.isPresent()) {
            throw new RuntimeException("Turf not found with ID: " + turfId);
        }

        Turf turf = optionalTurf.get();
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) continue;

            // Upload image bytes to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");
            uploadedUrls.add(imageUrl);
        }

        // Append to existing image URLs or initialize list
        List<String> currentImages = turf.getImageUrls();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }
        currentImages.addAll(uploadedUrls);
        turf.setImageUrls(currentImages);

        turfRepo.save(turf);
        return uploadedUrls;
    }

    // Add image URLs directly to a turf (used by controller)
    public Turf addImagesToTurf(Long turfId, List<String> imageUrls) {
        Optional<Turf> optionalTurf = turfRepo.findById(turfId);
        if (!optionalTurf.isPresent()) {
            throw new RuntimeException("Turf not found with ID: " + turfId);
        }

        Turf turf = optionalTurf.get();
        List<String> currentImages = turf.getImageUrls();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }
        currentImages.addAll(imageUrls);
        turf.setImageUrls(currentImages);

        return turfRepo.save(turf);
    }
}
