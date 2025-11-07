package com.example.demo.turfbooking.repository;

import com.example.demo.turfbooking.entity.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByTurfId(String turfId);
    List<Booking> findByUserEmail(String email);
}