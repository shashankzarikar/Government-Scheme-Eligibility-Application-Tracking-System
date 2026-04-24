package com.government.scheme_management.service;

import com.government.scheme_management.dto.*;
import com.government.scheme_management.entity.*;
import com.government.scheme_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationRemarkRepository remarkRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    // ── APPLICANT: Submit a new application ─────────────────────────────────
    public ApplicationResponse submitApplication(ApplicationRequest request, String applicantEmail) {

        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        Scheme scheme = schemeRepository.findById(request.getSchemeId())
                .orElseThrow(() -> new RuntimeException("Scheme not found with ID: " + request.getSchemeId()));

        // Check if scheme is active
        if (!scheme.getIsActive()) {
            throw new RuntimeException("This scheme is no longer accepting applications");
        }

        // Check if applicant already applied for this scheme
        if (applicationRepository.existsByApplicant_UserIdAndScheme_SchemeId(
                applicant.getUserId(), scheme.getSchemeId())) {
            throw new RuntimeException("You have already applied for this scheme");
        }

        Application application = new Application();
        application.setApplicant(applicant);
        application.setScheme(scheme);
        application.setStatus(Application.Status.PENDING);
        // officer is NULL at this stage - admin assigns later

        return toResponse(applicationRepository.save(application));
    }

    // ── APPLICANT: Get all my applications ──────────────────────────────────
    public List<ApplicationResponse> getMyApplications(String applicantEmail) {
        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        return applicationRepository.findByApplicant_UserId(applicant.getUserId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── APPLICANT: Get single application with full tracking detail ──────────
    public ApplicationResponse getApplicationById(Integer applicationId, String userEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        // Ensure applicant can only see their own application
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userRole = user.getRole().getRoleName();
        if (userRole.equals("APPLICANT") &&
                !application.getApplicant().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: This is not your application");
        }

        return toResponseWithDetails(application);
    }

    // ── ADMIN: Get all applications ──────────────────────────────────────────
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN: Get applications with no officer assigned ─────────────────────
    public List<ApplicationResponse> getUnassignedApplications() {
        return applicationRepository.findByOfficerIsNull()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── ADMIN: Assign officer to application ─────────────────────────────────
    public ApplicationResponse assignOfficer(Integer applicationId, AssignOfficerRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + request.getOfficerId()));

        // Verify the user being assigned is actually an officer
        if (!officer.getRole().getRoleName().equals("OFFICER")) {
            throw new RuntimeException("User is not an officer");
        }

        application.setOfficer(officer);
        return toResponse(applicationRepository.save(application));
    }

    // ── OFFICER: Get all applications assigned to me ─────────────────────────
    public List<ApplicationResponse> getAssignedApplications(String officerEmail) {
        User officer = userRepository.findByEmail(officerEmail)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        return applicationRepository.findByOfficer_UserId(officer.getUserId())
                .stream()
                .map(this::toResponseWithDetails)
                .collect(Collectors.toList());
    }

    // ── OFFICER: Make a decision (approve or reject) with remark ─────────────
    public ApplicationResponse makeDecision(Integer applicationId,
                                            DecisionRequest request,
                                            String officerEmail) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User officer = userRepository.findByEmail(officerEmail)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        // Verify this application is assigned to this officer
        if (application.getOfficer() == null ||
                !application.getOfficer().getUserId().equals(officer.getUserId())) {
            throw new RuntimeException("This application is not assigned to you");
        }

        // Set status based on decision
        try {
            Application.Status newStatus = Application.Status.valueOf(request.getDecision().toUpperCase());
            application.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid decision. Use APPROVED or REJECTED");
        }

        applicationRepository.save(application);

        // Save the remark
        ApplicationRemark remark = new ApplicationRemark();
        remark.setApplication(application);
        remark.setOfficer(officer);
        remark.setRemark(request.getRemark());
        remarkRepository.save(remark);

        return toResponseWithDetails(application);
    }

    // ── OFFICER: Add a remark without making a decision ──────────────────────
    public RemarkResponse addRemark(Integer applicationId,
                                    RemarkRequest request,
                                    String officerEmail) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User officer = userRepository.findByEmail(officerEmail)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        // Verify assignment
        if (application.getOfficer() == null ||
                !application.getOfficer().getUserId().equals(officer.getUserId())) {
            throw new RuntimeException("This application is not assigned to you");
        }

        ApplicationRemark remark = new ApplicationRemark();
        remark.setApplication(application);
        remark.setOfficer(officer);
        remark.setRemark(request.getRemark());
        remarkRepository.save(remark);

        return toRemarkResponse(remark);
    }

    // ── OFFICER: Get all remarks added by me ──────────────────────────────────
    public List<RemarkResponse> getMyRemarks(String officerEmail) {
        User officer = userRepository.findByEmail(officerEmail)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        return remarkRepository.findByOfficer_UserIdOrderByAddedAtDesc(officer.getUserId())
                .stream()
                .map(this::toRemarkResponse)
                .collect(Collectors.toList());
    }

    // ── Helper: ApplicationResponse (basic - no documents/remarks) ───────────
    private ApplicationResponse toResponse(Application app) {
        ApplicationResponse res = new ApplicationResponse();
        res.setApplicationId(app.getApplicationId());
        res.setApplicantName(app.getApplicant().getFullName());
        res.setApplicantEmail(app.getApplicant().getEmail());
        res.setSchemeName(app.getScheme().getSchemeName());
        res.setOfficerName(app.getOfficer() != null ? app.getOfficer().getFullName() : "Not Assigned");
        res.setStatus(app.getStatus().name());
        res.setAppliedAt(app.getAppliedAt());
        res.setUpdatedAt(app.getUpdatedAt());
        return res;
    }

    // ── Helper: ApplicationResponse with documents and remarks ───────────────
    private ApplicationResponse toResponseWithDetails(Application app) {
        ApplicationResponse res = toResponse(app);

        // Attach documents
        List<DocumentResponse> docs = documentRepository
                .findByApplication_ApplicationId(app.getApplicationId())
                .stream()
                .map(this::toDocumentResponse)
                .collect(Collectors.toList());
        res.setDocuments(docs);

        // Attach remarks
        List<RemarkResponse> remarks = remarkRepository
                .findByApplication_ApplicationIdOrderByAddedAtAsc(app.getApplicationId())
                .stream()
                .map(this::toRemarkResponse)
                .collect(Collectors.toList());
        res.setRemarks(remarks);

        return res;
    }

    // ── Helper: Document entity → DocumentResponse ───────────────────────────
    private DocumentResponse toDocumentResponse(Document doc) {
        DocumentResponse res = new DocumentResponse();
        res.setDocumentId(doc.getDocumentId());
        res.setDocumentName(doc.getDocumentName());
        res.setFilePath(doc.getFilePath());
        res.setUploadedAt(doc.getUploadedAt());
        return res;
    }

    // ── Helper: Remark entity → RemarkResponse ───────────────────────────────
    private RemarkResponse toRemarkResponse(ApplicationRemark remark) {
        RemarkResponse res = new RemarkResponse();
        res.setRemarkId(remark.getRemarkId());
        res.setRemark(remark.getRemark());
        res.setOfficerName(remark.getOfficer().getFullName());
        res.setAddedAt(remark.getAddedAt());
        return res;
    }
}
