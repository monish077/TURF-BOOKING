package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(Booking booking);
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long id);
    List<Booking> getBookingsByTurfId(Long turfId);
    List<Booking> getBookingsByUserEmail(String email);
    List<Booking> getBookingsByAdminEmail(String adminEmail); 
    void deleteBooking(Long id);
}
