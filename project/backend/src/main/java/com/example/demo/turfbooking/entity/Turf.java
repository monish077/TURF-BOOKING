package com.example.demo.turfbooking.entity;

import jakarta.persistence.*;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String facilities;

    @Column(name = "available_slots", columnDefinition = "TEXT")
    private String availableSlots;

    // ✅ NEW: Link Turf to the Admin who created it
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    public Turf() {}

    public Turf(String name, String location, double pricePerHour, String imageUrl,
                String description, String facilities, String availableSlots) {
        this.name = name;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.imageUrl = imageUrl;
        this.description = description;
        this.facilities = facilities;
        this.availableSlots = availableSlots;
    }

    // ===== Getters and Setters =====

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(String availableSlots) {
        this.availableSlots = availableSlots;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Turf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", facilities='" + facilities + '\'' +
                ", availableSlots='" + availableSlots + '\'' +
                ", admin=" + (admin != null ? admin.getEmail() : "null") +
                '}';
    }
}
