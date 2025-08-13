package com.example.demo.turfbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turfs")
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @Column(name = "price_per_hour")
    private double pricePerHour;

    // ✅ Store multiple image URLs in a separate table (turf_images)
    @ElementCollection(fetch = FetchType.EAGER) // <-- EAGER to avoid LazyInitializationException
    @CollectionTable(name = "turf_images", joinColumns = @JoinColumn(name = "turf_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String facilities;

    @Column(name = "available_slots", columnDefinition = "TEXT")
    private String availableSlots;

    // ✅ Link to admin (user who created this turf)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    @JsonIgnore // <-- Prevents JSON serialization from triggering lazy loading
    private User admin;

    // === Constructors ===
    public Turf() {}

    public Turf(String name, String location, double pricePerHour, List<String> imageUrls,
                String description, String facilities, String availableSlots) {
        this.name = name;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.imageUrls = imageUrls;
        this.description = description;
        this.facilities = facilities;
        this.availableSlots = availableSlots;
    }

    // === Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(String availableSlots) { this.availableSlots = availableSlots; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    @Override
    public String toString() {
        return "Turf{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", imageUrls=" + imageUrls +
                ", description='" + description + '\'' +
                ", facilities='" + facilities + '\'' +
                ", availableSlots='" + availableSlots + '\'' +
                ", admin=" + (admin != null ? admin.getEmail() : "null") +
                '}';
    }
}
