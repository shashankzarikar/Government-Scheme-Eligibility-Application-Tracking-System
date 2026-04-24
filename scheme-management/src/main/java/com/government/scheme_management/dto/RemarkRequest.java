package com.government.scheme_management.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RemarkRequest {
    @NotBlank(message = "Remark text is required")
    private String remark;
}
