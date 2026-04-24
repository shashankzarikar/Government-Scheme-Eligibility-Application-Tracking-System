package com.government.scheme_management.service;

import com.government.scheme_management.dto.DocumentResponse;
import com.government.scheme_management.entity.Application;
import com.government.scheme_management.entity.Document;
import com.government.scheme_management.entity.User;
import com.government.scheme_management.repository.ApplicationRepository;
import com.government.scheme_management.repository.DocumentRepository;
import com.government.scheme_management.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    // Folder where uploaded files are stored on the server
    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    public record DocumentDownload(Resource resource, String fileName, String contentType) {}

    // Upload a document for an application
    public DocumentResponse uploadDocument(Integer applicationId,
                                           String documentName,
                                           MultipartFile file,
                                           String applicantEmail) throws IOException {

        if (documentName == null || documentName.isBlank()) {
            throw new RuntimeException("Document name is required");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please choose a file to upload");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        // Verify this application belongs to this applicant
        if (!application.getApplicant().getUserId().equals(applicant.getUserId())) {
            throw new RuntimeException("Access denied: This is not your application");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Path.of(UPLOAD_DIR + "/app_" + applicationId);
        Files.createDirectories(uploadPath);

        // Generate unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Save document record to database
        Document document = new Document();
        document.setApplication(application);
        document.setApplicant(applicant);
        document.setDocumentName(documentName);
        document.setFilePath(filePath.toString());

        Document saved = documentRepository.save(document);
        return toDocumentResponse(saved);
    }

    // Get all documents for an application
    public List<DocumentResponse> getDocumentsByApplication(Integer applicationId) {
        return documentRepository.findByApplication_ApplicationId(applicationId)
                .stream()
                .map(this::toDocumentResponse)
                .collect(Collectors.toList());
    }

    // Get all documents for an application, ensuring the applicant owns it
    public List<DocumentResponse> getDocumentsByApplication(Integer applicationId, String applicantEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        if (!application.getApplicant().getUserId().equals(applicant.getUserId())) {
            throw new RuntimeException("Access denied: This is not your application");
        }

        return documentRepository.findByApplication_ApplicationId(applicationId)
                .stream()
                .map(this::toDocumentResponse)
                .collect(Collectors.toList());
    }

    // Get documents for an application, ensuring the officer is assigned to it
    public List<DocumentResponse> getDocumentsByApplicationForOfficer(Integer applicationId, String officerEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));

        User officer = userRepository.findByEmail(officerEmail)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        if (application.getOfficer() == null || !application.getOfficer().getUserId().equals(officer.getUserId())) {
            throw new RuntimeException("Access denied: This application is not assigned to you");
        }

        return documentRepository.findByApplication_ApplicationId(applicationId)
                .stream()
                .map(this::toDocumentResponse)
                .collect(Collectors.toList());
    }

    // Download a document if the current user is allowed to access it
    public DocumentDownload downloadDocument(Integer documentId, String userEmail) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().getRoleName();
        boolean allowed = switch (role) {
            case "ADMIN" -> true;
            case "APPLICANT" -> document.getApplicant().getUserId().equals(user.getUserId());
            case "OFFICER" -> document.getApplication().getOfficer() != null
                    && document.getApplication().getOfficer().getUserId().equals(user.getUserId());
            default -> false;
        };

        if (!allowed) {
            throw new RuntimeException("Access denied: You cannot access this document");
        }

        Path filePath = Paths.get(document.getFilePath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found on server");
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String storedName = filePath.getFileName().toString();
        String extension = storedName.contains(".") ? storedName.substring(storedName.lastIndexOf('.')) : "";
        String safeName = document.getDocumentName().replaceAll("[^a-zA-Z0-9._-]+", "_").replaceAll("_+", "_");

        return new DocumentDownload(resource, safeName + extension, contentType);
    }

    // Delete a document
    public String deleteDocument(Integer documentId, String applicantEmail) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        // Verify ownership
        if (!document.getApplicant().getUserId().equals(applicant.getUserId())) {
            throw new RuntimeException("Access denied: This is not your document");
        }

        // Delete file from disk
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            // Log but don't fail - still remove DB record
            System.err.println("Could not delete file: " + document.getFilePath());
        }

        documentRepository.deleteById(documentId);
        return "Document deleted successfully";
    }

    // Helper: Document entity → DocumentResponse
    private DocumentResponse toDocumentResponse(Document doc) {
        DocumentResponse res = new DocumentResponse();
        res.setDocumentId(doc.getDocumentId());
        res.setDocumentName(doc.getDocumentName());
        res.setFilePath(doc.getFilePath());
        res.setUploadedAt(doc.getUploadedAt());
        return res;
    }
}
