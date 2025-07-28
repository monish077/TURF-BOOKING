package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.dto.BookingRequest;
import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.service.BookingService;
import com.example.demo.turfbooking.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000") // or "*" if needed
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;

    // ✅ Create a new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            System.out.println("📩 Incoming booking request: " + request);

            Booking booking = Booking.builder()
                    .userName(request.getUserName())
                    .userEmail(request.getUserEmail())
                    .turfId(request.getTurfId())
                    .turfName(request.getTurfName())
                    .date(request.getDate())
                    .slot(request.getSlot())
                    .price(request.getPrice())
                    .build();

            Booking savedBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(201).body(savedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Booking creation failed: " + e.getMessage());
        }
    }

    // ✅ Get all bookings
    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch all bookings");
        }
    }

    // ✅ Get bookings by user email
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getBookingsByUserEmail(@PathVariable String email) {
        try {
            List<Booking> bookings = bookingService.getBookingsByUserEmail(email);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for email: " + email);
        }
    }

    // ✅ Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            Optional<Booking> optionalBooking = bookingService.getBookingById(id);
            if (optionalBooking.isPresent()) {
                return ResponseEntity.ok(optionalBooking.get());
            } else {
                return ResponseEntity.status(404).body("❌ Booking not found for ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch booking by ID");
        }
    }

    // ✅ Get bookings by turf ID
    @GetMapping("/turf/{turfId}")
    public ResponseEntity<?> getBookingsByTurfId(@PathVariable Long turfId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByTurfId(turfId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for turf ID: " + turfId);
        }
    }

    // ✅ Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Booking deletion failed for ID: " + id);
        }
    }

    // ✅ Send confirmation email after successful payment
    @GetMapping("/send-confirmation/{bookingId}")
    public ResponseEntity<?> sendConfirmationEmail(@PathVariable Long bookingId) {
        try {
            Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
            if (optionalBooking.isPresent()) {
                Booking booking = optionalBooking.get();
                emailService.sendBookingConfirmationEmail(
                        booking.getUserEmail(),
                        booking.getUserName(),
                        booking.getTurfName(),
                        booking.getDate().toString(),
                        booking.getSlot(),
                        String.valueOf(booking.getPrice())
                );
                return ResponseEntity.ok("✅ Booking confirmation email sent.");
            } else {
                return ResponseEntity.status(404).body("❌ Booking not found for ID: " + bookingId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to send booking confirmation email.");
        }
    }
}
