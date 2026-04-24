package com.government.scheme_management.repository;

import com.government.scheme_management.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    // All documents belonging to a specific application
    List<Document> findByApplication_ApplicationId(Integer applicationId);

    // All documents uploaded by a specific applicant
    List<Document> findByApplicant_UserId(Integer applicantId);
}
