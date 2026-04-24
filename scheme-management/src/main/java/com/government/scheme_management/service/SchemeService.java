package com.government.scheme_management.service;

import com.government.scheme_management.dto.SchemeRequest;
import com.government.scheme_management.dto.SchemeResponse;
import com.government.scheme_management.entity.Scheme;
import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.SchemeRepository;
import com.government.scheme_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemeService {

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all active schemes - for applicant browse page
    public List<SchemeResponse> getAllActiveSchemes() {
        return schemeRepository.findByIsActive(true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get all schemes (active + inactive) - for admin manage schemes page
    public List<SchemeResponse> getAllSchemes() {
        return schemeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get single scheme by ID
    public SchemeResponse getSchemeById(Integer schemeId) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found with ID: " + schemeId));
        return toResponse(scheme);
    }

    // Create new scheme - admin only
    public SchemeResponse createScheme(SchemeRequest request, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        Scheme scheme = new Scheme();
        scheme.setSchemeName(request.getSchemeName());
        scheme.setDescription(request.getDescription());
        scheme.setEligibilityCriteria(request.getEligibilityCriteria());
        scheme.setRequiredDocuments(request.getRequiredDocuments());
        scheme.setStartDate(request.getStartDate());
        scheme.setEndDate(request.getEndDate());
        scheme.setIsActive(request.getIsActive());
        scheme.setCreatedBy(admin);

        return toResponse(schemeRepository.save(scheme));
    }

    // Update existing scheme - admin only
    public SchemeResponse updateScheme(Integer schemeId, SchemeRequest request) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found with ID: " + schemeId));

        scheme.setSchemeName(request.getSchemeName());
        scheme.setDescription(request.getDescription());
        scheme.setEligibilityCriteria(request.getEligibilityCriteria());
        scheme.setRequiredDocuments(request.getRequiredDocuments());
        scheme.setStartDate(request.getStartDate());
        scheme.setEndDate(request.getEndDate());
        scheme.setIsActive(request.getIsActive());

        return toResponse(schemeRepository.save(scheme));
    }

    // Deactivate scheme - admin only (soft delete)
    public String deactivateScheme(Integer schemeId) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new RuntimeException("Scheme not found with ID: " + schemeId));
        scheme.setIsActive(false);
        schemeRepository.save(scheme);
        return "Scheme deactivated successfully";
    }

    // Delete scheme permanently - admin only
    public String deleteScheme(Integer schemeId) {
        if (!schemeRepository.existsById(schemeId)) {
            throw new RuntimeException("Scheme not found with ID: " + schemeId);
        }
        schemeRepository.deleteById(schemeId);
        return "Scheme deleted successfully";
    }

    // Helper: convert Scheme entity → SchemeResponse DTO
    private SchemeResponse toResponse(Scheme scheme) {
        SchemeResponse res = new SchemeResponse();
        res.setSchemeId(scheme.getSchemeId());
        res.setSchemeName(scheme.getSchemeName());
        res.setDescription(scheme.getDescription());
        res.setEligibilityCriteria(scheme.getEligibilityCriteria());
        res.setRequiredDocuments(scheme.getRequiredDocuments());
        res.setStartDate(scheme.getStartDate());
        res.setEndDate(scheme.getEndDate());
        res.setIsActive(scheme.getIsActive());
        res.setCreatedByName(scheme.getCreatedBy().getFullName());
        res.setCreatedAt(scheme.getCreatedAt());
        return res;
    }
}
