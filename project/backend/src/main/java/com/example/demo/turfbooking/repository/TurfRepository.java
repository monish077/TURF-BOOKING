package com.example.demo.turfbooking.repository;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurfRepository extends JpaRepository<Turf, Long> {

    // ✅ This is the method you're missing
    List<Turf> findByAdmin(User admin);

    // ✅ You can also keep this if needed
    List<Turf> findByAdmin_Email(String email);
}
