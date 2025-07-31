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
@CrossOrigin(origins = "http://localhost:3000")
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

    // ✅ 🔐 Secure: Get bookings for the admin’s turfs using JWT
    @GetMapping("/admin")
    public ResponseEntity<?> getBookingsForAdminTurfs(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.replace("Bearer ", "");
            String adminEmail = jwtUtil.extractUsername(token); // Extract from JWT

            System.out.println("📩 Fetching bookings for Admin: " + adminEmail); // Log email

            List<Booking> bookings = bookingService.getBookingsByAdminEmail(adminEmail);
            return ResponseEntity.ok(bookings);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 Print actual error
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for admin");
        }
    }

    // ✅ Get all bookings (for debugging/admin super-view)
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
            return ResponseEntity.status(500).body("❌ Failed to fetch bookings for user: " + email);
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

    // ✅ Send confirmation email after successful booking
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

    // ✅ Get a specific booking by ID (💥 Fix applied here!)
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            Optional<Booking> optionalBooking = bookingService.getBookingById(id);
            return optionalBooking.<ResponseEntity<?>>map(b -> ResponseEntity.ok(b))
                    .orElseGet(() -> ResponseEntity.status(404).body("❌ Booking not found for ID: " + id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Failed to fetch booking by ID");
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
}
