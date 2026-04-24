// Package declaration
package com.government.scheme_management.entity;

// JPA imports for database mapping
import jakarta.persistence.*;

// Lombok imports to reduce boilerplate
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Date and time import
import java.time.LocalDateTime;

// Auto generates getters, setters, toString, equals, hashCode
@Data

// Generates empty constructor
@NoArgsConstructor

// Generates constructor with all fields
@AllArgsConstructor

// Marks this class as a database table entity
@Entity

// Maps this class to "application_remarks" table in MySQL
@Table(name = "application_remarks")
public class ApplicationRemark {

    // PRIMARY KEY - auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remark_id")
    private Integer remarkId;

    // MANY TO ONE - Many remarks can belong to one application
    // application_id is foreign key pointing to applications table
    // nullable = false because every remark must belong to an application
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // MANY TO ONE - Many remarks can be added by one officer
    // officer_id is foreign key pointing to users table
    // nullable = false because every remark must have an officer
    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    // The actual comment or reason written by officer
    // e.g. "Aadhaar card is blurry, please reupload"
    // TEXT type because remarks can be long
    @Column(name = "remark", nullable = false, columnDefinition = "TEXT")
    private String remark;

    // When the remark was added
    // updatable = false - set once and never changed
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    // Runs automatically before saving to database
    // Sets addedAt to current time when remark is first created
    @PrePersist
    public void prePersist() {
        this.addedAt = LocalDateTime.now();
    }

}