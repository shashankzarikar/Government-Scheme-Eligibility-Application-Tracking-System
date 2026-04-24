// ─────────────────────────────────────────
// RoleRepository.java
// ─────────────────────────────────────────
package com.government.scheme_management.repository;

import com.government.scheme_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Find role by name e.g. findByRoleName("APPLICANT")
    Optional<Role> findByRoleName(String roleName);
}
