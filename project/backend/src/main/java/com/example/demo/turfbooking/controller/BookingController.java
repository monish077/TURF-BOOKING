package com.example.demo.turfbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
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