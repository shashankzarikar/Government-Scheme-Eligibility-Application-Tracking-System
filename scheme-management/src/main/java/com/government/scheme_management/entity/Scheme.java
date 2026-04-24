// Package declaration
package com.government.scheme_management.entity;

// JPA imports for database mapping
import jakarta.persistence.*;

// Lombok imports to reduce boilerplate
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Date and time imports
import java.time.LocalDate;
import java.time.LocalDateTime;

// Auto generates getters, setters, toString, equals, hashCode
@Data

// Generates empty constructor
@NoArgsConstructor

// Generates constructor with all fields
@AllArgsConstructor

// Marks this class as a database table entity
@Entity

// Maps this class to "schemes" table in MySQL
@Table(name = "schemes")
public class Scheme {

    // PRIMARY KEY - auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id")
    private Integer schemeId;

    // Name of the scheme - required field
    // length = 150 means VARCHAR(150) in MySQL
    @Column(name = "scheme_name", nullable = false, length = 150)
    private String schemeName;

    // Detailed description of the scheme
    // TEXT type because description can be very long
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Who can apply for this scheme
    // TEXT type because criteria can be long
    @Column(name = "eligibility_criteria", columnDefinition = "TEXT")
    private String eligibilityCriteria;

    // What documents applicant must upload
    // TEXT type because document list can be long
    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    // When scheme opens for applications
    // LocalDate maps to DATE type in MySQL
    @Column(name = "start_date")
    private LocalDate startDate;

    // Last date to apply for this scheme
    @Column(name = "end_date")
    private LocalDate endDate;

    // Whether scheme is currently active or not
    // Admin can deactivate without deleting
    @Column(name = "is_active")
    private Boolean isActive = true;

    // MANY TO ONE - Many schemes can be created by one admin
    // created_by is a foreign key pointing to users table
    // This gives us the full User object of the admin who created it
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Timestamp when scheme was created
    // updatable = false - set once and never changed
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Runs automatically before saving to database
    // Sets createdAt to current time when scheme is first created
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}