package com.example.demo.turfbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "turfs")
public class Turf {

    @Id
    private String id;

    private String name;
    private String location;

    private double pricePerHour;

    // Store multiple image URLs
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    private String description;

    private String facilities;

    private String availableSlots;

    // Link to admin (User)
    @DBRef(lazy = true)
    @JsonIgnore
    private User admin;

    @Override
    public String toString() {
        return "Turf{" +
                "id='" + id + '\'' +
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
