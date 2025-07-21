package com.example.demo.turfbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.turfbooking.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTurfId(Long turfId);
    List<Booking> findByUserEmail(String userEmail);

}