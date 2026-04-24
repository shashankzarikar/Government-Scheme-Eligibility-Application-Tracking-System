package com.government.scheme_management.controller;

import com.government.scheme_management.dto.*;
import com.government.scheme_management.service.ApplicationService;
import com.government.scheme_management.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All routes here require ROLE_OFFICER (enforced in SecurityConfig)
@RestController
@RequestMapping("/api/officer")
@CrossOrigin(origins = "*")
public class OfficerController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private DocumentService documentService;

    // ── APPLICATIONS ─────────────────────────────────────────────────────────

    // GET /api/officer/applications
    // Returns all applications assigned to the logged-in officer
    // Includes documents and remarks for each
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAssignedApplications(
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getAssignedApplications(authentication.getName()));
    }

    // GET /api/officer/applications/{applicationId}
    // Get full details of one assigned application
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Integer applicationId,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(applicationId, authentication.getName()));
    }

    // GET /api/officer/applications/{applicationId}/documents
    // Returns documents only if the application is assigned to the logged-in officer
    @GetMapping("/applications/{applicationId}/documents")
    public ResponseEntity<List<DocumentResponse>> getApplicationDocuments(
            @PathVariable Integer applicationId,
            Authentication authentication) {
        return ResponseEntity.ok(
                documentService.getDocumentsByApplicationForOfficer(applicationId, authentication.getName()));
    }

    // ── DECISION ─────────────────────────────────────────────────────────────

    // PUT /api/officer/applications/{applicationId}/decision
    // Approve or reject an application with a mandatory remark
    // Body: { decision: "APPROVED" | "REJECTED", remark: "reason..." }
    @PutMapping("/applications/{applicationId}/decision")
    public ResponseEntity<ApplicationResponse> makeDecision(
            @PathVariable Integer applicationId,
            @Valid @RequestBody DecisionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.makeDecision(applicationId, request, authentication.getName()));
    }

    // ── REMARKS ──────────────────────────────────────────────────────────────

    // POST /api/officer/applications/{applicationId}/remarks
    // Add an observation remark without making a final decision
    // Body: { remark: "Aadhaar card is blurry, please reupload" }
    @PostMapping("/applications/{applicationId}/remarks")
    public ResponseEntity<RemarkResponse> addRemark(
            @PathVariable Integer applicationId,
            @Valid @RequestBody RemarkRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.addRemark(applicationId, request, authentication.getName()));
    }

    // GET /api/officer/remarks
    // Get all remarks added by the logged-in officer (remarks history page)
    @GetMapping("/remarks")
    public ResponseEntity<List<RemarkResponse>> getMyRemarks(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getMyRemarks(authentication.getName()));
    }
}
