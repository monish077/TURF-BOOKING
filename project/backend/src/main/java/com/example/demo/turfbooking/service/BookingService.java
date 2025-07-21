package com.example.demo.turfbooking.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.turfbooking.entity.Booking;

public interface BookingService {
    Booking createBooking(Booking booking);
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(Long id);
    List<Booking> getBookingsByTurfId(Long turfId);
    List<Booking> getBookingsByUserEmail(String email);
    void deleteBooking(Long id);
}
