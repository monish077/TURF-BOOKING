package com.example.demo.turfbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.service.BookingService;
import com.example.demo.turfbooking.service.WhatsAppService;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private WhatsAppService whatsAppService;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        Booking savedBooking = bookingService.createBooking(booking);

        // ✅ Send WhatsApp confirmation if number is available
        if (booking.getWhatsappNumber() != null && !booking.getWhatsappNumber().isEmpty()) {
            String message = String.format(
                "✅ Hello %s!\n\nYour turf booking for *%s* on *%s* at *%s* is confirmed!\n\nAmount: ₹%.2f\nVenue: Mars Arena 🌱⚽",
                booking.getUserName(),
                booking.getTurfName(),
                booking.getDate(),
                booking.getSlot(),
                booking.getPrice()
            );

            whatsAppService.sendBookingConfirmation(booking.getWhatsappNumber(), message);
        }

        return savedBooking;
    }

    @GetMapping("/all")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{email}")
    public List<Booking> getBookingsByUserEmail(@PathVariable String email) {
        return bookingService.getBookingsByUserEmail(email);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @GetMapping("/turf/{turfId}")
    public List<Booking> getBookingsByTurfId(@PathVariable Long turfId) {
        return bookingService.getBookingsByTurfId(turfId);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
