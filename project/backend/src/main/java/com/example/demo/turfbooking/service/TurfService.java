package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class TurfService {

    private final TurfRepository turfRepo;
    private final String uploadDir = "uploads/";

    @Autowired
    public TurfService(TurfRepository turfRepo) {
        this.turfRepo = turfRepo;

        // ✅ Create uploads folder if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ✅ Get all turfs
    public List<Turf> getAllTurfs() {
        return turfRepo.findAll();
    }

    // ✅ Get turfs by admin
    public List<Turf> getTurfsByAdmin(User admin) {
        return turfRepo.findByAdmin(admin);
    }

    // ✅ Get turf by ID
    public Optional<Turf> getTurfById(Long id) {
        return turfRepo.findById(id);
    }

    // ✅ Add turf
    public Turf addTurf(Turf turf) {
        return turfRepo.save(turf);
    }

    // ✅ Delete turf
    public boolean deleteTurf(Long id) {
        if (turfRepo.existsById(id)) {
            turfRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ Update turf
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

    // ✅ Upload multiple images for a turf
    public List<String> uploadImagesForTurf(Long turfId, List<MultipartFile> images) throws IOException {
        Optional<Turf> optionalTurf = turfRepo.findById(turfId);
        if (!optionalTurf.isPresent()) {
            throw new RuntimeException("Turf not found with ID: " + turfId);
        }

        Turf turf = optionalTurf.get();
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : images) {
            if (file == null || file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                continue; // Skip invalid file
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + fileExtension;

            Path destinationPath = Paths.get(uploadDir).resolve(uniqueFileName);
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/uploads/" + uniqueFileName;
            uploadedUrls.add(fileUrl);
        }

        // ✅ Append to existing image URLs or initialize
        List<String> currentImages = turf.getImageUrls();
        if (currentImages == null) {
            currentImages = new ArrayList<>();
        }
        currentImages.addAll(uploadedUrls);
        turf.setImageUrls(currentImages);

        turfRepo.save(turf);
        return uploadedUrls;
    }
}
