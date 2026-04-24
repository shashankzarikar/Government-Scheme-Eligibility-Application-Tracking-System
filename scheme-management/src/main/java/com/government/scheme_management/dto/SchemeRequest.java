package com.government.scheme_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SchemeRequest {

    @NotBlank(message = "Scheme name is required")
    private String schemeName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Eligibility criteria is required")
    private String eligibilityCriteria;

    @NotBlank(message = "Required documents is required")
    private String requiredDocuments;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean isActive = true;
}
