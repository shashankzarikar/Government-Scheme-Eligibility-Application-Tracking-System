package com.government.scheme_management.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationResponse {
    private Integer applicationId;
    private String applicantName;
    private String applicantEmail;
    private String schemeName;
    private String officerName;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private List<DocumentResponse> documents;
    private List<RemarkResponse> remarks;
}
