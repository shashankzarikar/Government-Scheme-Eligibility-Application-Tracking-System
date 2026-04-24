package com.government.scheme_management.controller;

import com.government.scheme_management.dto.*;
import com.government.scheme_management.service.ApplicationService;
import com.government.scheme_management.service.DocumentService;
import com.government.scheme_management.service.SchemeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// All routes here require ROLE_APPLICANT (enforced in SecurityConfig)
@RestController
@RequestMapping("/api/applicant")
@CrossOrigin(origins = "*")
public class ApplicantController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private DocumentService documentService;

    // ── SCHEMES ───────────────────────────────────────────────────────────────

    // GET /api/applicant/schemes
    // Browse all active schemes available to apply for
    @GetMapping("/schemes")
    public ResponseEntity<List<SchemeResponse>> getActiveSchemes() {
        return ResponseEntity.ok(schemeService.getAllActiveSchemes());
    }

    // GET /api/applicant/schemes/{schemeId}
    // View full details of one scheme before applying
    @GetMapping("/schemes/{schemeId}")
    public ResponseEntity<SchemeResponse> getSchemeById(@PathVariable Integer schemeId) {
        return ResponseEntity.ok(schemeService.getSchemeById(schemeId));
    }

    // ── APPLICATIONS ─────────────────────────────────────────────────────────

    // POST /api/applicant/applications
    // Submit a new application for a scheme
    // Body: { schemeId }
    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> submitApplication(
            @Valid @RequestBody ApplicationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.submitApplication(request, authentication.getName()));
    }

    // GET /api/applicant/applications
    // Get all my submitted applications
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(authentication.getName()));
    }

    // GET /api/applicant/applications/{applicationId}
    // Get full tracking details of one application (includes remarks and documents)
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Integer applicationId,
            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.getApplicationById(applicationId, authentication.getName()));
    }

    // ── DOCUMENTS ─────────────────────────────────────────────────────────────

    // POST /api/applicant/applications/{applicationId}/documents
    // Upload a document for an application
    // Form data: documentName (text), file (multipart)
    @PostMapping(value = "/applications/{applicationId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Integer applicationId,
            @RequestParam("documentName") String documentName,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok(
                documentService.uploadDocument(applicationId, documentName, file, authentication.getName()));
    }

    // GET /api/applicant/applications/{applicationId}/documents
    // Get all documents uploaded for an application
    @GetMapping("/applications/{applicationId}/documents")
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @PathVariable Integer applicationId,
            Authentication authentication) {
        return ResponseEntity.ok(documentService.getDocumentsByApplication(applicationId, authentication.getName()));
    }

    // DELETE /api/applicant/documents/{documentId}
    // Delete a specific uploaded document
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Integer documentId,
            Authentication authentication) {
        return ResponseEntity.ok(documentService.deleteDocument(documentId, authentication.getName()));
    }
}
