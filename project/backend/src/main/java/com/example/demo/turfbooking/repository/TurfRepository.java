package com.example.demo.turfbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.turfbooking.entity.Turf;

@Repository
public interface TurfRepository extends JpaRepository<Turf, Long> {
}
