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
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getBookingsByTurfId(Long turfId) {
        return bookingRepository.findByTurfId(turfId);
    }

    @Override
    public List<Booking> getBookingsByUserEmail(String email) {
        return bookingRepository.findByUserEmail(email);
    }

    // ✅ NEW: Get bookings for all turfs owned by a specific admin
    @Override
    public List<Booking> getBookingsByAdminEmail(String adminEmail) {
        List<Turf> adminTurfs = turfRepository.findByAdmin_Email(adminEmail);
        List<Long> turfIds = adminTurfs.stream()
                .map(Turf::getId)
                .collect(Collectors.toList());

        return bookingRepository.findAll().stream()
                .filter(booking -> turfIds.contains(booking.getTurfId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
