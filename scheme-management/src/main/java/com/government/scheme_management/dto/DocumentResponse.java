package com.government.scheme_management.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponse {
    private Integer documentId;
    private String documentName;
    private String filePath;
    private LocalDateTime uploadedAt;
}
