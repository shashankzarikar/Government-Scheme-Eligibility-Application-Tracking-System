// Package declaration - this file belongs to entity package
package com.government.scheme_management.entity;

// Importing JPA annotations for database mapping
import jakarta.persistence.*;

// Importing Lombok annotations to reduce boilerplate code
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Importing Java date and time classes
import java.time.LocalDate;
import java.time.LocalDateTime;

// Auto generates getters, setters, toString, equals, hashCode
@Data

// Generates empty constructor → new User()
@NoArgsConstructor

// Generates constructor with all fields → new User(id, name, ...)
@AllArgsConstructor

// Marks this class as a database table entity
@Entity

// Maps this class to "users" table in MySQL
@Table(name = "users")
public class User {

    // PRIMARY KEY - maps to user_id column
    // AUTO_INCREMENT - MySQL generates 1, 2, 3 automatically
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    // Maps to full_name column
    // nullable = false means NOT NULL in MySQL
    // length = 100 means VARCHAR(100) in MySQL
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // Maps to email column
    // unique = true means no two users can have same email
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // Maps to password column
    // length = 255 because BCrypt encrypted passwords are long
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // Maps to phone column - nullable (optional field)
    @Column(name = "phone", length = 15)
    private String phone;

    // Maps to address column
    // columnDefinition = "TEXT" means TEXT type in MySQL (longer than VARCHAR)
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    // Maps to date_of_birth column
    // LocalDate maps to DATE type in MySQL
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // ENUM type in MySQL
    // EnumType.STRING means store "MALE", "FEMALE", "OTHER" as text in DB
    // EnumType.ORDINAL would store 0, 1, 2 which is not readable - avoid it
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // MANY TO ONE relationship
    // Many users can have one role
    // @JoinColumn - foreign key column in users table that points to roles table
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Maps to is_active column
    // Boolean maps to TINYINT(1) in MySQL
    // Default value is true - new users are active by default
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Maps to created_at column
    // LocalDateTime maps to TIMESTAMP in MySQL
    // updatable = false means this value is set once and never updated
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // @PrePersist - this method runs AUTOMATICALLY before saving to database
    // It sets the createdAt timestamp when a new user is created
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Enum class defined inside User class
    // These are the only allowed values for gender field
    public enum Gender {
        MALE, FEMALE, OTHER
    }

}