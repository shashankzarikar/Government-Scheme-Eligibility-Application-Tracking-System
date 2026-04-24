package com.government.scheme_management.repository;

import com.government.scheme_management.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    // All applications submitted by one applicant
    List<Application> findByApplicant_UserId(Integer applicantId);

    // All applications assigned to one officer
    List<Application> findByOfficer_UserId(Integer officerId);

    // All applications with no officer assigned yet - for admin assign page
    List<Application> findByOfficerIsNull();

    // Applications by status - e.g. all PENDING
    List<Application> findByStatus(Application.Status status);

    // Applications assigned to an officer with a specific status
    List<Application> findByOfficer_UserIdAndStatus(Integer officerId, Application.Status status);

    // Count by status - for dashboard stats
    long countByStatus(Application.Status status);

    // Count applications assigned to officer
    long countByOfficer_UserId(Integer officerId);

    // Count applications by officer and status
    long countByOfficer_UserIdAndStatus(Integer officerId, Application.Status status);

    // Check if applicant already applied for a scheme
    boolean existsByApplicant_UserIdAndScheme_SchemeId(Integer applicantId, Integer schemeId);
}
