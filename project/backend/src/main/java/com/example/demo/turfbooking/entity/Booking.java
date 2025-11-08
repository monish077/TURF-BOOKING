package com.example.demo.turfbooking.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String userName;

    private String userEmail;

    private String turfId;

    private String turfName;

    private String date;

    private String slot;

    private double price;
}
