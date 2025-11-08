package com.example.demo.turfbooking.controller;

import com.example.demo.turfbooking.dto.BookingRequest;
import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.service.BookingService;
import com.example.demo.turfbooking.service.EmailService;
import com.example.demo.turfbooking.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
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

    @GetMapping("/admin")
    public ResponseEntity<?> getBookingsForAdminTurfs(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Unauthorized: Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String adminEmail = jwtUtil.extractUsername(token);

            List<Booking> bookings = bookingService.getBookingsByAdminEmail(adminEmail);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for admin");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            return ResponseEntity.ok(bookingService.getAllBookings());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch all bookings");
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getBookingsByUserEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByUserEmail(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for user: " + email);
        }
    }

    @GetMapping("/turf/{turfId}")
    public ResponseEntity<?> getBookingsByTurfId(@PathVariable String turfId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByTurfId(turfId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for turf ID: " + turfId);
        }
    }

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
                        booking.getDate(),
                        booking.getSlot(),
                        String.valueOf(booking.getPrice()) // ✅ Fix Long → String
                );
                return ResponseEntity.ok("✅ Booking confirmation email sent for booking ID: " + bookingId);
            } else {
                return ResponseEntity.status(404).body("❌ Booking not found for ID: " + bookingId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to send booking confirmation email.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            Optional<Booking> optionalBooking = bookingService.getBookingById(id);
            return optionalBooking.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body("❌ Booking not found for ID: " + id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch booking by ID");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok("✅ Booking deleted successfully for ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Booking deletion failed for ID: " + id);
        }
    }
}
