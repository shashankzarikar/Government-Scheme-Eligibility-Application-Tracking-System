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

// Maps this class to "documents" table in MySQL
@Table(name = "documents")
public class Document {

    // PRIMARY KEY - auto increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    // MANY TO ONE - Many documents can belong to one application
    // application_id is foreign key pointing to applications table
    // nullable = false because every document must belong to an application
    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // MANY TO ONE - Direct link to applicant for easy querying
    // Remember we decided to add applicant_id directly in documents table
    // So we don't need JOIN to find whose document it is
    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // Name of the document
    // e.g. "Aadhaar Card", "Income Certificate", "Bank Passbook"
    @Column(name = "document_name", nullable = false, length = 100)
    private String documentName;

    // Path where file is stored on server
    // e.g. "uploads/app1/priya_aadhaar.pdf"
    // We never store actual file in database - only the path
    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    // When document was uploaded
    // updatable = false - set once and never changed
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    // Runs automatically before saving to database
    // Sets uploadedAt to current time when document is first uploaded
    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }

}