package com.example.demo.turfbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    private String userEmail;

    @Column
    private Long turfId;

    @Column
    private String turfName;

    @Column
    private String date;

    @Column
    private String slot;

    @Column
    private double price;
}
