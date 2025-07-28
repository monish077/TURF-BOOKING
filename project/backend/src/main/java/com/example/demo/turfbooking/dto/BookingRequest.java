package com.example.demo.turfbooking.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private String userName;
    private String userEmail;
    private Long turfId;
    private String turfName;
    private String date;
    private String slot;
    private double price;
}
