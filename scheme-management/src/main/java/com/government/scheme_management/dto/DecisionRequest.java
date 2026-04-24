package com.government.scheme_management.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DecisionRequest {
    @NotNull(message = "Decision is required")
    private String decision; // "APPROVED" or "REJECTED"

    @NotBlank(message = "Remark is required")
    private String remark;
}
