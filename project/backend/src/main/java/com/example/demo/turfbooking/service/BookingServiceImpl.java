package com.example.demo.turfbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.turfbooking.entity.Booking;
import com.example.demo.turfbooking.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

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


    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
