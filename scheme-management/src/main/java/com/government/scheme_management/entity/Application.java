// Package declaration
package com.government.scheme_management.entity;

// JPA imports for database mapping
import jakarta.persistence.*;

// Lombok imports to reduce boilerplate
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Date and time imports
import java.time.LocalDateTime;

// Auto generates getters, setters, toString, equals, hashCode
@Data

// Generates empty constructor
@NoArgsConstructor

// Generates constructor with all fields
@AllArgsConstructor

// Marks this class as a database table entity
@Entity

// Maps this class to "applications" table in MySQL
@Table(name = "applications")
public class Application {

    // PRIMARY KEY - auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    // MANY TO ONE - Many applications can belong to one applicant
    // applicant_id is foreign key pointing to users table
    // nullable = false because every application must have an applicant
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // MANY TO ONE - Many applications can be for one scheme
    // scheme_id is foreign key pointing to schemes table
    // nullable = false because every application must be for a scheme
    @ManyToOne
    @JoinColumn(name = "scheme_id", nullable = false)
    private Scheme scheme;

    // MANY TO ONE - Many applications can be assigned to one officer
    // officer_id is NULLABLE because officer is assigned later by admin
    // When applicant first submits - officer is NULL
    // Admin assigns officer later
    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = true)
    private User officer;

    // ENUM type - only 3 possible values
    // Default is PENDING when application is first submitted
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    // When application was submitted
    // updatable = false - set once and never changed
    @Column(name = "applied_at", updatable = false)
    private LocalDateTime appliedAt;

    // When application was last updated
    // This updates every time status changes
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Runs automatically BEFORE saving new application to database
    // Sets both appliedAt and updatedAt to current time
    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Runs automatically BEFORE updating existing application in database
    // Updates the updatedAt timestamp every time application is modified
    // For example when officer approves or rejects
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enum for application status
    // Only these 3 values are allowed
    public enum Status {
        PENDING,    // Just submitted, waiting for officer review
        APPROVED,   // Officer approved the application
        REJECTED    // Officer rejected the application
    }

}