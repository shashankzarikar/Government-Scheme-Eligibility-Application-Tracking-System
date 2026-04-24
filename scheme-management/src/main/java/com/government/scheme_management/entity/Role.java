// This declares which package this file belongs to
// Just like an address for this Java file
package com.government.scheme_management.entity;

// Importing JPA annotations - these come from Jakarta library
// @Entity, @Table, @Id, @Column etc. are all from here
import jakarta.persistence.*;

// Importing Lombok annotations
// These auto generate getters, setters, constructors for us
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data - Automatically generates:
//    - getters (getRoleId(), getRoleName())
//    - setters (setRoleId(), setRoleName())
//    - toString() method
//    - equals() and hashCode() methods
// Without this we would have to write all of them manually
@Data

// @NoArgsConstructor - Generates an empty constructor
// Role role = new Role();  ← this becomes possible
@NoArgsConstructor

// @AllArgsConstructor - Generates a constructor with all fields
// Role role = new Role(1, "ADMIN");  ← this becomes possible
@AllArgsConstructor

// @Entity - Tells Spring Boot that this class is a DATABASE TABLE
// Without this JPA won't recognize this class as a table
@Entity

// @Table(name = "roles") - Maps this class to "roles" table in MySQL
// If your class name and table name are same you can skip this
// But since Java uses PascalCase (Role) and SQL uses lowercase (roles)
// we explicitly mention the table name
@Table(name = "roles")

public class Role {

    // @Id - Marks this field as the PRIMARY KEY of the table
    @Id

    // @GeneratedValue - Tells JPA how to generate the ID value
    // GenerationType.IDENTITY means AUTO_INCREMENT in MySQL
    // MySQL will automatically assign 1, 2, 3... to each row
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // @Column - Maps this Java field to a specific column in the table
    // name = "role_id" means this field maps to "role_id" column in MySQL
    @Column(name = "role_id")

    // This is the Java field - maps to role_id column in roles table
    private Integer roleId;

    // @Column with extra constraints:
    // name = "role_name"   → maps to role_name column in MySQL
    // nullable = false     → same as NOT NULL in MySQL
    // unique = true        → same as UNIQUE in MySQL
    // length = 20          → same as VARCHAR(20) in MySQL
    @Column(name = "role_name", nullable = false, unique = true, length = 20)

    // This is the Java field - maps to role_name column in roles table
    private String roleName;

}