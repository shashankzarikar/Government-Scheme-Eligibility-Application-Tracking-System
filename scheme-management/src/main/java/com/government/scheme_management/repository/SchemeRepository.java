package com.government.scheme_management.repository;

import com.government.scheme_management.entity.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Integer> {

    // Find all active schemes - for applicant browse page
    List<Scheme> findByIsActive(Boolean isActive);
}
