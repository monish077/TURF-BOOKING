package com.example.demo.turfbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "turfs")
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private double pricePerHour;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Constructors

    public Turf() {
        // Default constructor
    }

    public Turf(String name, String location, double pricePerHour, String imageUrl) {
        this.name = name;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // toString override for easy logging

    @Override
    public String toString() {
        return "Turf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", imageUrl='" + (imageUrl != null ? "[image-data]" : "null") + '\'' +
                '}';
    }
}
