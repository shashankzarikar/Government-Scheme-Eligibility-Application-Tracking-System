package com.government.scheme_management.repository;

import com.government.scheme_management.entity.ApplicationRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRemarkRepository extends JpaRepository<ApplicationRemark, Integer> {

    // All remarks for a specific application - shown to applicant in tracking
    List<ApplicationRemark> findByApplication_ApplicationIdOrderByAddedAtAsc(Integer applicationId);

    // All remarks added by a specific officer - for officer remarks history page
    List<ApplicationRemark> findByOfficer_UserIdOrderByAddedAtDesc(Integer officerId);
}
