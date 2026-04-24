package com.government.scheme_management.repository;

import com.government.scheme_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find user by email - used for login
    Optional<User> findByEmail(String email);

    // Check if email already exists - used during registration
    boolean existsByEmail(String email);

    // Find all users by role name - used by admin to list officers
    List<User> findByRole_RoleName(String roleName);

    // Find all active users
    List<User> findByIsActive(Boolean isActive);
}
