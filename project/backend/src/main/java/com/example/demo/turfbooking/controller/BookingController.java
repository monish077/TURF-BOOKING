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

    // ✅ Create a new booking
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

            // ✅ Send confirmation email after booking creation
            emailService.sendBookingConfirmationEmail(
                    savedBooking.getUserEmail(),
                    savedBooking.getUserName(),
                    savedBooking.getTurfName(),
                    savedBooking.getDate(),
                    savedBooking.getSlot(),
                    String.valueOf(savedBooking.getPrice())
            );

            return ResponseEntity.status(201).body(savedBooking);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Booking creation failed: " + e.getMessage());
        }
    }

    // ✅ Get bookings for admin's turfs using JWT
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

    // ✅ Get all bookings (for admin panel)
    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            return ResponseEntity.ok(bookingService.getAllBookings());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch all bookings");
        }
    }

    // ✅ Get bookings by user email
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getBookingsByUserEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByUserEmail(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for user: " + email);
        }
    }

    // ✅ Get bookings by turf ID
    @GetMapping("/turf/{turfId}")
    public ResponseEntity<?> getBookingsByTurfId(@PathVariable String turfId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByTurfId(turfId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for turf ID: " + turfId);
        }
    }

    // ✅ Send booking confirmation email manually
    @GetMapping("/send-confirmation/{bookingId}")
    public ResponseEntity<?> sendConfirmationEmail(@PathVariable Long bookingId) {
        try {
            Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);

            if (optionalBooking.isPresent()) {
                Booking booking = optionalBooking.get();

                // ✅ Convert Long -> String properly
                emailService.sendBookingConfirmationEmail(
                        booking.getUserEmail(),
                        booking.getUserName(),
                        booking.getTurfName(),
                        booking.getDate(),
                        booking.getSlot(),
                        String.valueOf(booking.getPrice())
                );

                // ✅ Example of using bookingId safely as String
                String bookingIdStr = String.valueOf(bookingId);
                System.out.println("Email sent for booking ID: " + bookingIdStr);

                return ResponseEntity.ok("✅ Booking confirmation email sent for ID: " + bookingIdStr);
            } else {
                return ResponseEntity.status(404).body("❌ Booking not found for ID: " + bookingId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to send booking confirmation email.");
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

    // ✅ Delete booking by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            String idStr = String.valueOf(id); // ✅ Long -> String conversion
            return ResponseEntity.ok("✅ Booking deleted successfully with ID: " + idStr);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Booking deletion failed for ID: " + id);
        }
    }
}
