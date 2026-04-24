package com.government.scheme_management.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotNull(message = "Scheme ID is required")
    private Integer schemeId;
}
