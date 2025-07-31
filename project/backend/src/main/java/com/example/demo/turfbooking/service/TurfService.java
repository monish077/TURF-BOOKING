package com.example.demo.turfbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import com.example.demo.turfbooking.repository.TurfRepository;

@Service
public class TurfService {

    private final TurfRepository turfRepo;

    @Autowired
    public TurfService(TurfRepository turfRepo) {
        this.turfRepo = turfRepo;
    }

    // ✅ Get all turfs (for testing or full admin access if needed)
    public List<Turf> getAllTurfs() {
        return turfRepo.findAll();
    }

    // ✅ Get turfs owned by a specific admin
    public List<Turf> getTurfsByAdmin(User admin) {
        return turfRepo.findByAdmin(admin);
    }

    // ✅ Get turf by ID
    public Optional<Turf> getTurfById(Long id) {
        return turfRepo.findById(id);
    }

    // ✅ Add new turf
    public Turf addTurf(Turf turf) {
        return turfRepo.save(turf);
    }

    // ✅ Delete turf by ID
    public boolean deleteTurf(Long id) {
        if (turfRepo.existsById(id)) {
            turfRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ Update existing turf
    public Turf updateTurf(Long id, Turf updatedTurf) {
        return turfRepo.findById(id).map(existingTurf -> {
            existingTurf.setName(updatedTurf.getName());
            existingTurf.setLocation(updatedTurf.getLocation());
            existingTurf.setPricePerHour(updatedTurf.getPricePerHour());
            existingTurf.setImageUrl(updatedTurf.getImageUrl());
            existingTurf.setDescription(updatedTurf.getDescription());
            existingTurf.setFacilities(updatedTurf.getFacilities());
            existingTurf.setAvailableSlots(updatedTurf.getAvailableSlots());
            return turfRepo.save(existingTurf);
        }).orElse(null);
    }
}
