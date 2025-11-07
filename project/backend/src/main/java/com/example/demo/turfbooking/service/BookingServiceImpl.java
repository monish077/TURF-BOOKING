package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.entity.Turf;
import com.example.demo.turfbooking.repository.BookingRepository;
import com.example.demo.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TurfRepository turfRepository;

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(String id) {  // Changed to String
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByTurfId(String turfId) {
        return bookingRepository.findByTurfId(turfId);
    }

    @Override
    public List<Booking> getBookingsByUserEmail(String email) {
        return bookingRepository.findByUserEmail(email);
    }

    @Override
    public List<Booking> getBookingsByAdminEmail(String adminEmail) {
        // Get all turfs administered by this admin
        List<Turf> adminTurfs = turfRepository.findByAdmin_Email(adminEmail);

        // Convert turf IDs to String to match 'turfId' in Booking
        List<String> turfIds = adminTurfs.stream()
                .map(turf -> turf.getId().toString()) // Convert Long to String
                .collect(Collectors.toList());

        // Filter bookings whose turfId is among the admin's turfs
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getTurfId() != null && turfIds.contains(booking.getTurfId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(String id) {  // Changed to String
        bookingRepository.deleteById(id);
    }
}