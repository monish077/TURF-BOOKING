package com.example.demo.turfbooking.repository;

import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurfRepository extends MongoRepository<Turf, String> {

    // Find turfs by admin user reference
    List<Turf> findByAdmin(User admin);

    // Find turfs by admin email
    List<Turf> findByAdmin_Email(String email);
}
