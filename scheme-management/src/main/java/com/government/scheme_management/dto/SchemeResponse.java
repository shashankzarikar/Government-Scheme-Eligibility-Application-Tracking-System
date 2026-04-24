package com.government.scheme_management.dto;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SchemeResponse {
    private Integer schemeId;
    private String schemeName;
    private String description;
    private String eligibilityCriteria;
    private String requiredDocuments;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String createdByName;
    private LocalDateTime createdAt;
}
